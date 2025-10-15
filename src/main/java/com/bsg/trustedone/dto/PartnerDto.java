package com.bsg.trustedone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartnerDto {

    private Long partnerId;
    private String name;
    private CompanyDto company;
    private GroupDto group;
    private List<ExpertiseDto> expertises;
    private List<ContactMethodDto> contactMethods;
    private List<GainsProfileDto> gainsProfile;
    private List<BusinessProfileDto> businessProfile;

}
