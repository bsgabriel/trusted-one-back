package com.bsg.trustedone.factory;

import com.bsg.trustedone.dto.GroupCreationDto;
import com.bsg.trustedone.dto.GroupDto;
import com.bsg.trustedone.dto.UserDto;
import com.bsg.trustedone.entity.Group;
import org.springframework.stereotype.Component;

@Component
public class GroupFactory {

    public Group createEntity(GroupCreationDto groupCreationDto, UserDto userDto) {
        return Group.builder()
                .userId(userDto.getUserId())
                .name(groupCreationDto.getName())
                .description(groupCreationDto.getDescription())
                .build();
    }

    public Group createEntity(GroupDto company, UserDto userDto) {
        return Group.builder()
                .groupId(company.getGroupId())
                .userId(userDto.getUserId())
                .name(company.getName())
                .description(company.getDescription())
                .build();
    }

}
