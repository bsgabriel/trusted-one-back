package com.bsg.trustedone.service;

import com.bsg.trustedone.dto.ExpertiseCreationDto;
import com.bsg.trustedone.dto.ExpertiseDto;
import com.bsg.trustedone.dto.UserDto;
import com.bsg.trustedone.entity.Expertise;
import com.bsg.trustedone.exception.ResourceAlreadyExistsException;
import com.bsg.trustedone.exception.ResourceCreationException;
import com.bsg.trustedone.exception.ResourceUpdateException;
import com.bsg.trustedone.exception.UnauthorizedAccessException;
import com.bsg.trustedone.factory.ExpertiseFactory;
import com.bsg.trustedone.helper.DummyObjects;
import com.bsg.trustedone.helper.RandomUtils;
import com.bsg.trustedone.mapper.ExpertiseMapper;
import com.bsg.trustedone.repository.ExpertiseRepository;
import com.bsg.trustedone.validator.ExpertiseValidator;
import jakarta.validation.Validator;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpertiseServiceTest {

    @InjectMocks
    private ExpertiseService expertiseService;

    @Mock
    private ExpertiseMapper expertiseMapper;

    @Mock
    private ExpertiseFactory expertiseFactory;

    @Mock
    private ExpertiseRepository expertiseRepository;

    @Mock
    private ExpertiseValidator expertiseValidator;

    @Mock
    private Validator validator;

    @Mock
    private UserService userService;

    private UserDto loggedUser;

    @BeforeEach
    public void beforeAll() {
        lenient().when(expertiseMapper.toDto(any(Expertise.class))).thenCallRealMethod();
        lenient().when(expertiseMapper.toCreationDto(any(ExpertiseDto.class))).thenCallRealMethod();
        lenient().when(expertiseFactory.createEntity(any(ExpertiseCreationDto.class), any(UserDto.class))).thenCallRealMethod();
        lenient().when(expertiseRepository.save(any(Expertise.class))).then(invocation -> {
            var created = (Expertise) invocation.getArguments()[0];
            created.setExpertiseId(RandomUtils.nextLong(1, 999));
            return created;
        });

        this.loggedUser = DummyObjects.newInstance(UserDto.class);
        lenient().when(userService.getLoggedUser()).thenReturn(loggedUser);
    }

    @Test
    @DisplayName("Should propagate exception when expertise creation validate fails")
    void createExpertise_withInvalidExpertiseData_shouldPropagateValidationException() {
        // Given
        var expertiseCreationDto = DummyObjects.newInstance(ExpertiseCreationDto.class);

        doThrow(new ResourceAlreadyExistsException("Error", List.of()))
                .when(expertiseValidator).validateExpertiseCreate(expertiseCreationDto);

        // When & Then
        assertThatThrownBy(() -> expertiseService.createExpertise(expertiseCreationDto))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessage("Error");

        verify(expertiseValidator).validateExpertiseCreate(expertiseCreationDto);
    }

    @Test
    @DisplayName("Should throw error if expertise already exist")
    void createExpertise_withAlreadyRegisteredName_shouldThrowException() {
        // Given
        var expertiseCreationDto = DummyObjects.newInstance(ExpertiseCreationDto.class);
        expertiseCreationDto.setParentExpertiseId(null);

        when(expertiseRepository.existsByNameAndUserId(expertiseCreationDto.getName(), loggedUser.getUserId())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> expertiseService.createExpertise(expertiseCreationDto))
                .isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @Test
    @DisplayName("Should throw error if parent expertise not exists")
    void createExpertise_withNonExistingParentExpertise_shouldThrowError() {
        // Given
        var expertiseCreationDto = DummyObjects.newInstance(ExpertiseCreationDto.class);

        // When & Then
        assertThatThrownBy(() -> expertiseService.createExpertise(expertiseCreationDto))
                .isInstanceOf(ResourceCreationException.class);
    }

    @Test
    @DisplayName("Should create expertise successfully when data is valid")
    void createExpertise_withValidData_shouldcreateNewExpertiseSuccessfully() {
        // Given
        var expertiseCreationDto = DummyObjects.newInstance(ExpertiseCreationDto.class);
        expertiseCreationDto.setParentExpertiseId(null);

        when(expertiseRepository.existsByNameAndUserId(expertiseCreationDto.getName(), loggedUser.getUserId())).thenReturn(false);

        // When
        var result = expertiseService.createExpertise(expertiseCreationDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(expertiseCreationDto.getName());
        assertThat(result.getParentExpertiseId()).isNull();
    }

    @Test
    @DisplayName("Should create specialization successfully when data is valid")
    void createExpertise_withValidData_shouldcreateSpecializationSuccessfully() {
        // Given
        var parentExpertise = DummyObjects.newInstance(Expertise.class);
        parentExpertise.setExpertiseId(1234L);
        parentExpertise.setUserId(loggedUser.getUserId());

        var expertiseCreationDto = DummyObjects.newInstance(ExpertiseCreationDto.class);
        expertiseCreationDto.setParentExpertiseId(parentExpertise.getExpertiseId());

        when(expertiseRepository.findById(parentExpertise.getExpertiseId())).thenReturn(Optional.of(parentExpertise));

        // When
        var result = expertiseService.createExpertise(expertiseCreationDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(expertiseCreationDto.getName());
        assertThat(result.getParentExpertiseId()).isEqualTo(expertiseCreationDto.getParentExpertiseId());
    }

    @Test
    @DisplayName("Should propagate exception when expertise update validate fails")
    void updateExpertise_withInvalidExpertiseData_shouldPropagateValidationException() {
        // Given
        var updateData = DummyObjects.newInstance(ExpertiseCreationDto.class);

        doThrow(new ResourceUpdateException("Error", List.of()))
                .when(expertiseValidator).validateExpertiseUpdate(updateData);

        // When & Then
        assertThatThrownBy(() -> expertiseService.updateExpertise(updateData, anyLong()))
                .isInstanceOf(ResourceUpdateException.class)
                .hasMessage("Error");

        verify(expertiseValidator).validateExpertiseUpdate(updateData);
    }

    @Test
    @DisplayName("Should throw exception when updating expertise from another user")
    void updateExpertise_withUserIdDifferentThanLogged_shouldThrowException() {
        // Given
        var expertiseOwner = DummyObjects.newInstance(UserDto.class);

        var expertiseId = 999L;
        var updateData = DummyObjects.newInstance(ExpertiseCreationDto.class);
        var expertise = DummyObjects.newInstance(Expertise.class);

        expertise.setExpertiseId(expertiseId);
        expertise.setUserId(expertiseOwner.getUserId());

        when(expertiseRepository.findById(expertise.getExpertiseId())).thenReturn(Optional.of(expertise));

        // When & Then
        assertThatThrownBy(() -> expertiseService.updateExpertise(updateData, expertiseId))
                .isInstanceOf(UnauthorizedAccessException.class);
    }

    @Test
    @DisplayName("Should successfully update expertise data")
    void updateExpertise_shouldSuccessfullyUpdate() {
        // Given
        var expertiseId = 999L;
        var updateData = DummyObjects.newInstance(ExpertiseCreationDto.class);
        var existingExpertise = DummyObjects.newInstance(Expertise.class);

        existingExpertise.setExpertiseId(expertiseId);
        existingExpertise.setUserId(loggedUser.getUserId());

        when(expertiseRepository.findById(existingExpertise.getExpertiseId())).thenReturn(Optional.of(existingExpertise));

        // When
        var result = expertiseService.updateExpertise(updateData, expertiseId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(updateData.getName());
        assertThat(result.getParentExpertiseId()).isEqualTo(updateData.getParentExpertiseId());
    }

}