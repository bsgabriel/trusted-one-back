package com.bsg.trustedone.mapper;

import com.bsg.trustedone.dto.ExpertiseCreationDto;
import com.bsg.trustedone.dto.AssignedExpertiseDto;
import com.bsg.trustedone.dto.ExpertiseListingDto;
import com.bsg.trustedone.entity.Expertise;
import com.bsg.trustedone.entity.PartnerExpertise;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ExpertiseMapper {

    public AssignedExpertiseDto toDto(Expertise expertise) {
        var optParent = Optional.ofNullable(expertise.getParentExpertise());
        return AssignedExpertiseDto.builder()
                .expertiseId(expertise.getExpertiseId())
                .name(expertise.getName())
                .parentExpertiseId(optParent.map(Expertise::getExpertiseId)
                        .orElse(null))
                .parentExpertiseName(optParent.map(Expertise::getName)
                        .orElse(null))
                .build();
    }

    public AssignedExpertiseDto toDto(PartnerExpertise partnerExpertise) {
        var optParent = Optional.ofNullable(partnerExpertise.getExpertise().getParentExpertise());
        return AssignedExpertiseDto.builder()
                .expertiseId(partnerExpertise.getExpertise().getExpertiseId())
                .name(partnerExpertise.getExpertise().getName())
                .parentExpertiseId(optParent.map(Expertise::getExpertiseId)
                        .orElse(null))
                .parentExpertiseName(optParent.map(Expertise::getName)
                        .orElse(null))
                .availableForReferral(partnerExpertise.isAvailableForReferral())
                .build();
    }

    public ExpertiseCreationDto toCreationDto(AssignedExpertiseDto expertise) {
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
