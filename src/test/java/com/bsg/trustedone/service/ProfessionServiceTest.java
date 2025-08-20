package com.bsg.trustedone.service;

import com.bsg.trustedone.dto.ProfessionCreationDto;
import com.bsg.trustedone.dto.ProfessionDto;
import com.bsg.trustedone.dto.UserDto;
import com.bsg.trustedone.entity.Profession;
import com.bsg.trustedone.exception.ResourceAlreadyExistsException;
import com.bsg.trustedone.exception.ResourceCreationException;
import com.bsg.trustedone.exception.ResourceUpdateException;
import com.bsg.trustedone.exception.UnauthorizedAccessException;
import com.bsg.trustedone.factory.ProfessionFactory;
import com.bsg.trustedone.helper.DummyObjects;
import com.bsg.trustedone.helper.RandomUtils;
import com.bsg.trustedone.mapper.ProfessionMapper;
import com.bsg.trustedone.repository.ProfessionRepository;
import com.bsg.trustedone.validator.ProfessionValidator;
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
class ProfessionServiceTest {

    @InjectMocks
    private ProfessionService professionService;

    @Mock
    private ProfessionMapper professionMapper;

    @Mock
    private ProfessionFactory professionFactory;

    @Mock
    private ProfessionRepository professionRepository;

    @Mock
    private ProfessionValidator professionValidator;

    @Mock
    private Validator validator;

    @Mock
    private UserService userService;

    private UserDto loggedUser;

    @BeforeEach
    public void beforeAll() {
        lenient().when(professionMapper.toDto(any(Profession.class))).thenCallRealMethod();
        lenient().when(professionMapper.toCreationDto(any(ProfessionDto.class))).thenCallRealMethod();
        lenient().when(professionFactory.createEntity(any(ProfessionCreationDto.class), any(UserDto.class))).thenCallRealMethod();
        lenient().when(professionRepository.save(any(Profession.class))).then(invocation -> {
            var created = (Profession) invocation.getArguments()[0];
            created.setProfessionId(RandomUtils.nextLong(1, 999));
            return created;
        });

        this.loggedUser = DummyObjects.newInstance(UserDto.class);
        lenient().when(userService.getLoggedUser()).thenReturn(loggedUser);
    }

    @Test
    @DisplayName("Should propagate exception when profession creation validate fails")
    void createProfession_withInvalidProfessionData_shouldPropagateValidationException() {
        // Given
        var professionCreationDto = DummyObjects.newInstance(ProfessionCreationDto.class);

        doThrow(new ResourceAlreadyExistsException("Error", List.of()))
                .when(professionValidator).validateProfessionCreate(professionCreationDto);

        // When & Then
        assertThatThrownBy(() -> professionService.createProfession(professionCreationDto))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessage("Error");

        verify(professionValidator).validateProfessionCreate(professionCreationDto);
    }

