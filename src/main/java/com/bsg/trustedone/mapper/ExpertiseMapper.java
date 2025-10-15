package com.bsg.trustedone.mapper;

import com.bsg.trustedone.dto.ExpertiseCreationDto;
import com.bsg.trustedone.dto.ExpertiseDto;
import com.bsg.trustedone.dto.ExpertiseListingDto;
import com.bsg.trustedone.entity.Expertise;
import com.bsg.trustedone.entity.PartnerExpertise;
import org.springframework.stereotype.Component;

@Component
public class ExpertiseMapper {

    public ExpertiseDto toDto(Expertise expertise) {
        return ExpertiseDto.builder()
                .expertiseId(expertise.getExpertiseId())
                .name(expertise.getName())
                .parentExpertiseId(expertise.getParentExpertiseId())
                .build();
    }

    public ExpertiseDto toDto(PartnerExpertise partnerExpertise) {
        return ExpertiseDto.builder()
                .expertiseId(partnerExpertise.getExpertise().getExpertiseId())
                .name(partnerExpertise.getExpertise().getName())
                .parentExpertiseId(partnerExpertise.getExpertise().getParentExpertiseId())
                .availableForReferrals(partnerExpertise.isAvailableForReferrals())
                .build();
    }

    public ExpertiseCreationDto toCreationDto(ExpertiseDto expertise) {
        return ExpertiseCreationDto.builder()
                .name(expertise.getName())
                .parentExpertiseId(expertise.getParentExpertiseId())
                .build();
    }

    public ExpertiseListingDto toListingDto(Expertise expertise) {
        return ExpertiseListingDto.builder()
                .expertiseId(expertise.getExpertiseId())
                .parentExpertiseId(expertise.getParentExpertiseId())
                .name(expertise.getName())
                .build();
    }
}
