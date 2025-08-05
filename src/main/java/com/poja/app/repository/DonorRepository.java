package com.poja.app.repository;

import com.poja.app.model.Donor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DonorRepository extends JpaRepository<Donor, Long> {
    Optional<Donor> findByEmail(String email);
    Optional<Donor> findByFullName(String fullName);
}