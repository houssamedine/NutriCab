package com.bd.patientsmd.models.entites;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "consultations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Consultations extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patients patient;

    @Column(name = "consultation_date", nullable = false)
    private LocalDate consultationDate;

    @Column(name = "weight_kg", nullable = false)
    private Double weightKg;

    @Column(name = "waist_cm")
    private Double waistCm;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(columnDefinition = "TEXT")
    private String recommendations;
}
