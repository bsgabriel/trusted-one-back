package com.bsg.trustedone.service;

import com.bsg.trustedone.dto.GroupDto;
import com.bsg.trustedone.dto.GroupFormDto;
import com.bsg.trustedone.dto.GroupListingDto;
import com.bsg.trustedone.dto.UserDto;
import com.bsg.trustedone.entity.Group;
import com.bsg.trustedone.exception.ResourceAlreadyExistsException;
import com.bsg.trustedone.exception.ResourceNotFoundException;
import com.bsg.trustedone.factory.GroupFactory;
import com.bsg.trustedone.helper.DummyObjects;
import com.bsg.trustedone.helper.RandomUtils;
import com.bsg.trustedone.mapper.GroupMapper;
import com.bsg.trustedone.projection.GroupListingProjection;
import com.bsg.trustedone.projection.GroupWithPartnersProjection;
import com.bsg.trustedone.repository.GroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @InjectMocks
    private GroupService groupService;

    @Mock
    private GroupMapper groupMapper;

    @Mock
    private UserService userService;

    @Mock
    private GroupFactory groupFactory;

    @Mock
    private MessageService messageService;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private ObjectProvider<PartnerService> partnerServiceProvider;

    @Mock
    private PartnerService partnerService;

    @Test
    @DisplayName("Should create group successfully")
    void createGroup_withValidData_shouldCreateAndReturnGroupDto() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        var partners = List.of(1L, 2L, 3L);
        var formDto = DummyObjects.newInstance(GroupFormDto.class);
        formDto.setPartners(partners);

        var entity = DummyObjects.newInstance(Group.class);
        var dto = DummyObjects.newInstance(GroupDto.class);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(groupRepository.existsByNameAndUserId(formDto.getName(), loggedUser.getUserId())).thenReturn(false);
        when(groupFactory.createEntity(formDto, loggedUser)).thenReturn(entity);
        when(groupRepository.save(entity)).thenReturn(entity);
        when(groupMapper.toDto(entity)).thenReturn(dto);
        when(partnerServiceProvider.getObject()).thenReturn(partnerService);

        var result = groupService.createGroup(formDto);

        assertThat(result).isEqualTo(dto);
        verify(partnerService, times(1)).addPartnersToGroup(partners, entity.getGroupId());
    }

    @Test
    @DisplayName("Should throw exception when group already exists")
    void createGroup_withExistingGroup_shouldThrowResourceAlreadyExistsException() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        var formDto = DummyObjects.newInstance(GroupFormDto.class);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(groupRepository.existsByNameAndUserId(formDto.getName(), loggedUser.getUserId())).thenReturn(true);
        when(messageService.getMessage(anyString())).thenReturn("error");

        assertThatThrownBy(() -> groupService.createGroup(formDto)).isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @Test
    @DisplayName("Should remove partners and delete group")
    void deleteGroup_withValidGroupId_shouldRemovePartnersAndDeleteGroup() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(partnerServiceProvider.getObject()).thenReturn(partnerService);

        groupService.deleteGroup(1L);

        verify(partnerService).removePartnersFromGroup(1L);
        verify(groupRepository).deleteByGroupIdAndUserId(1L, loggedUser.getUserId());
    }

    @Test
    @DisplayName("Should throw exception when group is not found")
    void updateGroup_withInvalidGroupId_shouldThrowResourceNotFoundException() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        var formDto = DummyObjects.newInstance(GroupFormDto.class);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(groupRepository.findByGroupIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(messageService.getMessage(anyString())).thenReturn("error");

        assertThatThrownBy(() -> groupService.updateGroup(formDto, 1L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should update group and sync partners successfully")
    void updateGroup_withValidData_shouldUpdateGroupAndReturnDto() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        var formDto = DummyObjects.newInstance(GroupFormDto.class);
        var group = DummyObjects.newInstance(Group.class);
        var dto = DummyObjects.newInstance(GroupDto.class);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(groupRepository.findByGroupIdAndUserId(group.getGroupId(), loggedUser.getUserId())).thenReturn(Optional.of(group));
        when(groupRepository.save(group)).thenReturn(group);
        when(groupMapper.toDto(group)).thenReturn(dto);
        when(partnerServiceProvider.getObject()).thenReturn(partnerService);

        var result = groupService.updateGroup(formDto, group.getGroupId());

        assertThat(group.getName()).isEqualTo(formDto.getName());
        assertThat(result).isEqualTo(dto);

        verify(partnerService).removePartnersFromGroup(group.getGroupId());
        if (!formDto.getPartners().isEmpty()) {
            verify(partnerService).addPartnersToGroup(formDto.getPartners(), group.getGroupId());
        }
    }

    @Test
    @DisplayName("Should return null when group is null")
    void findOrCreateGroup_withNullGroup_shouldReturnNull() {
        var result = groupService.findOrCreateGroup(null);
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should create group when group id is null")
    void findOrCreateGroup_withNullGroupId_shouldCreateGroup() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        var groupDto = DummyObjects.newInstance(GroupDto.class);
        groupDto.setGroupId(null);

        var formDto = DummyObjects.newInstance(GroupFormDto.class);
        var createdDto = DummyObjects.newInstance(GroupDto.class);

        when(groupMapper.toCreationDto(groupDto)).thenReturn(formDto);
        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(groupRepository.existsByNameAndUserId(any(), any())).thenReturn(false);
        when(groupFactory.createEntity(any(GroupFormDto.class), any())).thenReturn(DummyObjects.newInstance(Group.class));
        when(groupRepository.save(any())).thenReturn(DummyObjects.newInstance(Group.class));
        when(groupMapper.toDto(any(Group.class))).thenReturn(createdDto);
        when(partnerServiceProvider.getObject()).thenReturn(partnerService);

        var result = groupService.findOrCreateGroup(groupDto);

        assertThat(result).isEqualTo(createdDto);
    }

    @Test
    @DisplayName("Should return existing group when found")
    void findOrCreateGroup_withExistingGroupId_shouldReturnGroup() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        var groupDto = DummyObjects.newInstance(GroupDto.class);
        var entity = DummyObjects.newInstance(Group.class);
        var mappedDto = DummyObjects.newInstance(GroupDto.class);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(groupRepository.findByGroupIdAndUserId(groupDto.getGroupId(), loggedUser.getUserId())).thenReturn(Optional.of(entity));
        when(groupMapper.toDto(entity)).thenReturn(mappedDto);

        var result = groupService.findOrCreateGroup(groupDto);

        assertThat(result).isEqualTo(mappedDto);
    }

    @Test
    @DisplayName("Should list groups with pagination")
    void listGroups_withValidRequest_shouldReturnPagedResult() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        var group = new GroupListingProjection() {
            @Override
            public Long getGroupId() {
                return 999L;
            }

            @Override
            public String getName() {
                return "Group";
            }

            @Override
            public String getDescription() {
                return "Description";
            }

            @Override
            public Integer getPartnerCount() {
                return RandomUtils.nextInt(0, 10);
            }
        };

        var listingDto = DummyObjects.newInstance(GroupListingDto.class);

        Page<GroupListingProjection> page = new PageImpl<>(List.of(group));

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(groupRepository.listGroups(anyLong(), any(), any())).thenReturn(page);
        when(groupMapper.toListingDto(group)).thenReturn(listingDto);

        var result = groupService.listGroups(null, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(listingDto);
    }

    @Test
    @DisplayName("Should throw exception when group is not found")
    void findById_withInvalidGroupId_shouldThrowResourceNotFoundException() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(groupRepository.findGroupWithPartners(anyLong(), anyLong())).thenReturn(Collections.emptyList());
        when(messageService.getMessage(anyString())).thenReturn("error");

        assertThatThrownBy(() -> groupService.findById(1L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should return group when found")
    void findById_withValidGroupId_shouldReturnGroupDto() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        var projection = new GroupWithPartnersProjection() {
            @Override
            public Long getGroupId() {
                return 999L;
            }

            @Override
            public String getGroupName() {
                return "Group";
            }

            @Override
            public String getGroupDescription() {
                return "Description";
            }

            @Override
            public Long getPartnerId() {
                return 999L;
            }

            @Override
            public String getPartnerName() {
                return "Partner";
            }
        };
        var dto = DummyObjects.newInstance(GroupDto.class);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(groupRepository.findGroupWithPartners(anyLong(), anyLong())).thenReturn(List.of(projection));
        when(groupMapper.toDto(any(List.class))).thenReturn(dto);

        var result = groupService.findById(1L);

        assertThat(result).isEqualTo(dto);
    }
}
