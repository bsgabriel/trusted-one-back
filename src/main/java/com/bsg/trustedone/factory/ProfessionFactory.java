package com.bsg.trustedone.factory;

import com.bsg.trustedone.dto.ProfessionCreationDto;
import com.bsg.trustedone.dto.UserDto;
import com.bsg.trustedone.entity.Profession;
import org.springframework.stereotype.Component;

@Component
public class ProfessionFactory {

    public Profession createEntity(ProfessionCreationDto creationDto, UserDto user) {
        return Profession.builder()
                .name(creationDto.getName())
                .parentProfessionId(creationDto.getParentProfessionId())
                .userId(user.getUserId())
                .build();
    }

}
