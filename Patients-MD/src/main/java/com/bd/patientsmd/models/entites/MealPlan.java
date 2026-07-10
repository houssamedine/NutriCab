package com.bd.patientsmd.models.entites;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "meal_plans")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class MealPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patients patient;

    @Column(nullable = false)
    private String title;

    private String objective;

    private Integer calories;

    @Column(columnDefinition = "LONGTEXT")
    private String content;
}
