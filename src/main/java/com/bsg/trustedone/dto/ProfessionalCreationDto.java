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
public class ProfessionalCreationDto {

    @NotBlank(message = "Professional name is required")
    private String name;

    private CompanyDto company;
    private GroupDto group;
    private List<ProfessionDto> professions;

}
