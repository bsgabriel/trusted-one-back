package com.bsg.trustedone.factory;

import com.bsg.trustedone.dto.GroupCreationDto;
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

}
