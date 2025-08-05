package com.poja.app.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="beneficiary")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Beneficiary {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String fullName;
}