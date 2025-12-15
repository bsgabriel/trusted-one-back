package com.bsg.trustedone.mapper;

import com.bsg.trustedone.dto.GroupDto;
import com.bsg.trustedone.dto.GroupFormDto;
import com.bsg.trustedone.dto.GroupListingDto;
import com.bsg.trustedone.entity.Group;
import com.bsg.trustedone.projection.GroupListingProjection;
import com.bsg.trustedone.projection.GroupWithPartnersProjection;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
public class GroupMapper {

    public GroupDto toDto(Group entity) {
        return GroupDto.builder()
                .groupId(entity.getGroupId())
                .name(entity.getName())
                .description(entity.getDescription())
                .build();
    }

    public GroupDto toDto(List<GroupWithPartnersProjection> projections) {
        var first = projections.getFirst();

        return GroupDto.builder()
                .groupId(first.getGroupId())
                .name(first.getGroupName())
                .description(first.getGroupDescription())
                .partners(projections
                        .stream()
                        .filter(p -> p.getPartnerId() != null && isNotBlank(p.getPartnerName()))
                        .map(p -> GroupDto.GroupPartnerDto.builder()
                                .partnerId(p.getPartnerId())
                                .name(p.getPartnerName())
                                .build())
                        .toList())
                .build();
    }

    public GroupFormDto toCreationDto(GroupDto groupDto) {
        return GroupFormDto.builder()
                .name(groupDto.getName())
                .description(groupDto.getDescription())
                .build();
    }

    public GroupListingDto toListingDto(GroupListingProjection projection) {
        return GroupListingDto.builder()
                .groupId(projection.getGroupId())
                .name(projection.getName())
                .description(projection.getDescription())
                .partnerCount(projection.getPartnerCount() == null ? 0 : projection.getPartnerCount())
                .build();
    }
}
