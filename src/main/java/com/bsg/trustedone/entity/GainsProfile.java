package com.bsg.trustedone.entity;

import com.bsg.trustedone.enums.GainsCategory;
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
@Table(name = "gains_profile")
public class GainsProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gainsProfileId;

    private Long partnerId;
    private GainsCategory type;
    private String info;
}
