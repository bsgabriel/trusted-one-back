package com.bsg.trustedone.service;

import com.bsg.trustedone.dto.GroupCreationDto;
import com.bsg.trustedone.dto.GroupDto;
import com.bsg.trustedone.exception.GroupAlreadyExistsException;
import com.bsg.trustedone.factory.GroupFactory;
import com.bsg.trustedone.mapper.GroupMapper;
import com.bsg.trustedone.repository.GroupRepository;
import com.bsg.trustedone.validator.GroupValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return groupRepository.findByUserId(loggedUser.getUserId())
                .stream()
                .map(groupMapper::toDto)
                .toList();
    }

    public GroupDto createGroup(GroupCreationDto group) {
        group.setName(group.getName().trim());
        groupValidator.validateGroup(group);
        var loggedUser = userService.getLoggedUser();

        if (groupRepository.existsByNameAndUserId(group.getName(), loggedUser.getUserId())) {
            throw new GroupAlreadyExistsException("A group with this name already exists. Please choose a different name.");
        }

        var entity = groupFactory.createEntity(group, loggedUser);
        return groupMapper.toDto(groupRepository.save(entity));
    }

    public void deleteGroup(Long groupId) {
        groupRepository.deleteById(groupId);
    }


}
