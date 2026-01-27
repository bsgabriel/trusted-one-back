package com.bsg.trustedone.dto.expertise.form;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartnerExpertiseFormDto {

    @NotNull
    private Long partnerId;

    private boolean availableForReferral;
}