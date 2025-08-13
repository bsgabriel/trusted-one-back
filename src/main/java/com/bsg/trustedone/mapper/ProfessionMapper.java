package com.bsg.trustedone.mapper;

import com.bsg.trustedone.dto.ProfessionCreationDto;
import com.bsg.trustedone.dto.ProfessionDto;
import com.bsg.trustedone.entity.Profession;
import com.bsg.trustedone.entity.ProfessionalProfession;
import org.springframework.stereotype.Component;

@Component
public class ProfessionMapper {

    public ProfessionDto toDto(Profession profession) {
        return ProfessionDto.builder()
                .professionId(profession.getProfessionId())
                .name(profession.getName())
                .parentProfessionId(profession.getParentProfessionId())
                .build();
    }

    public ProfessionDto toDto(ProfessionalProfession professionalProfession) {
        return ProfessionDto.builder()
                .professionId(professionalProfession.getProfession().getProfessionId())
                .name(professionalProfession.getProfession().getName())
                .parentProfessionId(professionalProfession.getProfession().getParentProfessionId())
                .availableForReferrals(professionalProfession.isAvailableForReferrals())
                .build();
    }

    public ProfessionCreationDto toCreationDto(ProfessionDto profession) {
        return ProfessionCreationDto.builder()
                .name(profession.getName())
                .parentProfessionId(profession.getParentProfessionId())
                .build();
    }
}
