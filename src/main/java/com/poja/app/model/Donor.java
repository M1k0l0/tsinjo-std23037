package com.poja.app.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="donor")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Donor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String fullName;
}
