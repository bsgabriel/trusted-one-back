package com.bsg.trustedone.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "partner_expertises")
public class PartnerExpertise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long partnerExpertiseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false)
    private Partner partner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expertise_id", nullable = false)
    private Expertise expertise;

    private boolean availableForReferral = false;
}