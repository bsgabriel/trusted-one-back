package com.bsg.trustedone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartnerListingDto {

    private Long partnerId;
    private String name;
    private CompanyDto company;
    private GroupDto group;
    private PartnerMetricsDto metrics;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PartnerMetricsDto {
        private int pendingReferrals;
        private int rejectedReferrals;
        private int acceptedReferrals;
    }
}
