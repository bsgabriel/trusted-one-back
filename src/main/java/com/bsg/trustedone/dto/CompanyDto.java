package com.bsg.trustedone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDto {

    private Long companyId;
    private String name;
    private String image;

    @Builder.Default
    private List<CompanyPartnerDto> partners = new ArrayList<>();

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CompanyPartnerDto {
        private Long partnerId;
        private String name;
    }
}
