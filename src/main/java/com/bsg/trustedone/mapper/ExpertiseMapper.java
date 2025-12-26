package com.bsg.trustedone.mapper;

import com.bsg.trustedone.dto.AssignedExpertiseDto;
import com.bsg.trustedone.dto.UserDto;
import com.bsg.trustedone.dto.expertise.*;
import com.bsg.trustedone.dto.expertise.form.ExpertiseFormDto;
import com.bsg.trustedone.dto.expertise.form.PartnerExpertiseFormDto;
import com.bsg.trustedone.dto.expertise.form.SpecializationFormDto;
import com.bsg.trustedone.entity.Expertise;
import com.bsg.trustedone.entity.Partner;
import com.bsg.trustedone.entity.PartnerExpertise;
import com.bsg.trustedone.projection.SpecializationListingProjection;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ExpertiseMapper {

    public Expertise expertiseFormToEntity(ExpertiseFormDto form, UserDto user) {
        var expertise = Expertise.builder()
                .name(form.getName())
                .userId(user.getUserId())
                .build();

        expertise.setSpecializations(form.getSpecializations()
                .stream()
                .map(e -> Expertise.builder()
                        .name(e.getName())
                        .parentExpertise(expertise)
                        .userId(user.getUserId())
                        .build())
                .toList());

        return expertise;
    }

    public ExpertiseDto entityToExpertiseDto(Expertise entity) {
        var expertise = ExpertiseDto.builder()
                .expertiseId(entity.getExpertiseId())
                .name(entity.getName())
                .build();

        expertise.setSpecializations(entity.getSpecializations()
                .stream()
                .map(e -> SpecializationDto.builder()
                        .parentExpertiseId(e.getParentExpertise().getExpertiseId())
                        .parentExpertiseName(e.getParentExpertise().getName())
                        .expertiseId(e.getExpertiseId())
                        .name(e.getName())
                        .build())
                .toList());

        return expertise;
    }

    public ExpertiseListingDto entityToListingDto(Expertise expertise) {
        return ExpertiseListingDto.builder()
                .expertiseId(expertise.getExpertiseId())
                .name(expertise.getName())
                .specializationCount(expertise.getSpecializations().size())
                .build();
    }

    public Expertise specializationToEntity(SpecializationDto specializationDto) {
        return Expertise.builder()
                .parentExpertise(Expertise.builder()
                        .expertiseId(specializationDto.getParentExpertiseId())
                        .build())
                .expertiseId(specializationDto.getExpertiseId())
                .name(specializationDto.getName())
                .build();
    }

    public SpecializationDto entityToSpecialization(Expertise entity) {
        return SpecializationDto.builder()
                .parentExpertiseId(entity.getParentExpertise().getExpertiseId())
                .parentExpertiseName(entity.getParentExpertise().getName())
                .expertiseId(entity.getExpertiseId())
                .name(entity.getName())
                .partners(entity.getPartnerExpertises()
                        .stream()
                        .map(partnerExpertise -> PartnerExpertiseDto.builder()
                                .partnerExpertiseId(partnerExpertise.getPartnerExpertiseId())
                                .partnerId(partnerExpertise.getPartner().getPartnerId())
                                .partnerName(partnerExpertise.getPartner().getName())
                                .availableForReferral(partnerExpertise.isAvailableForReferral())
                                .build())
                        .toList())
                .build();
    }

    public Expertise specializationFormToEntity(SpecializationFormDto form, UserDto user) {
        var specialization = Expertise.builder()
                .parentExpertise(Expertise.builder()
                        .expertiseId(form.getParentExpertiseId())
                        .build())
                .name(form.getName())
                .userId(user.getUserId())
                .build();

        specialization.setPartnerExpertises(form.getPartners()
                .stream()
                .map(partnerExpertise -> PartnerExpertise.builder()
                        .availableForReferral(partnerExpertise.isAvailableForReferral())
                        .expertise(specialization)
                        .partner(Partner.builder()
                                .partnerId(partnerExpertise.getPartnerId())
                                .build())
                        .build())
                .toList());
        return specialization;
    }

    public PartnerExpertise partnerExpertiseFormToEntity(PartnerExpertiseFormDto partnerExpertise) {
        return PartnerExpertise.builder()
                .availableForReferral(partnerExpertise.isAvailableForReferral())
                .partner(Partner.builder()
                        .partnerId(partnerExpertise.getPartnerId())
                        .build())
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

    public SpecializationFormDto toCreationDto(AssignedExpertiseDto expertise) {
        return SpecializationFormDto.builder()
                .name(expertise.getName())
                .parentExpertiseId(expertise.getParentExpertiseId())
                .build();
    }

    public SpecializationListingDto specializationListingProjectionToDto(SpecializationListingProjection projection) {
        return SpecializationListingDto.builder()
                .parentExpertiseId(projection.getParentExpertiseId())
                .expertiseId(projection.getExpertiseId())
                .name(projection.getName())
                .partnerCount(projection.getPartnerCount() == null ? 0 : projection.getPartnerCount())
                .build();
    }
}
