package com.bsg.trustedone.service;

import com.bsg.trustedone.dto.*;
import com.bsg.trustedone.entity.Professional;
import com.bsg.trustedone.exception.UnauthorizedAccessException;
import com.bsg.trustedone.factory.ProfessionalFactory;
import com.bsg.trustedone.helper.DummyObjects;
import com.bsg.trustedone.helper.RandomUtils;
import com.bsg.trustedone.mapper.ProfessionalMapper;
import com.bsg.trustedone.repository.ProfessionalRepository;
import com.bsg.trustedone.validator.ProfessionalValidator;
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
class ProfessionalServiceTest {

    @InjectMocks
    private ProfessionalService professionalService;

    @Mock
    private ProfessionService professionService;

    @Mock
    private CompanyService companyService;

    @Mock
    private UserService userService;

    @Mock
    private GroupService groupService;

    @Mock
    private ProfessionalFactory professionalFactory;

    @Mock
    private ProfessionalRepository professionalRepository;

    @Mock
    private ProfessionalValidator professionalValidator;

    @Mock
    private ProfessionalMapper professionalMapper;

    private UserDto loggedUser;

    @BeforeEach
    public void beforeAll() {
        lenient().when(professionalRepository.save(any(Professional.class))).then(invocation -> {
            var created = (Professional) invocation.getArguments()[0];
            created.setProfessionalId(RandomUtils.nextLong(1, 999));
            return created;
        });

        this.loggedUser = DummyObjects.newInstance(UserDto.class);
        lenient().when(userService.getLoggedUser()).thenReturn(loggedUser);
    }

    @Test
    @DisplayName("Should create a professional successfully when data is valid")
    void createProfessional_whenValidData_thenReturnSavedProfessionalDto() {
        // Given
        var creationDto = ProfessionalCreationDto.builder()
                .group(GroupDto.builder().build())
                .company(CompanyDto.builder().build())
                .professions(List.of(ProfessionDto.builder().professionId(10L).availableForReferrals(true).build()))
                .contactMethods(List.of(ContactMethodCreationDto.builder().build()))
                .build();

        var group = GroupDto.builder().groupId(1L).build();
        var company = CompanyDto.builder().companyId(2L).build();
        var profession = ProfessionDto.builder().professionId(10L).availableForReferrals(true).build();

        var entity = Professional.builder().build();
        var savedEntity = Professional.builder().build();
        var dto = ProfessionalDto.builder().professionalId(99L).build();

        when(groupService.findOrCreateGroup(any())).thenReturn(group);
        when(companyService.findOrCreateCompany(any())).thenReturn(company);
        when(professionService.findOrCreateProfession(any())).thenReturn(profession);
        when(professionalFactory.createEntity(any(), eq(group), eq(company), eq(loggedUser), any(), any())).thenReturn(entity);
        when(professionalRepository.save(entity)).thenReturn(savedEntity);
        when(professionalMapper.toDto(savedEntity)).thenReturn(dto);

        // When
        var result = professionalService.createProfessional(creationDto);

        // Then
        assertThat(result).isEqualTo(dto);
        verify(professionalValidator).validateProfessionalCreation(creationDto);
    }

    @Test
    @DisplayName("Should handle null group when creating a professional")
    void createProfessional_whenGroupIsNull_thenHandleNullGroup() {
        // Given
        var creationDto = ProfessionalCreationDto.builder()
                .group(null)
                .company(CompanyDto.builder().build())
                .professions(List.of())
                .contactMethods(List.of())
                .build();

        var emptyGroup = GroupDto.builder().build();
        when(groupService.findOrCreateGroup(null)).thenReturn(emptyGroup);
        when(companyService.findOrCreateCompany(any())).thenReturn(CompanyDto.builder().build());
        when(professionalRepository.save(any())).thenReturn(Professional.builder().build());
        when(professionalMapper.toDto(any())).thenReturn(ProfessionalDto.builder().build());

        // When
        var result = professionalService.createProfessional(creationDto);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should handle null company when creating a professional")
    void createProfessional_whenCompanyIsNull_thenHandleNullCompany() {
        // Given
        var creationDto = ProfessionalCreationDto.builder()
                .group(GroupDto.builder().build())
                .company(null)
                .professions(List.of())
                .contactMethods(List.of())
                .build();

        var emptyCompany = CompanyDto.builder().build();
        when(groupService.findOrCreateGroup(any())).thenReturn(GroupDto.builder().build());
        when(companyService.findOrCreateCompany(null)).thenReturn(emptyCompany);
        when(professionalRepository.save(any())).thenReturn(Professional.builder().build());
        when(professionalMapper.toDto(any())).thenReturn(ProfessionalDto.builder().build());

        // When
        var result = professionalService.createProfessional(creationDto);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should do nothing when trying to delete a non-existing professional")
    void deleteProfessional_whenProfessionalNotFound_thenDoNothing() {
        // Given
        when(professionalRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        professionalService.deleteProfessional(99L);

        // Then
        verify(professionalRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should delete professional when it belongs to the logged user")
    void deleteProfessional_whenProfessionalBelongsToUser_thenDelete() {
        // Given
        var professional = DummyObjects.newInstance(Professional.class);
        professional.setUserId(loggedUser.getUserId());
        when(professionalRepository.findById(10L)).thenReturn(Optional.of(professional));

        // When
        professionalService.deleteProfessional(10L);

        // Then
        verify(professionalRepository).deleteById(10L);
    }

    @Test
    @DisplayName("Should throw UnauthorizedAccessException when trying to delete someone else's professional")
    void deleteProfessional_whenProfessionalDoesNotBelongToUser_thenThrowUnauthorized() {
        // Given
        var professional = DummyObjects.newInstance(Professional.class);
        professional.setUserId(loggedUser.getUserId() + 1);
        when(professionalRepository.findById(10L)).thenReturn(Optional.of(professional));

        // When & then
        assertThatThrownBy(() -> professionalService.deleteProfessional(10L))
                .isInstanceOf(UnauthorizedAccessException.class)
                .hasMessageContaining("An error occurred while deleting professional");

        verify(professionalRepository, never()).deleteById(any());
    }
}