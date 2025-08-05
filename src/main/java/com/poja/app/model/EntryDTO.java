package com.poja.app.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** DTO mixte pour afficher soit un Donation soit un Help dans la même liste. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntryDTO {
  private String type; // "DONATION" ou "HELP"
  private String person; // nom complet + email
  private Long amount; // montant en centimes ou unité choisie
  private PaymentState paymentState;
  private Instant createdAt;
  private String noteOrDescription; // note pour donation ou description pour aide
  private String paymentMethod; // method de paiement
}
