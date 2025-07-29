package com.bsg.trustedone.mapper;

import com.bsg.trustedone.dto.ProfessionDto;
import com.bsg.trustedone.entity.Profession;
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

}
