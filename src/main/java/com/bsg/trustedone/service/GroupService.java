package com.bsg.trustedone.service;

import com.bsg.trustedone.dto.GroupCreationDto;
import com.bsg.trustedone.dto.GroupDto;
import com.bsg.trustedone.dto.GroupListingDto;
import com.bsg.trustedone.dto.PageResponse;
import com.bsg.trustedone.exception.ResourceAlreadyExistsException;
import com.bsg.trustedone.exception.ResourceNotFoundException;
import com.bsg.trustedone.exception.UnauthorizedAccessException;
import com.bsg.trustedone.factory.GroupFactory;
import com.bsg.trustedone.mapper.GroupMapper;
import com.bsg.trustedone.repository.GroupRepository;
import com.bsg.trustedone.validator.GroupValidator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupMapper groupMapper;
    private final UserService userService;
    private final GroupFactory groupFactory;
    private final GroupValidator groupValidator;
    private final GroupRepository groupRepository;

    public List<GroupDto> getAllGroups() {
        var loggedUser = userService.getLoggedUser();
        return groupRepository.findAllByUserIdOrderByName(loggedUser.getUserId())
                .stream()
                .map(groupMapper::toDto)
                .toList();
    }

    public GroupDto createGroup(GroupCreationDto group) {
        group.setName(group.getName().trim());
        groupValidator.validateGroupCreate(group);
        var loggedUser = userService.getLoggedUser();

        if (groupRepository.existsByNameAndUserId(group.getName(), loggedUser.getUserId())) {
            throw new ResourceAlreadyExistsException("A group with this name already exists. Please choose a different name.");
        }

        var entity = groupFactory.createEntity(group, loggedUser);
        return groupMapper.toDto(groupRepository.save(entity));
    }

    public void deleteGroup(Long groupId) {
        var opt = groupRepository.findById(groupId);

        if (opt.isEmpty()) {
            return;
        }

        var loggedUser = userService.getLoggedUser();
        var group = opt.get();

        if (!group.getUserId().equals(loggedUser.getUserId())) {
            throw new UnauthorizedAccessException("An error occurred while deleting group");
        }

        groupRepository.deleteById(groupId);
    }

    public GroupDto updateGroup(GroupCreationDto request, Long groupId) {
        groupValidator.validateGroupUpdate(request);

        var group = groupRepository.findById(groupId).orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        if (!group.getUserId().equals(userService.getLoggedUser().getUserId())) {
            throw new UnauthorizedAccessException("An error occurred while updating group");
        }

        group.setName(request.getName());
        group.setDescription(request.getDescription());
        return groupMapper.toDto(groupRepository.save(group));
    }

    public GroupDto findOrCreateGroup(GroupDto group) {
        if (isNull(group)) {
            return null;
        }

        if (isNull(group.getGroupId())) {
            return this.createGroup(groupMapper.toCreationDto(group));
        }

        return this.groupRepository.findById(group.getGroupId())
                .map(groupMapper::toDto)
                .orElseGet(() -> this.createGroup(groupMapper.toCreationDto(group)));
    }

    public PageResponse<GroupListingDto> listGroups(String search, Pageable pageable) {
        var loggedUser = userService.getLoggedUser();
        var sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("name").ascending());
        var searchParam = StringUtils.isBlank(search) ? null : search.trim();

        var page = groupRepository.listGroups(loggedUser.getUserId(), searchParam, sortedPageable);
        return PageResponse.from(page.map(groupMapper::toListingDto));
    }

    public GroupDto findById(Long groupId) {
        var groupProjections = groupRepository.findGroupWithPartners(groupId);

        if (CollectionUtils.isEmpty(groupProjections)) {
            throw new ResourceNotFoundException("Group not found");
        }

        return groupMapper.toDto(groupProjections);
    }
}
