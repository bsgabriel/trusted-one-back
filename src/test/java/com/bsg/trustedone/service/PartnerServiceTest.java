package com.bsg.trustedone.service;

import com.bsg.trustedone.dto.*;
import com.bsg.trustedone.entity.Partner;
import com.bsg.trustedone.exception.UnauthorizedAccessException;
import com.bsg.trustedone.factory.PartnerFactory;
import com.bsg.trustedone.helper.DummyObjects;
import com.bsg.trustedone.helper.RandomUtils;
import com.bsg.trustedone.mapper.PartnerMapper;
import com.bsg.trustedone.repository.PartnerRepository;
import com.bsg.trustedone.validator.PartnerValidator;
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
class PartnerServiceTest {

    @InjectMocks
    private PartnerService partnerService;

    @Mock
    private ExpertiseService expertiseService;

    @Mock
    private CompanyService companyService;

    @Mock
    private UserService userService;

    @Mock
    private GroupService groupService;

    @Mock
    private PartnerFactory partnerFactory;

    @Mock
    private PartnerRepository partnerRepository;

    @Mock
    private PartnerValidator partnerValidator;

    @Mock
    private PartnerMapper partnerMapper;

    private UserDto loggedUser;

    @BeforeEach
    public void beforeAll() {
        lenient().when(partnerRepository.save(any(Partner.class))).then(invocation -> {
            var created = (Partner) invocation.getArguments()[0];
            created.setPartnerId(RandomUtils.nextLong(1, 999));
            return created;
        });

        this.loggedUser = DummyObjects.newInstance(UserDto.class);
        lenient().when(userService.getLoggedUser()).thenReturn(loggedUser);
    }

    @Test
    @DisplayName("Should create a partner successfully when data is valid")
    void createPartner_whenValidData_thenReturnSavedPartnerDto() {
        // Given
        var creationDto = PartnerCreationDto.builder()
                .group(GroupDto.builder().build())
                .company(CompanyDto.builder().build())
                .expertises(List.of(ExpertiseDto.builder()
                        .expertiseId(10L)
                        .name("IT").availableForReferral(true)
                        .build()))
                .contactMethods(List.of(ContactMethodCreationDto.builder().build()))
                .build();

        var group = GroupDto.builder().groupId(1L).build();
        var company = CompanyDto.builder().companyId(2L).build();
        var expertise = ExpertiseDto.builder()
                .expertiseId(10L)
                .name("IT")
                .availableForReferral(true)
                .build();

        var entity = Partner.builder().build();
        var savedEntity = Partner.builder().build();
        var dto = PartnerDto.builder().partnerId(99L).build();

        when(groupService.findOrCreateGroup(any())).thenReturn(group);
        when(companyService.findOrCreateCompany(any())).thenReturn(company);
        when(expertiseService.findOrCreateExpertise(any())).thenReturn(expertise);
        when(partnerFactory.createEntity(any(), eq(group), eq(company), eq(loggedUser), any(), any(), any(), any())).thenReturn(entity);
        when(partnerRepository.save(entity)).thenReturn(savedEntity);
        when(partnerMapper.toDto(savedEntity)).thenReturn(dto);

        // When
        var result = partnerService.createPartner(null, creationDto);

        // Then
        assertThat(result).isEqualTo(dto);
        verify(partnerValidator).validatePartnerCreation(creationDto);
    }

    @Test
    @DisplayName("Should handle null group when creating a partner")
    void createPartner_whenGroupIsNull_thenHandleNullGroup() {
        // Given
        var creationDto = PartnerCreationDto.builder()
                .group(null)
                .company(CompanyDto.builder().build())
                .expertises(List.of())
                .contactMethods(List.of())
                .build();

        var emptyGroup = GroupDto.builder().build();
        when(groupService.findOrCreateGroup(null)).thenReturn(emptyGroup);
        when(companyService.findOrCreateCompany(any())).thenReturn(CompanyDto.builder().build());
        when(partnerRepository.save(any())).thenReturn(Partner.builder().build());
        when(partnerMapper.toDto(any())).thenReturn(PartnerDto.builder().build());
        when(partnerFactory.createEntity(any(), any(), any(), eq(loggedUser), any(), any(), any(), any())).thenReturn(Partner.builder().build());

        // When
        var result = partnerService.createPartner(null, creationDto);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should handle null company when creating a partner")
    void createPartner_whenCompanyIsNull_thenHandleNullCompany() {
        // Given
        var creationDto = PartnerCreationDto.builder()
                .group(GroupDto.builder().build())
                .company(null)
                .expertises(List.of())
                .contactMethods(List.of())
                .build();

        var emptyCompany = CompanyDto.builder().build();
        when(groupService.findOrCreateGroup(any())).thenReturn(GroupDto.builder().build());
        when(companyService.findOrCreateCompany(null)).thenReturn(emptyCompany);
        when(partnerRepository.save(any())).thenReturn(Partner.builder().build());
        when(partnerMapper.toDto(any())).thenReturn(PartnerDto.builder().build());
        when(partnerFactory.createEntity(any(), any(), any(), eq(loggedUser), any(), any(), any(), any())).thenReturn(Partner.builder().build());

        // When
        var result = partnerService.createPartner(null, creationDto);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should do nothing when trying to delete a non-existing partner")
    void deletePartner_whenPartnerNotFound_thenDoNothing() {
        // Given
        when(partnerRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        partnerService.deletePartner(99L);

        // Then
        verify(partnerRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should delete partner when it belongs to the logged user")
    void deletePartner_whenPartnerBelongsToUser_thenDelete() {
        // Given
        var partner = DummyObjects.newInstance(Partner.class);
        partner.setUserId(loggedUser.getUserId());
        when(partnerRepository.findById(10L)).thenReturn(Optional.of(partner));

        // When
        partnerService.deletePartner(10L);

        // Then
        verify(partnerRepository).deleteById(10L);
    }

    @Test
    @DisplayName("Should throw UnauthorizedAccessException when trying to delete someone else's partner")
    void deletePartner_whenPartnerDoesNotBelongToUser_thenThrowUnauthorized() {
        // Given
        var partner = DummyObjects.newInstance(Partner.class);
        partner.setUserId(loggedUser.getUserId() + 1);
        when(partnerRepository.findById(10L)).thenReturn(Optional.of(partner));

        // When & then
        assertThatThrownBy(() -> partnerService.deletePartner(10L))
                .isInstanceOf(UnauthorizedAccessException.class)
                .hasMessageContaining("An error occurred while deleting partner");

        verify(partnerRepository, never()).deleteById(any());
    }
}