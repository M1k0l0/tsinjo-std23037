package com.poja.app.repository;

import com.poja.app.model.Payment;
import com.poja.app.model.PaymentState;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
  Optional<Payment> findByReference(String reference);

  List<Payment> findByState(PaymentState state);
}
