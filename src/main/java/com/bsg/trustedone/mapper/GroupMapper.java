package com.bsg.trustedone.mapper;

import com.bsg.trustedone.dto.GroupCreationDto;
import com.bsg.trustedone.dto.GroupDto;
import com.bsg.trustedone.entity.Group;
import org.springframework.stereotype.Component;

@Component
public class GroupMapper {

    public GroupDto toDto(Group entity) {
        return GroupDto.builder()
                .groupId(entity.getGroupId())
                .name(entity.getName())
                .description(entity.getDescription())
                .build();
    }

    public GroupCreationDto toCreationDto(GroupDto groupDto) {
        return GroupCreationDto.builder()
                .name(groupDto.getName())
                .description(groupDto.getDescription())
                .build();
    }
}
