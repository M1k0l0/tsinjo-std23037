package com.poja.app.model;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "help")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Help {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne private Beneficiary beneficiary;
  @OneToOne private Payment payment;
  private String description;
  private Instant createdAt = Instant.now();
}
