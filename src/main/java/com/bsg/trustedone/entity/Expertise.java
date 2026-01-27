package com.bsg.trustedone.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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
    private Long userId;

    @Builder.Default
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_expertise_id")
    private Expertise parentExpertise;

    @Builder.Default
    @OneToMany(mappedBy = "parentExpertise", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Expertise> specializations = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "expertise", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartnerExpertise> partnerExpertises = new ArrayList<>();
}