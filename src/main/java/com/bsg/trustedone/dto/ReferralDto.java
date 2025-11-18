package com.bsg.trustedone.dto;

import com.bsg.trustedone.enums.ReferralStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReferralDto {

    private Long referralId;
    private String partnerName;
    private String expertise;
    private String specialization;
    private String referredTo;
    private ReferralStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
