package com.bsg.trustedone.dto.expertise.form;

import jakarta.validation.constraints.NotBlank;
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
public class ExpertiseFormDto {

    @NotBlank(message = "{expertise.validation.name.required}")
    private String name;

    @Builder.Default
    private List<SpecializationFormDto> specializations = new ArrayList<>();
}
