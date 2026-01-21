package com.bsg.trustedone.service;

import com.bsg.trustedone.dto.GroupDto;
import com.bsg.trustedone.dto.GroupFormDto;
import com.bsg.trustedone.dto.GroupListingDto;
import com.bsg.trustedone.dto.PageResponse;
import com.bsg.trustedone.exception.ResourceAlreadyExistsException;
import com.bsg.trustedone.exception.ResourceNotFoundException;
import com.bsg.trustedone.factory.GroupFactory;
import com.bsg.trustedone.mapper.GroupMapper;
import com.bsg.trustedone.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupMapper groupMapper;
    private final UserService userService;
    private final GroupFactory groupFactory;
    private final MessageService messageService;
    private final GroupRepository groupRepository;
    private final ObjectProvider<PartnerService> partnerServiceProvider;

    public PageResponse<GroupListingDto> listGroups(String search, Pageable pageable) {
        var loggedUser = userService.getLoggedUser();
        var sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("name").ascending());
        var searchParam = StringUtils.isBlank(search) ? null : search.trim();

        var page = groupRepository.listGroups(loggedUser.getUserId(), searchParam, sortedPageable);
        return PageResponse.from(page.map(groupMapper::toListingDto));
    }

    @Transactional
    public GroupDto createGroup(GroupFormDto group) {
        group.setName(group.getName().trim());
        var loggedUser = userService.getLoggedUser();

        if (groupRepository.existsByNameAndUserId(group.getName(), loggedUser.getUserId())) {
            throw new ResourceAlreadyExistsException(messageService.getMessage("error.title.create-resource"), messageService.getMessage("group.error.already-exists"));
        }

        var entity = groupRepository.save(groupFactory.createEntity(group, loggedUser));
        partnerServiceProvider.getObject().addPartnersToGroup(group.getPartners(), entity.getGroupId());
        return groupMapper.toDto(entity);
    }

    @Transactional
    public void deleteGroup(Long groupId) {
        var loggedUserId = userService.getLoggedUser().getUserId();
        partnerServiceProvider.getObject().removePartnersFromGroup(groupId);
        groupRepository.deleteByGroupIdAndUserId(groupId, loggedUserId);
    }

    @Transactional
    public GroupDto updateGroup(GroupFormDto request, Long groupId) {
        var group = groupRepository.findByGroupIdAndUserId(groupId, userService.getLoggedUser().getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(messageService.getMessage("error.title.update-resource"), messageService.getMessage("group.error.not-found")));

        group.setName(request.getName());
        group.setDescription(request.getDescription());
        this.syncPartnersWithGroup(groupId, request.getPartners());
        return groupMapper.toDto(groupRepository.save(group));
    }

    public GroupDto findOrCreateGroup(GroupDto group) {
        if (isNull(group)) {
            return null;
        }

        if (isNull(group.getGroupId())) {
            return this.createGroup(groupMapper.toCreationDto(group));
        }

        return this.groupRepository.findByGroupIdAndUserId(group.getGroupId(), userService.getLoggedUser().getUserId())
                .map(groupMapper::toDto)
                .orElseGet(() -> this.createGroup(groupMapper.toCreationDto(group)));
    }

    public GroupDto findById(Long groupId) {
        var groupProjections = groupRepository.findGroupWithPartners(groupId, userService.getLoggedUser().getUserId());

        if (CollectionUtils.isEmpty(groupProjections)) {
            throw new ResourceNotFoundException(messageService.getMessage("error.title.fetch-resource"), messageService.getMessage("group.error.not-found"));
        }

        return groupMapper.toDto(groupProjections);
    }

    private void syncPartnersWithGroup(Long groupId, List<Long> partnerIds) {
        partnerServiceProvider.getObject().removePartnersFromGroup(groupId);

        if (!partnerIds.isEmpty()) {
            partnerServiceProvider.getObject().addPartnersToGroup(partnerIds, groupId);
        }
    }

}
