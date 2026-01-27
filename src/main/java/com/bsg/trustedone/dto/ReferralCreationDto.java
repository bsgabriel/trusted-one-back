package com.bsg.trustedone.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReferralCreationDto {

    @NotNull(message = "{referral.validation.partnerId.required}")
    private Long partnerId;

    @NotNull(message = "{referral.validation.expertiseId.required}")
    private Long expertiseId;

    @NotNull(message = "{referral.validation.referredTo.required}")
    private String referredTo;
}
