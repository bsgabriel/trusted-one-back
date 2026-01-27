package com.bsg.trustedone.service;

import com.bsg.trustedone.dto.CompanyDto;
import com.bsg.trustedone.dto.CompanyFormDto;
import com.bsg.trustedone.dto.CompanyListingDto;
import com.bsg.trustedone.dto.UserDto;
import com.bsg.trustedone.entity.Company;
import com.bsg.trustedone.exception.ResourceAlreadyExistsException;
import com.bsg.trustedone.exception.ResourceNotFoundException;
import com.bsg.trustedone.factory.CompanyFactory;
import com.bsg.trustedone.helper.DummyObjects;
import com.bsg.trustedone.helper.RandomUtils;
import com.bsg.trustedone.mapper.CompanyMapper;
import com.bsg.trustedone.projection.CompanyListingProjection;
import com.bsg.trustedone.projection.CompanyWithPartnersProjection;
import com.bsg.trustedone.repository.CompanyRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @InjectMocks
    private CompanyService companyService;

    @Mock
    private UserService userService;

    @Mock
    private CompanyMapper companyMapper;

    @Mock
    private MessageService messageService;

    @Mock
    private CompanyFactory companyFactory;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private ObjectProvider<PartnerService> partnerServiceProvider;

    @Mock
    private PartnerService partnerService;

    @Test
    @DisplayName("Should create company successfully")
    void createCompany_withValidData_shouldCreateAndReturnCompanyDto() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        var partners = List.of(1L, 2L, 3L);
        var formDto = DummyObjects.newInstance(CompanyFormDto.class);
        formDto.setPartners(partners);

        var entity = DummyObjects.newInstance(Company.class);
        var dto = DummyObjects.newInstance(CompanyDto.class);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(companyRepository.existsByNameAndUserId(formDto.getName(), loggedUser.getUserId())).thenReturn(false);
        when(companyFactory.createEntity(formDto, loggedUser.getUserId())).thenReturn(entity);
        when(companyRepository.save(entity)).thenReturn(entity);
        when(companyMapper.toDto(entity)).thenReturn(dto);
        when(partnerServiceProvider.getObject()).thenReturn(partnerService);

        var result = companyService.createCompany(formDto);

        assertThat(result).isEqualTo(dto);
        verify(partnerService, times(1)).addPartnersToCompany(partners, entity.getCompanyId());
    }

    @Test
    @DisplayName("Should throw exception when company already exists")
    void createCompany_withExistingCompany_shouldThrowResourceAlreadyExistsException() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        var formDto = DummyObjects.newInstance(CompanyFormDto.class);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(companyRepository.existsByNameAndUserId(formDto.getName(), loggedUser.getUserId())).thenReturn(true);
        when(messageService.getMessage(anyString())).thenReturn("error");

        assertThatThrownBy(() -> companyService.createCompany(formDto)).isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @Test
    @DisplayName("Should remove partners and delete company")
    void deleteCompany_withValidCompanyId_shouldRemovePartnersAndDeleteCompany() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(partnerServiceProvider.getObject()).thenReturn(partnerService);

        companyService.deleteCompany(1L);

        verify(partnerService).removePartnersFromCompany(1L);
        verify(companyRepository).deleteByCompanyIdAndUserId(1L, loggedUser.getUserId());
    }

    @Test
    @DisplayName("Should throw exception when company is not found")
    void updateCompany_withInvalidCompanyId_shouldThrowResourceNotFoundException() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        var formDto = DummyObjects.newInstance(CompanyFormDto.class);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(companyRepository.findByCompanyIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(messageService.getMessage(anyString())).thenReturn("error");

        assertThatThrownBy(() -> companyService.updateCompany(formDto, 1L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should update company and sync partners successfully")
    void updateCompany_withValidData_shouldUpdateCompanyAndReturnDto() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        var formDto = DummyObjects.newInstance(CompanyFormDto.class);
        var company = DummyObjects.newInstance(Company.class);
        var dto = DummyObjects.newInstance(CompanyDto.class);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(companyRepository.findByCompanyIdAndUserId(company.getCompanyId(), loggedUser.getUserId())).thenReturn(Optional.of(company));
        when(companyRepository.save(company)).thenReturn(company);
        when(companyMapper.toDto(company)).thenReturn(dto);
        when(partnerServiceProvider.getObject()).thenReturn(partnerService);

        var result = companyService.updateCompany(formDto, company.getCompanyId());

        assertThat(company.getName()).isEqualTo(formDto.getName());
        assertThat(company.getImage()).isEqualTo(formDto.getImage());
        assertThat(result).isEqualTo(dto);

        verify(partnerService).removePartnersFromCompany(company.getCompanyId());
        if (!formDto.getPartners().isEmpty()) {
            verify(partnerService).addPartnersToCompany(formDto.getPartners(), company.getCompanyId());
        }
    }

    @Test
    @DisplayName("Should return null when company is null")
    void findOrCreateCompany_withNullCompany_shouldReturnNull() {
        var result = companyService.findOrCreateCompany(null);
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should create company when company id is null")
    void findOrCreateCompany_withNullCompanyId_shouldCreateCompany() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        var companyDto = DummyObjects.newInstance(CompanyDto.class);
        companyDto.setCompanyId(null);

        var formDto = DummyObjects.newInstance(CompanyFormDto.class);
        var createdDto = DummyObjects.newInstance(CompanyDto.class);

        when(companyMapper.toCreationDto(companyDto)).thenReturn(formDto);
        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(companyRepository.existsByNameAndUserId(any(), any())).thenReturn(false);
        when(companyFactory.createEntity(any(CompanyFormDto.class), any())).thenReturn(DummyObjects.newInstance(Company.class));
        when(companyRepository.save(any())).thenReturn(DummyObjects.newInstance(Company.class));
        when(companyMapper.toDto(any(Company.class))).thenReturn(createdDto);
        when(partnerServiceProvider.getObject()).thenReturn(partnerService);
        when(partnerServiceProvider.getObject()).thenReturn(partnerService);

        var result = companyService.findOrCreateCompany(companyDto);

        assertThat(result).isEqualTo(createdDto);
    }

    @Test
    @DisplayName("Should return existing company when found")
    void findOrCreateCompany_withExistingCompanyId_shouldReturnCompany() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        var companyDto = DummyObjects.newInstance(CompanyDto.class);
        var entity = DummyObjects.newInstance(Company.class);
        var mappedDto = DummyObjects.newInstance(CompanyDto.class);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(companyRepository.findByCompanyIdAndUserId(companyDto.getCompanyId(), loggedUser.getUserId())).thenReturn(Optional.of(entity));
        when(companyMapper.toDto(entity)).thenReturn(mappedDto);

        var result = companyService.findOrCreateCompany(companyDto);

        assertThat(result).isEqualTo(mappedDto);
    }

    @Test
    @DisplayName("Should list companies with pagination")
    void listCompanies_withValidRequest_shouldReturnPagedResult() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        var company = new CompanyListingProjection() {
            @Override
            public Long getCompanyId() {
                return 999L;
            }

            @Override
            public String getName() {
                return "company";
            }

            @Override
            public Integer getPartnerCount() {
                return RandomUtils.nextInt(0, 10);
            }
        };
        var listingDto = DummyObjects.newInstance(CompanyListingDto.class);

        Page<CompanyListingProjection> page = new PageImpl<>(List.of(company));

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(companyRepository.listCompanies(anyLong(), any(), any())).thenReturn(page);
        when(companyMapper.toListingDto(company)).thenReturn(listingDto);

        var result = companyService.listCompanies(null, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(listingDto);
    }

    @Test
    @DisplayName("Should throw exception when company is not found")
    void findById_withInvalidCompanyId_shouldThrowResourceNotFoundException() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(companyRepository.findCompanyWithPartners(anyLong(), anyLong())).thenReturn(Collections.emptyList());
        when(messageService.getMessage(anyString())).thenReturn("error");

        assertThatThrownBy(() -> companyService.findById(1L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should return company when found")
    void findById_withValidCompanyId_shouldReturnCompanyDto() {
        var loggedUser = DummyObjects.newInstance(UserDto.class);
        var projection = new CompanyWithPartnersProjection() {
            @Override
            public Long getCompanyId() {
                return 999L;
            }

            @Override
            public String getCompanyName() {
                return "company";
            }

            @Override
            public Long getPartnerId() {
                return 999L;
            }

            @Override
            public String getPartnerName() {
                return "partner";
            }
        };
        var dto = DummyObjects.newInstance(CompanyDto.class);

        when(userService.getLoggedUser()).thenReturn(loggedUser);
        when(companyRepository.findCompanyWithPartners(anyLong(), anyLong())).thenReturn(List.of(projection));
        when(companyMapper.toDto(any(List.class))).thenReturn(dto);

        var result = companyService.findById(1L);

        assertThat(result).isEqualTo(dto);
    }
}
