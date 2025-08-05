package com.poja.app.repository;

import com.poja.app.model.Donor;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonorRepository extends JpaRepository<Donor, Long> {
  Optional<Donor> findByEmail(String email);

  Optional<Donor> findByFullName(String fullName);
}
