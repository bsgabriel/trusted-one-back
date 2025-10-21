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
                .parentExpertise(Expertise.builder()
                        .expertiseId(creationDto.getParentExpertiseId())
                        .build())
                .userId(user.getUserId())
                .build();
    }

    public Expertise createEntity(ExpertiseDto expertiseDto, UserDto user) {
        return Expertise.builder()
                .name(expertiseDto.getName())
                .parentExpertise(Expertise.builder()
                        .expertiseId(expertiseDto.getParentExpertiseId())
                        .build())
                .userId(user.getUserId())
                .expertiseId(expertiseDto.getExpertiseId())
                .build();
    }

}
