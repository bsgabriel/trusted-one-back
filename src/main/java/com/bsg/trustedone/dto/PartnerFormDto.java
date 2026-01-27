package com.bsg.trustedone.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartnerFormDto {

    @NotBlank(message = "{partner.validation.name.required}")
    private String name;

    private CompanyDto company;
    private GroupDto group;

    @NotEmpty(message = "{partner.validation.expertises.not-empty}")
    private List<AssignedExpertiseDto> expertises;

    @NotEmpty(message = "{partner.validation.contactMethods.not-empty}")
    @Valid
    private List<ContactMethodFormDto> contactMethods;

    private List<GainsProfileDto> gainsProfile;
    private List<BusinessProfileDto> businessProfile;

}