    @Test
    @DisplayName("Should throw error if profession already exist")
    void createProfession_withAlreadyRegisteredName_shouldThrowException() {
        // Given
        var professionCreationDto = DummyObjects.newInstance(ProfessionCreationDto.class);
        professionCreationDto.setParentProfessionId(null);

        when(professionRepository.existsByNameAndUserId(professionCreationDto.getName(), loggedUser.getUserId())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> professionService.createProfession(professionCreationDto))
                .isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @Test
    @DisplayName("Should throw error if parent profession not exists")
    void createProfession_withNonExistingParentProfession_shouldThrowError() {
        // Given
        var professionCreationDto = DummyObjects.newInstance(ProfessionCreationDto.class);

        // When & Then
        assertThatThrownBy(() -> professionService.createProfession(professionCreationDto))
                .isInstanceOf(ResourceCreationException.class);
    }

    @Test
    @DisplayName("Should create profession successfully when data is valid")
    void createProfession_withValidData_shouldcreateNewProfessionSuccessfully() {
        // Given
        var professionCreationDto = DummyObjects.newInstance(ProfessionCreationDto.class);
        professionCreationDto.setParentProfessionId(null);

        when(professionRepository.existsByNameAndUserId(professionCreationDto.getName(), loggedUser.getUserId())).thenReturn(false);

        // When
        var result = professionService.createProfession(professionCreationDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(professionCreationDto.getName());
        assertThat(result.getParentProfessionId()).isNull();
    }

    @Test
    @DisplayName("Should create specialization successfully when data is valid")
    void createProfession_withValidData_shouldcreateSpecializationSuccessfully() {
        // Given
        var parentProfession = DummyObjects.newInstance(Profession.class);
        parentProfession.setProfessionId(1234L);
        parentProfession.setUserId(loggedUser.getUserId());

        var professionCreationDto = DummyObjects.newInstance(ProfessionCreationDto.class);
        professionCreationDto.setParentProfessionId(parentProfession.getProfessionId());

        when(professionRepository.findById(parentProfession.getProfessionId())).thenReturn(Optional.of(parentProfession));

        // When
        var result = professionService.createProfession(professionCreationDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(professionCreationDto.getName());
        assertThat(result.getParentProfessionId()).isEqualTo(professionCreationDto.getParentProfessionId());
    }

    @Test
    @DisplayName("Should propagate exception when group update validate fails")
    void groupUpdate_withInvalidGroupData_shouldPropagateValidationException() {
        // Given
        var updateData = DummyObjects.newInstance(ProfessionCreationDto.class);

        doThrow(new ResourceUpdateException("Error", List.of()))
                .when(professionValidator).validateProfessionUpdate(updateData);

        // When & Then
        assertThatThrownBy(() -> professionService.updateProfession(updateData, anyLong()))
                .isInstanceOf(ResourceUpdateException.class)
                .hasMessage("Error");

        verify(professionValidator).validateProfessionUpdate(updateData);
    }

    @Test
    @DisplayName("Should throw exception when updating profession from another user")
    void updateProfession_withUserIdDifferentThanLogged_shouldThrowException() {
        // Given
        var professionOwner = DummyObjects.newInstance(UserDto.class);

        var professionId = 999L;
        var updateData = DummyObjects.newInstance(ProfessionCreationDto.class);
        var profession = DummyObjects.newInstance(Profession.class);

        profession.setProfessionId(professionId);
        profession.setUserId(professionOwner.getUserId());

        when(professionRepository.findById(profession.getProfessionId())).thenReturn(Optional.of(profession));

        // When & Then
        assertThatThrownBy(() -> professionService.updateProfession(updateData, professionId))
                .isInstanceOf(UnauthorizedAccessException.class);
    }

    @Test
    @DisplayName("Should successfully update profession data")
    void updateProfession_shouldSuccessfullyUpdate() {
        // Given
        var groupId = 999L;
        var updateData = DummyObjects.newInstance(ProfessionCreationDto.class);
        var existingProfession = DummyObjects.newInstance(Profession.class);

        existingProfession.setProfessionId(groupId);
        existingProfession.setUserId(loggedUser.getUserId());

        when(professionRepository.findById(existingProfession.getProfessionId())).thenReturn(Optional.of(existingProfession));

        // When
        var result = professionService.updateProfession(updateData, groupId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(updateData.getName());
        assertThat(result.getParentProfessionId()).isEqualTo(updateData.getParentProfessionId());
    }

    @Test
    void testeQueVaiFalhar() {
        // Given
        var groupId = 999L;
        var updateData = DummyObjects.newInstance(ProfessionCreationDto.class);
        var existingProfession = DummyObjects.newInstance(Profession.class);

        existingProfession.setProfessionId(groupId);
        existingProfession.setUserId(loggedUser.getUserId());

        when(professionRepository.findById(existingProfession.getProfessionId())).thenReturn(Optional.of(existingProfession));

        // When
        var result = professionService.updateProfession(updateData, groupId);

        // Then
        assertThat(result).isNull();
    }
}