package com.poja.app.repository;

import com.poja.app.model.Beneficiary;
import com.poja.app.model.Help;
import com.poja.app.model.Payment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HelpRepository extends JpaRepository<Help, Integer> {
  List<Help> findByPayment(Payment payment);

  List<Help> findByBeneficiary(Beneficiary beneficiary);

  List<Help> findAllByOrderByCreatedAtDesc();
}
