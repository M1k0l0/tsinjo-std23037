package com.poja.app.service;

import com.poja.app.model.Payment;
import com.poja.app.model.PaymentState;
import com.poja.app.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RestTemplate volaRestTemplate;
    private final String volaBaseUrl = System.getProperty("vola.base-url", "https://42cwka3n4ifcp7ufheyrpmph240iuaxo.lambda-url.eu-west-3.on.aws/v3/api-docs");

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);
    private final ConcurrentMap<String, ScheduledFuture<?>> futures = new ConcurrentHashMap<>();
    private final Logger log = LoggerFactory.getLogger(PaymentService.class);

    // configurable
    private final int initialDelaySec = 3;
    private final int pollingIntervalSec = 5;
    private final int maxAttempts = 30;

    public Payment createAndSubmit(Payment p) {
        p.setState(PaymentState.VERIFYING);
        p = paymentRepository.save(p);

        // Prépare payload
        Map<String, Object> body = Map.of(
                "amount", p.getAmount(),
                "method", p.getMethod(),
                "localId", p.getId()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            // POST to Vola
            ResponseEntity<Map> resp = volaRestTemplate.postForEntity(
                    volaBaseUrl + "/payments", request, Map.class);

            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                Object idObj = resp.getBody().get("id");
                String reference = Objects.toString(idObj, null);
                if (reference != null) {
                    p.setReference(reference);
                    paymentRepository.save(p);
                    startPolling(reference, p.getId());
                    log.info("Payment submitted to Vola: localId={}, externalId={}", p.getId(), reference);
                } else {
                    log.warn("Vola returned no id for localId={}", p.getId());
                }
            } else {
                log.warn("Vola POST non-2xx: status={}, body={}", resp.getStatusCode(), resp.getBody());
            }
        } catch (RestClientException ex) {
            log.error("Erreur lors de la soumission vers Vola pour localId={}", p.getId(), ex);
        }

        return p;
    }

    private void startPolling(String externalId, Long paymentId) {
        if (futures.containsKey(externalId)) {
            log.debug("Polling déjà en cours pour externalId={}", externalId);
            return;
        }

        AtomicInteger attempts = new AtomicInteger(0);

        Runnable task = () -> {
            int attempt = attempts.incrementAndGet();
            try {
                // GET status
                ResponseEntity<Map> resp = volaRestTemplate.getForEntity(volaBaseUrl + "/payments/{id}", Map.class, externalId);

                if (!resp.getStatusCode().is2xxSuccessful()) {
                    log.warn("Polling non-2xx for externalId={}, status={}", externalId, resp.getStatusCode());
                    if (attempt >= maxAttempts) cancelPolling(externalId);
                    return;
                }

                Map body = resp.getBody();
                if (body == null) {
                    log.warn("Polling empty body for externalId={} attempt={}", externalId, attempt);
                    if (attempt >= maxAttempts) cancelPolling(externalId);
                    return;
                }

                String statusRaw = Optional.ofNullable(body.get("status")).map(Object::toString).orElse("UNKNOWN");
                String status = statusRaw.toUpperCase();
                log.debug("Polling Vola: externalId={}, attempt={}, status={}", externalId, attempt, status);

                if ("SUCCEEDED".equals(status) || "FAILED".equals(status) || attempt >= maxAttempts) {
                    // Update DB
                    Payment payment = paymentRepository.findById(paymentId).orElse(null);
                    if (payment != null) {
                        try {
                            payment.setState(PaymentState.valueOf(status));
                        } catch (IllegalArgumentException e) {
                            payment.setState(PaymentState.FAILED);
                        }
                        paymentRepository.save(payment);
                        log.info("Payment updated from polling: localId={}, externalId={}, newState={}",
                                payment.getId(), payment.getReference(), payment.getState());
                    } else {
                        log.warn("Payment not found localId={} when polling externalId={}", paymentId, externalId);
                    }
                    cancelPolling(externalId);
                }

            } catch (Exception e) {
                log.warn("Erreur polling for externalId={} attempt={} : {}", externalId, attempts.get(), e.toString());
                if (attempt >= maxAttempts) {
                    // marque failed et annule
                    Payment payment = paymentRepository.findById(paymentId).orElse(null);
                    if (payment != null) {
                        payment.setState(PaymentState.FAILED);
                        paymentRepository.save(payment);
                    }
                    cancelPolling(externalId);
                }
            }
        };

        ScheduledFuture<?> future = executor.scheduleAtFixedRate(task, initialDelaySec, pollingIntervalSec, TimeUnit.SECONDS);
        futures.put(externalId, future);
    }

    private void cancelPolling(String externalId) {
        ScheduledFuture<?> f = futures.remove(externalId);
        if (f != null) {
            f.cancel(false);
            log.debug("Polling cancelled for externalId={}", externalId);
        }
    }

}
