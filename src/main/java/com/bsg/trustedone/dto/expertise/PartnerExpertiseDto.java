package com.bsg.trustedone.dto.expertise;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartnerExpertiseDto {
    private Long partnerExpertiseId;
    private Long partnerId;
    private String partnerName;
    private boolean availableForReferral;
}
