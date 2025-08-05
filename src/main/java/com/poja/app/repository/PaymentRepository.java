package com.poja.app.repository;

import com.poja.app.model.Payment;
import com.poja.app.model.PaymentState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByReference(String reference);
    List<Payment> findByState(PaymentState state);
}

