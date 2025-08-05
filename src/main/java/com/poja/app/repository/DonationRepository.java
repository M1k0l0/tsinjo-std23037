package com.poja.app.repository;

import com.poja.app.model.Donation;
import com.poja.app.model.Donor;
import com.poja.app.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DonationRepository extends JpaRepository<Donation, Long> {
    List<Donation> findAllByOrderByCreatedAtDesc();
    List<Donation> findByDonor(Donor donor);
    List<Donation> findByPayment(Payment payment);
}