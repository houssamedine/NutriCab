package com.bd.patientsmd.models.entites;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "patients")
@AllArgsConstructor @NoArgsConstructor @Setter @Getter @Builder
public class Patients {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private LocalDate birthDate;
    private String phone;
    private Double heightCm;
    private Double initialWeightKg;
    private String objective;
}
