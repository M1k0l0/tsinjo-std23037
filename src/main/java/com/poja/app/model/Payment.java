package com.poja.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name="payment")
@Data @NoArgsConstructor @AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String reference;
    private Long amount;
    private String method;
    private Instant createdAt = Instant.now();
    @Enumerated(EnumType.STRING)
    private PaymentState state = PaymentState.VERIFYING;
}