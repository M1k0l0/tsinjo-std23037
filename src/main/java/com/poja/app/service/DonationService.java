package com.poja.app.service;

import com.poja.app.model.Donation;
import com.poja.app.model.Donor;
import com.poja.app.model.Payment;
import com.poja.app.model.PaymentState;
import com.poja.app.repository.DonationRepository;
import com.poja.app.repository.DonorRepository;
import com.poja.app.repository.PaymentRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DonationService {

  private final DonorRepository donorRepository;
  private final DonationRepository donationRepository;
  private final PaymentRepository paymentRepository;
  private final PaymentService paymentService; // service qui fait createAndSubmit(...)
  private final Logger log = LoggerFactory.getLogger(DonationService.class);

  @Transactional
  public Donation submitDonation(
      String donorEmail, String donorFullName, Long amount, String method, String note) {
    // 1. find or create donor
    Donor donor =
        donorRepository
            .findByEmail(donorEmail)
            .orElseGet(
                () -> {
                  Donor d = new Donor();
                  d.setEmail(donorEmail);
                  d.setFullName(donorFullName);
                  log.info("Creating new donor: {}", donorEmail);
                  return donorRepository.save(d);
                });

    // 2. create payment with VERIFYING state
    Payment payment = new Payment();
    payment.setAmount(amount);
    payment.setMethod(method);
    payment.setCreatedAt(Instant.now());
    payment.setState(PaymentState.VERIFYING);
    payment = paymentRepository.save(payment);

    // 3. create donation
    Donation donation = new Donation();
    donation.setDonor(donor);
    donation.setPayment(payment);
    donation.setCreatedAt(Instant.now());
    donation = donationRepository.save(donation);

    log.info(
        "Donation created (local): donationId={}, donor={}, amount={}",
        donation.getId(),
        donor.getEmail(),
        amount);

    // 4. submit to external Vola asynchronously via PaymentService
    try {
      paymentService.createAndSubmit(payment);
    } catch (Exception e) {
      // PaymentService should handle async errors, but log here too
      log.error(
          "Failed to submit payment to Vola for paymentId={} donationId={}",
          payment.getId(),
          donation.getId(),
          e);
    }

    return donation;
  }
}
