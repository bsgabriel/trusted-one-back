package com.bsg.trustedone.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "expertises")
public class Expertise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long expertiseId;

    private String name;
    private Long parentExpertiseId;
    private Long userId;

    @OneToMany(mappedBy = "parentExpertiseId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Expertise> specializations;

    @OneToMany(mappedBy = "expertise", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartnerExpertise> partnerExpertises;

}