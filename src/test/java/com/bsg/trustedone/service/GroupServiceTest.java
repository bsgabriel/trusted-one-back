package com.bsg.trustedone.service;

import com.bsg.trustedone.dto.GroupDto;
import com.bsg.trustedone.dto.GroupFormDto;
import com.bsg.trustedone.dto.UserDto;
import com.bsg.trustedone.entity.Group;
import com.bsg.trustedone.exception.ResourceAlreadyExistsException;
import com.bsg.trustedone.exception.ResourceCreationException;
import com.bsg.trustedone.exception.ResourceUpdateException;
import com.bsg.trustedone.exception.UnauthorizedAccessException;
import com.bsg.trustedone.factory.GroupFactory;
import com.bsg.trustedone.helper.DummyObjects;
import com.bsg.trustedone.helper.RandomUtils;
import com.bsg.trustedone.mapper.GroupMapper;
import com.bsg.trustedone.repository.GroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @InjectMocks
    private GroupService groupService;

    @Mock
    private UserService userService;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupMapper groupMapper;

    @Mock
    private GroupFactory groupFactory;

    @Mock
    private ObjectProvider<PartnerService> partnerServiceProvider;

    private UserDto loggedUser;

    @BeforeEach
    public void beforeAll() {
        lenient().when(groupMapper.toDto(any(Group.class))).thenCallRealMethod();
        lenient().when(groupMapper.toCreationDto(any(GroupDto.class))).thenCallRealMethod();
        lenient().when(groupFactory.createEntity(any(GroupFormDto.class), any(UserDto.class))).thenCallRealMethod();
        lenient().when(groupRepository.save(any(Group.class))).then(invocation -> {
            var created = (Group) invocation.getArguments()[0];
            created.setGroupId(RandomUtils.nextLong(1, 999));
            return created;
        });

        this.loggedUser = DummyObjects.newInstance(UserDto.class);
        lenient().when(userService.getLoggedUser()).thenReturn(loggedUser);

        lenient().when(partnerServiceProvider.getObject()).thenReturn(Mockito.mock(PartnerService.class));

    }

    @Test
    @DisplayName("Should propagate exception when group creation validate fails")
    void groupCreation_withInvalidGroupData_shouldPropagateValidationException() {
        // Given
        var groupCreationDto = GroupFormDto.builder().build();

        // When & Then
        assertThatThrownBy(() -> groupService.createGroup(groupCreationDto))
                .isInstanceOf(ResourceCreationException.class);
    }

    @Test
    @DisplayName("Should throw error if group already exist")
    void groupCreation_withAlreadyRegisteredName_shouldThrowException() {
        // Given
        var groupCreationDto = DummyObjects.newInstance(GroupFormDto.class);

        when(groupRepository.existsByNameAndUserId(groupCreationDto.getName(), loggedUser.getUserId())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> groupService.createGroup(groupCreationDto))
                .isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @Test
    @DisplayName("Should create group successfully when data is valid")
    void createGroup_withValidData_shouldCreateGroupSuccessfully() {
        // Given
        var groupCreationDto = DummyObjects.newInstance(GroupFormDto.class);

        when(groupRepository.existsByNameAndUserId(groupCreationDto.getName(), loggedUser.getUserId())).thenReturn(false);
        when(groupRepository.save(any(Group.class))).then(invocation -> invocation.getArguments()[0]);

        // When
        var result = groupService.createGroup(groupCreationDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(groupCreationDto.getName());
        assertThat(result.getDescription()).isEqualTo(groupCreationDto.getDescription());
    }

    @Test
    @DisplayName("Should remove partner from grouop")
    void deleteGroup_shouldRemovePartnerFromGrouop() {
        // Given
        var groupOwner = DummyObjects.newInstance(UserDto.class);

        var group = DummyObjects.newInstance(Group.class);
        group.setUserId(groupOwner.getUserId());

        // When
        groupService.deleteGroup(group.getGroupId());

        // Then
        verify(partnerServiceProvider.getObject(), times(1)).removePartnersFromGroup(group.getGroupId());
    }

    @Test
    @DisplayName("Should propagate exception when group update validate fails")
    void groupUpdate_withInvalidGroupData_shouldPropagateValidationException() {
        // Given
        var updateData = GroupFormDto.builder().build();

        // When & Then
        assertThatThrownBy(() -> groupService.updateGroup(updateData, anyLong()))
                .isInstanceOf(ResourceUpdateException.class);
    }

    @Test
    @DisplayName("Should throw exception when updating group from another user")
    void updateGroup_withUserIdDifferentThanLogged_shouldThrowException() {
        // Given
        var groupOwner = DummyObjects.newInstance(UserDto.class);

        var groupId = 999L;
        var updateData = DummyObjects.newInstance(GroupFormDto.class);
        var group = DummyObjects.newInstance(Group.class);

        group.setGroupId(groupId);
        group.setUserId(groupOwner.getUserId());

        when(groupRepository.findById(group.getGroupId())).thenReturn(Optional.of(group));

        // When & Then
        assertThatThrownBy(() -> groupService.updateGroup(updateData, groupId))
                .isInstanceOf(UnauthorizedAccessException.class);
    }

    @Test
    @DisplayName("Should successfully update group data")
    void updateGroup_shouldSuccessfullyUpdate() {
        // Given
        var groupId = 999L;
        var updateData = DummyObjects.newInstance(GroupFormDto.class);
        var existingGroup = DummyObjects.newInstance(Group.class);

        existingGroup.setGroupId(groupId);
        existingGroup.setUserId(loggedUser.getUserId());

        when(groupRepository.findById(existingGroup.getGroupId())).thenReturn(Optional.of(existingGroup));

        // When
        var result = groupService.updateGroup(updateData, groupId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(updateData.getName());
        assertThat(result.getDescription()).isEqualTo(updateData.getDescription());
    }

    @Test
    @DisplayName("Should return null GroupDto when group is null")
    void findOrCreateGroup_shouldReturnNullGroupDto_whenGroupIsNull() {
        // When
        var result = groupService.findOrCreateGroup(null);

        // Then
        assertThat(result).isNull();
        verifyNoInteractions(groupRepository, groupMapper);
    }

    @Test
    @DisplayName("Should create new group when groupId is null")
    void findOrCreateGroup_shouldCreateNewGroup_whenGroupIdIsNull() {
        // Given
        var inputGroup = DummyObjects.newInstance(GroupDto.class);
        inputGroup.setGroupId(null);

        // When
        var result = groupService.findOrCreateGroup(inputGroup);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getGroupId()).isNotNull();
        assertThat(result.getName()).isEqualTo(inputGroup.getName());
        assertThat(result.getDescription()).isEqualTo(inputGroup.getDescription());

        verify(groupMapper).toCreationDto(inputGroup);
    }

    @Test
    @DisplayName("Should return existing group when found by ID")
    void findOrCreateGroup_shouldReturnExistingGroup_whenFoundById() {
        // Given
        var inputGroup = DummyObjects.newInstance(GroupDto.class);
        var existingGroupEntity = DummyObjects.newInstance(Group.class);
        existingGroupEntity.setGroupId(inputGroup.getGroupId());

        when(groupRepository.findById(inputGroup.getGroupId())).thenReturn(Optional.of(existingGroupEntity));

        // When
        var result = groupService.findOrCreateGroup(inputGroup);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getGroupId()).isEqualTo(inputGroup.getGroupId());
        assertThat(result.getName()).isEqualTo(existingGroupEntity.getName());
        assertThat(result.getDescription()).isEqualTo(existingGroupEntity.getDescription());

        verify(groupRepository).findById(inputGroup.getGroupId());
        verify(groupMapper).toDto(existingGroupEntity);
        verify(groupMapper, never()).toCreationDto(any());
    }

    @Test
    @DisplayName("Should create new group when not found by ID")
    void findOrCreateGroup_shouldCreateNewGroup_whenNotFoundById() {
        // Given
        var inputGroup = DummyObjects.newInstance(GroupDto.class);

        // When
        var result = groupService.findOrCreateGroup(inputGroup);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getGroupId()).isNotNull();
        assertThat(result.getName()).isEqualTo(inputGroup.getName());
        assertThat(result.getDescription()).isEqualTo(inputGroup.getDescription());

        verify(groupRepository).findById(inputGroup.getGroupId());
        verify(groupMapper).toCreationDto(inputGroup);
    }

    @Test
    @DisplayName("Should return GroupDto with only non-null fields when group has only groupId")
    void findOrCreateGroup_shouldHandleGroup_withOnlyGroupId() {
        // Given
        var inputGroup = GroupDto.builder().groupId(999L).build();
        var existingGroupEntity = DummyObjects.newInstance(Group.class);
        existingGroupEntity.setGroupId(inputGroup.getGroupId());

        when(groupRepository.findById(inputGroup.getGroupId())).thenReturn(Optional.of(existingGroupEntity));

        // When
        var result = groupService.findOrCreateGroup(inputGroup);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getGroupId()).isEqualTo(inputGroup.getGroupId());
        assertThat(result.getName()).isEqualTo(existingGroupEntity.getName());
        assertThat(result.getDescription()).isEqualTo(existingGroupEntity.getDescription());

        verify(groupRepository).findById(inputGroup.getGroupId());
        verify(groupMapper).toDto(existingGroupEntity);
    }

}