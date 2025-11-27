package com.bsg.trustedone.mapper;

import com.bsg.trustedone.dto.ReferralCreationDto;
import com.bsg.trustedone.dto.ReferralDto;
import com.bsg.trustedone.dto.ReferralStatsDto;
import com.bsg.trustedone.dto.UserDto;
import com.bsg.trustedone.entity.Expertise;
import com.bsg.trustedone.entity.Partner;
import com.bsg.trustedone.entity.Referral;
import com.bsg.trustedone.enums.ReferralStatus;
import com.bsg.trustedone.projection.ReferralStatsProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ReferralMapper {

    public Referral toEntity(ReferralCreationDto dto, UserDto userDto) {
        var currentTime = LocalDateTime.now();
        return Referral.builder()
                .partner(Partner.builder()
                        .partnerId(dto.getPartnerId())
                        .build())
                .expertise(Expertise.builder()
                        .expertiseId(dto.getExpertiseId())
                        .build())
                .referredTo(dto.getReferredTo())
                .userId(userDto.getUserId())
                .status(ReferralStatus.PENDING)
                .createdAt(currentTime)
                .updatedAt(currentTime)
                .build();
    }

    public ReferralDto toDto(Referral entity) {
        var containsParent = Objects.nonNull(entity.getExpertise().getParentExpertise());
        return ReferralDto.builder()
                .referralId(entity.getReferralId())
                .referredTo(entity.getReferredTo())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .partnerName(entity.getPartner().getName())
                .expertise(containsParent ? entity.getExpertise().getParentExpertise().getName() : entity.getExpertise().getName())
                .specialization(containsParent ? entity.getExpertise().getName() : null)
                .status(entity.getStatus())
                .build();
    }

    public ReferralStatsDto toReferralStats(ReferralStatsProjection projection) {
        return ReferralStatsDto.builder()
                .total(safeLongToInt(projection.getTotal()))
                .accepted(safeLongToInt(projection.getAccepted()))
                .declined(safeLongToInt(projection.getDeclined()))
                .pending(safeLongToInt(projection.getPending()))
                .currentMonth(ReferralStatsDto.MonthlyStatsDto.builder()
                        .created(safeLongToInt(projection.getCurrentMonthCreated()))
                        .responses(safeLongToInt(projection.getCurrentMonthResponses()))
                        .accepted(safeLongToInt(projection.getCurrentMonthAccepted()))
                        .declined(safeLongToInt(projection.getCurrentMonthDeclined()))
                        .build())
                .previousMonth(ReferralStatsDto.MonthlyStatsDto.builder()
                        .created(safeLongToInt(projection.getPreviousMonthCreated()))
                        .responses(safeLongToInt(projection.getPreviousMonthResponses()))
                        .accepted(safeLongToInt(projection.getPreviousMonthAccepted()))
                        .declined(safeLongToInt(projection.getPreviousMonthDeclined()))
                        .build())
                .build();
    }

    private int safeLongToInt(Long val) {
        return val == null ? 0 : val.intValue();
    }


}
