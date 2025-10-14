package com.bsg.trustedone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpertiseDto {

    private Long parentExpertiseId;
    private Long expertiseId;
    private String name;
    private String parentExpertiseName;
    private boolean availableForReferrals;

}
