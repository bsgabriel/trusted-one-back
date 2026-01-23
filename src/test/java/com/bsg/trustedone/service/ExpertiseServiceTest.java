package com.bsg.trustedone.service;

import com.bsg.trustedone.dto.AssignedExpertiseDto;
import com.bsg.trustedone.dto.UserDto;
import com.bsg.trustedone.dto.expertise.ExpertiseDto;
import com.bsg.trustedone.dto.expertise.ExpertiseListingDto;
import com.bsg.trustedone.dto.expertise.SpecializationDto;
import com.bsg.trustedone.dto.expertise.SpecializationListingDto;
import com.bsg.trustedone.dto.expertise.form.ExpertiseFormDto;
import com.bsg.trustedone.dto.expertise.form.SpecializationFormDto;
import com.bsg.trustedone.entity.Expertise;
import com.bsg.trustedone.exception.ResourceAlreadyExistsException;
import com.bsg.trustedone.exception.ResourceNotFoundException;
import com.bsg.trustedone.helper.DummyObjects;
import com.bsg.trustedone.helper.RandomUtils;
import com.bsg.trustedone.mapper.ExpertiseMapper;
import com.bsg.trustedone.projection.SpecializationListingProjection;
import com.bsg.trustedone.repository.ExpertiseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

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
    private UserService userService;

    @Mock
    private MessageService messageService;

    @Mock
    private ExpertiseMapper expertiseMapper;

    @Mock
    private ExpertiseRepository expertiseRepository;

    @Test
    @DisplayName("Should list expertises with pagination")
    void listExpertises_withValidRequest_shouldReturnPagedResult() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        var expertise = DummyObjects.newInstance(Expertise.class);
        var listingDto = DummyObjects.newInstance(ExpertiseListingDto.class);

        Page<Expertise> page = new PageImpl<>(List.of(expertise));

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(expertiseRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(expertiseMapper.entityToListingDto(expertise)).thenReturn(listingDto);

        var result = expertiseService.listExpertises(null, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(listingDto);
    }

    @Test
    @DisplayName("Should throw exception when expertise is not found")
    void findExpertise_withInvalidId_shouldThrowResourceNotFoundException() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(expertiseRepository.findByExpertiseIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(messageService.getMessage(anyString())).thenReturn("error");

        assertThatThrownBy(() -> expertiseService.findExpertise(1L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should throw exception when expertise is inactive")
    void findExpertise_withInactiveExpertise_shouldThrowResourceNotFoundException() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        var expertise = DummyObjects.newInstance(Expertise.class);
        expertise.setActive(false);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(expertiseRepository.findByExpertiseIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(expertise));
        when(messageService.getMessage(anyString())).thenReturn("error");

        assertThatThrownBy(() -> expertiseService.findExpertise(expertise.getExpertiseId())).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should return expertise and filter inactive relations")
    void findExpertise_withActiveExpertise_shouldFilterRelationsAndReturnDto() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        var dto = DummyObjects.newInstance(ExpertiseDto.class);
        var expertise = DummyObjects.newInstance(Expertise.class);
        expertise.setActive(true);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(expertiseRepository.findByExpertiseIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(expertise));
        when(expertiseMapper.entityToExpertiseDto(expertise)).thenReturn(dto);

        var result = expertiseService.findExpertise(expertise.getExpertiseId());

        assertThat(result).isEqualTo(dto);
    }

    @Test
    @DisplayName("Should create expertise successfully")
    void createExpertise_withValidData_shouldCreateAndReturnDto() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        var formDto = DummyObjects.newInstance(ExpertiseFormDto.class);
        var entity = DummyObjects.newInstance(Expertise.class);
        var dto = DummyObjects.newInstance(ExpertiseDto.class);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(expertiseRepository.existsByNameAndUserId(formDto.getName(), loggedUser.getUserId())).thenReturn(false);
        when(expertiseMapper.expertiseFormToEntity(formDto, loggedUser)).thenReturn(entity);
        when(expertiseRepository.save(entity)).thenReturn(entity);
        when(expertiseMapper.entityToExpertiseDto(entity)).thenReturn(dto);

        var result = expertiseService.createExpertise(formDto);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    @DisplayName("Should throw exception when expertise already exists")
    void createExpertise_withExistingExpertise_shouldThrowResourceAlreadyExistsException() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        var formDto = DummyObjects.newInstance(ExpertiseFormDto.class);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(expertiseRepository.existsByNameAndUserId(formDto.getName(), loggedUser.getUserId())).thenReturn(true);
        when(messageService.getMessage(anyString())).thenReturn("error");

        assertThatThrownBy(() -> expertiseService.createExpertise(formDto)).isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @Test
    @DisplayName("Should throw exception when expertise is not found")
    void updateExpertise_withInvalidId_shouldThrowResourceNotFoundException() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        var formDto = DummyObjects.newInstance(ExpertiseFormDto.class);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(expertiseRepository.findByExpertiseIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(messageService.getMessage(anyString())).thenReturn("error");

        assertThatThrownBy(() -> expertiseService.updateExpertise(1L, formDto)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should update expertise successfully")
    void updateExpertise_withValidData_shouldUpdateAndReturnDto() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        var expertise = DummyObjects.newInstance(Expertise.class);
        var formDto = DummyObjects.newInstance(ExpertiseFormDto.class);
        var dto = DummyObjects.newInstance(ExpertiseDto.class);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(expertiseRepository.findByExpertiseIdAndUserId(expertise.getExpertiseId(), loggedUser.getUserId())).thenReturn(Optional.of(expertise));
        when(expertiseRepository.save(expertise)).thenReturn(expertise);
        when(expertiseMapper.entityToExpertiseDto(expertise)).thenReturn(dto);

        var result = expertiseService.updateExpertise(expertise.getExpertiseId(), formDto);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    @DisplayName("Should deactivate expertise")
    void deleteExpertise_withValidId_shouldDeactivateExpertise() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        when(userService.getLoggedUser()).thenReturn(loggedUser);

        expertiseService.deleteExpertise(1L);

        verify(expertiseRepository).deactivate(1L, loggedUser.getUserId());
    }

    @Test
    @DisplayName("Should list specializations")
    void listSpecializations_withValidExpertiseId_shouldReturnDtos() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        var specialization = new SpecializationListingProjection() {
            @Override
            public Long getParentExpertiseId() {
                return 1L;
            }

            @Override
            public Long getExpertiseId() {
                return 2L;
            }

            @Override
            public String getName() {
                return "Specialization";
            }

            @Override
            public Integer getPartnerCount() {
                return RandomUtils.nextInt(0, 10);
            }
        };
        var dto = DummyObjects.newInstance(SpecializationListingDto.class);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(expertiseRepository.listSpecializations(anyLong(), anyLong())).thenReturn(List.of(specialization));
        when(expertiseMapper.specializationListingProjectionToDto(specialization)).thenReturn(dto);

        var result = expertiseService.listSpecializations(1L);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(dto);
    }

    @Test
    @DisplayName("Should throw exception when specialization is not found")
    void findSpecialization_withInvalidId_shouldThrowResourceNotFoundException() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(expertiseRepository.findByExpertiseIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(messageService.getMessage(anyString())).thenReturn("error");

        assertThatThrownBy(() -> expertiseService.findSpecialization(1L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should return specialization and filter inactive partners")
    void findSpecialization_withValidData_shouldReturnDto() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        var specialization = DummyObjects.newInstance(Expertise.class);
        var dto = DummyObjects.newInstance(SpecializationDto.class);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(expertiseRepository.findByExpertiseIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(specialization));
        when(expertiseMapper.entityToSpecialization(specialization)).thenReturn(dto);

        var result = expertiseService.findSpecialization(specialization.getExpertiseId());

        assertThat(result).isEqualTo(dto);
    }

    @Test
    @DisplayName("Should create specialization successfully")
    void createSpecialization_withValidData_shouldCreateAndReturnDto() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        var formDto = DummyObjects.newInstance(SpecializationFormDto.class);
        var parent = DummyObjects.newInstance(Expertise.class);
        parent.setParentExpertise(null);

        var specialization = DummyObjects.newInstance(Expertise.class);
        var dto = DummyObjects.newInstance(SpecializationDto.class);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(expertiseRepository.findByExpertiseIdAndUserId(formDto.getParentExpertiseId(), loggedUser.getUserId())).thenReturn(Optional.of(parent));
        when(expertiseMapper.specializationFormToEntity(formDto, loggedUser)).thenReturn(specialization);
        when(expertiseRepository.save(specialization)).thenReturn(specialization);
        when(expertiseMapper.entityToSpecialization(specialization)).thenReturn(dto);

        var result = expertiseService.createSpecialization(formDto);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    @DisplayName("Should update specialization successfully")
    void updateSpecialization_withValidData_shouldUpdateAndReturnDto() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        var specialization = DummyObjects.newInstance(Expertise.class);
        var formDto = DummyObjects.newInstance(SpecializationFormDto.class);
        var dto = DummyObjects.newInstance(SpecializationDto.class);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(expertiseMapper.entityToSpecialization(specialization)).thenReturn(dto);
        when(expertiseRepository.findByExpertiseIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(specialization));
        when(expertiseRepository.save(specialization)).thenReturn(specialization);

        var result = expertiseService.updateSpecialization(specialization.getExpertiseId(), formDto);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    @DisplayName("Should create specialization when expertise id is null")
    void findOrCreateExpertise_withNullExpertiseId_shouldCreateAndReturnAssignedDto() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        var parentExpertise = DummyObjects.newInstance(Expertise.class);
        var assigned = DummyObjects.newInstance(AssignedExpertiseDto.class);
        assigned.setParentExpertiseId(parentExpertise.getExpertiseId());
        assigned.setParentExpertiseName(parentExpertise.getName());
        assigned.setExpertiseId(null);

        var formDto = DummyObjects.newInstance(SpecializationFormDto.class);
        formDto.setParentExpertiseId(parentExpertise.getExpertiseId());
        var specializationDto = DummyObjects.newInstance(SpecializationDto.class);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(expertiseMapper.toCreationDto(assigned)).thenReturn(formDto);
        when(expertiseRepository.findByExpertiseIdAndUserId(parentExpertise.getExpertiseId(), loggedUser.getUserId())).thenReturn(Optional.of(parentExpertise));
        when(expertiseRepository.save(any())).thenReturn(DummyObjects.newInstance(Expertise.class));
        when(expertiseMapper.entityToSpecialization(any())).thenReturn(specializationDto);

        var result = expertiseService.findOrCreateExpertise(assigned);

        assertThat(result.getExpertiseId()).isEqualTo(specializationDto.getExpertiseId());
    }
}
