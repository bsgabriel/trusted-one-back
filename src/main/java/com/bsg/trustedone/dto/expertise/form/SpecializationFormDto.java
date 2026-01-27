package com.bsg.trustedone.dto.expertise.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class SpecializationFormDto {

    @NotBlank(message = "{specialization.validation.name.required}")
    private String name;

    @NotNull(message = "{specialization.parentExpertiseId.required}")
    private Long parentExpertiseId;

    @Builder.Default
    private List<PartnerExpertiseFormDto> partners = new ArrayList<>();
}