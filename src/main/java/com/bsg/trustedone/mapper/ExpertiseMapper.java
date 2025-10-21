package com.bsg.trustedone.mapper;

import com.bsg.trustedone.dto.ExpertiseCreationDto;
import com.bsg.trustedone.dto.ExpertiseDto;
import com.bsg.trustedone.dto.ExpertiseListingDto;
import com.bsg.trustedone.entity.Expertise;
import com.bsg.trustedone.entity.PartnerExpertise;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class ExpertiseMapper {

    public ExpertiseDto toDto(Expertise expertise) {
        var optParent = Optional.ofNullable(expertise.getParentExpertise());
        return ExpertiseDto.builder()
                .expertiseId(expertise.getExpertiseId())
                .name(expertise.getName())
                .parentExpertiseId(optParent.map(Expertise::getExpertiseId)
                        .orElse(null))
                .parentExpertiseName(optParent.map(Expertise::getName)
                        .orElse(null))
                .build();
    }

    public ExpertiseDto toDto(PartnerExpertise partnerExpertise) {
        var optParent = Optional.ofNullable(partnerExpertise.getExpertise().getParentExpertise());
        return ExpertiseDto.builder()
                .expertiseId(partnerExpertise.getExpertise().getExpertiseId())
                .name(partnerExpertise.getExpertise().getName())
                .parentExpertiseId(optParent.map(Expertise::getExpertiseId)
                        .orElse(null))
                .parentExpertiseName(optParent.map(Expertise::getName)
                        .orElse(null))
                .availableForReferral(partnerExpertise.isAvailableForReferral())
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
                .parentExpertiseId(Optional.ofNullable(expertise.getParentExpertise())
                        .map(Expertise::getExpertiseId)
                        .orElse(null))
                .name(expertise.getName())
                .build();
    }
}
