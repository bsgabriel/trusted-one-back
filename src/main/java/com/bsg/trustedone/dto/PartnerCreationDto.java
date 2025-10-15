package com.bsg.trustedone.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartnerCreationDto {

    @NotBlank(message = "Partner name is required")
    private String name;

    private CompanyDto company;
    private GroupDto group;
    private List<ExpertiseDto> expertises;
    private List<ContactMethodCreationDto> contactMethods;
    private List<GainsProfileDto> gainsProfile;
    private List<BusinessProfileDto> businessProfile;

}
