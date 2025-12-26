package com.bsg.trustedone.dto.expertise;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpecializationListingDto {
    private Long parentExpertiseId;
    private Long expertiseId;
    private String name;
    private int partnerCount;
}
