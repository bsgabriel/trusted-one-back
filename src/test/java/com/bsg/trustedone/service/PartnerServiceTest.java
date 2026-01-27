package com.bsg.trustedone.service;

import com.bsg.trustedone.dto.*;
import com.bsg.trustedone.entity.Partner;
import com.bsg.trustedone.entity.PartnerExpertise;
import com.bsg.trustedone.exception.ResourceNotFoundException;
import com.bsg.trustedone.factory.PartnerFactory;
import com.bsg.trustedone.helper.DummyObjects;
import com.bsg.trustedone.mapper.ExpertiseMapper;
import com.bsg.trustedone.mapper.PartnerMapper;
import com.bsg.trustedone.repository.PartnerExpertiseRepository;
import com.bsg.trustedone.repository.PartnerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartnerServiceTest {

    @InjectMocks
    private PartnerService partnerService;

    @Mock
    private UserService userService;

    @Mock
    private GroupService groupService;

    @Mock
    private CompanyService companyService;

    @Mock
    private MessageService messageService;

    @Mock
    private ExpertiseService expertiseService;

    @Mock
    private PartnerMapper partnerMapper;

    @Mock
    private PartnerFactory partnerFactory;

    @Mock
    private PartnerRepository partnerRepository;

    @Mock
    private ExpertiseMapper expertiseMapper;

    @Mock
    private PartnerExpertiseRepository partnerExpertiseRepository;


    @Test
    @DisplayName("Should create partner successfully")
    void createPartner_withValidData_shouldCreatePartner() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        var expertise = DummyObjects.newInstance(AssignedExpertiseDto.class);
        var form = DummyObjects.newInstance(PartnerFormDto.class);
        form.setExpertises(List.of(expertise));
        var group = DummyObjects.newInstance(GroupDto.class);
        var company = DummyObjects.newInstance(CompanyDto.class);
        var entity = DummyObjects.newInstance(Partner.class);
        var dto = DummyObjects.newInstance(PartnerDto.class);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(groupService.findOrCreateGroup(form.getGroup())).thenReturn(group);
        when(companyService.findOrCreateCompany(form.getCompany())).thenReturn(company);
        when(expertiseService.findOrCreateExpertise(any())).thenReturn(expertise);
        when(partnerFactory.createEntity(
                form,
                group,
                company,
                loggedUser,
                form.getContactMethods(),
                form.getExpertises(),
                form.getGainsProfile(),
                form.getBusinessProfile()
        )).thenReturn(entity);
        when(partnerRepository.save(entity)).thenReturn(entity);
        when(partnerMapper.toDto(entity)).thenReturn(dto);

        var result = partnerService.createPartner(1L, form);

        assertNotNull(result);
        verify(partnerRepository).save(entity);
    }

    @Test
    @DisplayName("Should list partners with pagination")
    void listPartners_withValidSearch_shouldReturnPagedResult() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        var pageable = PageRequest.of(0, 10);
        var partner = DummyObjects.newInstance(Partner.class);
        var listing = DummyObjects.newInstance(PartnerListingDto.class);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(partnerRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(partner)));
        when(partnerMapper.toListingDto(partner)).thenReturn(listing);

        var result = partnerService.listPartners("test", pageable, true);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    @DisplayName("Should deactivate partner")
    void deletePartner_withValidId_shouldDeactivatePartner() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);

        when(userService.getLoggedUser()).thenReturn(loggedUser);

        partnerService.deletePartner(10L);

        verify(partnerRepository).deactivate(10L, loggedUser.getUserId());
    }

    @Test
    @DisplayName("Should return partner when active")
    void findPartner_withActivePartner_shouldReturnPartner() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        var partner = DummyObjects.newInstance(Partner.class);
        partner.setActive(true);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(partnerRepository.findByPartnerIdAndUserId(1L, loggedUser.getUserId())).thenReturn(Optional.of(partner));
        when(partnerMapper.toDto(partner)).thenReturn(DummyObjects.newInstance(PartnerDto.class));

        var result = partnerService.findPartner(1L);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Should throw exception when partner is inactive")
    void findPartner_withInactivePartner_shouldThrowException() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        var partner = DummyObjects.newInstance(Partner.class);
        partner.setActive(false);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(partnerRepository.findByPartnerIdAndUserId(1L, loggedUser.getUserId())).thenReturn(Optional.of(partner));
        when(messageService.getMessage(anyString())).thenReturn("error");

        assertThrows(ResourceNotFoundException.class, () -> partnerService.findPartner(1L));
    }

    @Test
    @DisplayName("Should return recommendable expertises")
    void findRecommendableExpertises_withValidPartner_shouldReturnList() {
        var projection = DummyObjects.newInstance(PartnerExpertise.class);
        var dto = DummyObjects.newInstance(AssignedExpertiseDto.class);

        when(partnerExpertiseRepository.findRecommendableExpertisesForPartner(1L)).thenReturn(List.of(projection));
        when(expertiseMapper.toDto(projection)).thenReturn(dto);

        var result = partnerService.findRecommendableExpertises(1L);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should remove partners from group")
    void removePartnersFromGroup_withValidGroup_shouldCallRepository() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);

        when(userService.getLoggedUser()).thenReturn(loggedUser);

        partnerService.removePartnersFromGroup(5L);

        verify(partnerRepository).removePartnersFromGroup(5L, loggedUser.getUserId());
    }

    @Test
    @DisplayName("Should add partners to group")
    void addPartnersToGroup_withValidData_shouldCallRepository() {
        partnerService.addPartnersToGroup(List.of(1L, 2L), 3L);
        verify(partnerRepository).addPartnersToGroup(List.of(1L, 2L), 3L);
    }

    @Test
    @DisplayName("Should remove partners from company")
    void removePartnersFromCompany_withValidCompany_shouldCallRepository() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);

        when(userService.getLoggedUser()).thenReturn(loggedUser);

        partnerService.removePartnersFromCompany(7L);

        verify(partnerRepository).removePartnersFromCompany(7L, loggedUser.getUserId());
    }

    @Test
    @DisplayName("Should add partners to company")
    void addPartnersToCompany_withValidData_shouldCallRepository() {
        partnerService.addPartnersToCompany(List.of(1L, 2L), 4L);

        verify(partnerRepository).addPartnersToCompany(List.of(1L, 2L), 4L);
    }
}
