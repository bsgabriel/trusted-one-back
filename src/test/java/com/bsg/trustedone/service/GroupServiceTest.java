package com.bsg.trustedone.service;

import com.bsg.trustedone.dto.GroupCreationDto;
import com.bsg.trustedone.dto.UserDto;
import com.bsg.trustedone.entity.Group;
import com.bsg.trustedone.exception.GroupAlreadyExistsException;
import com.bsg.trustedone.exception.GroupCreationException;
import com.bsg.trustedone.exception.GroupUpdateException;
import com.bsg.trustedone.exception.UnauthorizedAccessException;
import com.bsg.trustedone.factory.GroupFactory;
import com.bsg.trustedone.helper.DummyObjects;
import com.bsg.trustedone.mapper.GroupMapper;
import com.bsg.trustedone.repository.GroupRepository;
import com.bsg.trustedone.validator.GroupValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
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
    private GroupValidator groupValidator;

    @Mock
    private GroupMapper groupMapper;

    @Mock
    private GroupFactory groupFactory;

    @BeforeEach
    public void beforeAll() {
        lenient().when(groupMapper.toDto(any(Group.class))).thenCallRealMethod();
        lenient().when(groupFactory.createEntity(any(GroupCreationDto.class), any(UserDto.class))).thenCallRealMethod();
        lenient().when(groupRepository.save(any(Group.class))).then(invocation -> invocation.getArguments()[0]);
    }

    @Test
    @DisplayName("Should propagate exception when group creation validate fails")
    void groupCreation_WithInvalidGroupData_ShouldPropagateValidationException() {
        // Given
        var groupCreationDto = DummyObjects.newInstance(GroupCreationDto.class);

        doThrow(new GroupCreationException("Error", List.of()))
                .when(groupValidator).validateGroupCreate(groupCreationDto);

        // When & Then
        assertThatThrownBy(() -> groupService.createGroup(groupCreationDto))
                .isInstanceOf(GroupCreationException.class)
                .hasMessage("Error");

        verify(groupValidator).validateGroupCreate(groupCreationDto);
    }

    @Test
    @DisplayName("Should throw error if group already exist")
    void groupCreation_WithAlreadyRegisteredName_ShouldThrowException() {
        // Given
        var groupCreationDto = DummyObjects.newInstance(GroupCreationDto.class);
        var loggedUser = DummyObjects.newInstance(UserDto.class);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(groupRepository.existsByNameAndUserId(groupCreationDto.getName(), loggedUser.getUserId())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> groupService.createGroup(groupCreationDto))
                .isInstanceOf(GroupAlreadyExistsException.class);
    }

    @Test
    @DisplayName("Should create group successfully when data is valid")
    void createGroup_WithValidData_ShouldCreateGroupSuccessfully() {
        // Given
        var groupCreationDto = DummyObjects.newInstance(GroupCreationDto.class);
        var loggedUser = DummyObjects.newInstance(UserDto.class);

        when(userService.getLoggedUser()).thenReturn(loggedUser);

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
    @DisplayName("Should throw exception when deleting group from another user")
    void deleteGroup_WithUserIdDifferentThanLogged_ShouldThrowException() {
        // Given
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        var groupOwner = DummyObjects.newInstance(UserDto.class);

        var group = DummyObjects.newInstance(Group.class);
        group.setUserId(groupOwner.getUserId());
        when(groupRepository.findById(group.getGroupId())).thenReturn(Optional.of(group));

        when(userService.getLoggedUser()).thenReturn(loggedUser);

        // When & Then
        assertThatThrownBy(() -> groupService.deleteGroup(group.getGroupId()))
                .isInstanceOf(UnauthorizedAccessException.class);
    }

    @Test
    @DisplayName("Should propagate exception when group update validate fails")
    void groupUpdate_WithInvalidGroupData_ShouldPropagateValidationException() {
        // Given
        var updateData = DummyObjects.newInstance(GroupCreationDto.class);

        doThrow(new GroupUpdateException("Error", List.of()))
                .when(groupValidator).validateGroupUpdate(updateData);

        // When & Then
        assertThatThrownBy(() -> groupService.updateGroup(updateData, anyLong()))
                .isInstanceOf(GroupUpdateException.class)
                .hasMessage("Error");

        verify(groupValidator).validateGroupUpdate(updateData);
    }

    @Test
    @DisplayName("Should throw exception when updating group from another user")
    void updateGroup_WithUserIdDifferentThanLogged_ShouldThrowException() {
        // Given
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        var groupOwner = DummyObjects.newInstance(UserDto.class);

        var groupId = 999L;
        var updateData = DummyObjects.newInstance(GroupCreationDto.class);
        var group = DummyObjects.newInstance(Group.class);

        group.setGroupId(groupId);
        group.setUserId(groupOwner.getUserId());

        when(groupRepository.findById(group.getGroupId())).thenReturn(Optional.of(group));
        when(userService.getLoggedUser()).thenReturn(loggedUser);

        // When & Then
        assertThatThrownBy(() -> groupService.updateGroup(updateData, groupId))
                .isInstanceOf(UnauthorizedAccessException.class);
    }

    @Test
    @DisplayName("Should successfully update group data")
    void updateGroup_ShouldSuccessfullyUpdate() {
        // Given
        var loggedUser = DummyObjects.newInstance(UserDto.class);

        var groupId = 999L;
        var updateData = DummyObjects.newInstance(GroupCreationDto.class);
        var existingGroup = DummyObjects.newInstance(Group.class);

        existingGroup.setGroupId(groupId);
        existingGroup.setUserId(loggedUser.getUserId());

        when(groupRepository.findById(existingGroup.getGroupId())).thenReturn(Optional.of(existingGroup));
        when(userService.getLoggedUser()).thenReturn(loggedUser);

        // When
        var result = groupService.updateGroup(updateData, groupId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result.get().getName()).isEqualTo(updateData.getName());
        assertThat(result.get().getDescription()).isEqualTo(updateData.getDescription());
    }

}