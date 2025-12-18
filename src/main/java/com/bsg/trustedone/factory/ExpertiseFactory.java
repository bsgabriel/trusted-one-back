package com.bsg.trustedone.factory;

import com.bsg.trustedone.dto.ExpertiseCreationDto;
import com.bsg.trustedone.dto.AssignedExpertiseDto;
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

    public Expertise createEntity(AssignedExpertiseDto assignedExpertiseDto, UserDto user) {
        return Expertise.builder()
                .name(assignedExpertiseDto.getName())
                .parentExpertise(Expertise.builder()
                        .expertiseId(assignedExpertiseDto.getParentExpertiseId())
                        .build())
                .userId(user.getUserId())
                .expertiseId(assignedExpertiseDto.getExpertiseId())
                .build();
    }

}
