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
@Table(name = "professions")
public class Profession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long professionId;

    private String name;
    private Long parentProfessionId;
    private Long userId;

    @OneToMany(mappedBy = "parentProfessionId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Profession> specializations;
}