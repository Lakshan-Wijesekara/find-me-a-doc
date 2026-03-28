package com.findmeadoc.domain.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "doctors")
@Data
@NoArgsConstructor
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(nullable = false)
    private String specialization;

    @Column(name = "consultation_fee", nullable = false)
    private Double consultationFee;

    @Column(name = "is_verified")
    private Boolean isVerified = false;
}
