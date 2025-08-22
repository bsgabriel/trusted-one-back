package com.bsg.trustedone.factory;

import com.bsg.trustedone.dto.ExpertiseCreationDto;
import com.bsg.trustedone.dto.ExpertiseDto;
import com.bsg.trustedone.dto.UserDto;
import com.bsg.trustedone.entity.Expertise;
import org.springframework.stereotype.Component;

@Component
public class ExpertiseFactory {

    public Expertise createEntity(ExpertiseCreationDto creationDto, UserDto user) {
        return Expertise.builder()
                .name(creationDto.getName())
                .parentExpertiseId(creationDto.getParentExpertiseId())
                .userId(user.getUserId())
                .build();
    }

    public Expertise createEntity(ExpertiseDto expertiseDto, UserDto user) {
        return Expertise.builder()
                .name(expertiseDto.getName())
                .parentExpertiseId(expertiseDto.getParentExpertiseId())
                .userId(user.getUserId())
                .expertiseId(expertiseDto.getExpertiseId())
                .build();
    }

}
