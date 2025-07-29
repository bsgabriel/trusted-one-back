package com.bsg.trustedone.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfessionCreationDto {

    @NotBlank(message = "Profession/Especialization name is required")
    private String name;

    private Long parentProfessionId;
}
