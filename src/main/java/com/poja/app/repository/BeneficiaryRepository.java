package com.poja.app.repository;

import com.poja.app.model.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {
    Optional<Beneficiary> findByEmail(String email);
    Optional<Beneficiary> findByFullName(String fullName);
}
