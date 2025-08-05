package com.poja.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name="donation")
@Data
@NoArgsConstructor @AllArgsConstructor
public class Donation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne private Donor donor;
    @OneToOne private Payment payment;
    private String note;
    private Instant createdAt = Instant.now();
}
