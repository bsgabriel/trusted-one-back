package com.bsg.trustedone.service;

import com.bsg.trustedone.dto.CompanyDto;
import com.bsg.trustedone.dto.CompanyFormDto;
import com.bsg.trustedone.dto.UserDto;
import com.bsg.trustedone.entity.Company;
import com.bsg.trustedone.exception.ResourceAlreadyExistsException;
import com.bsg.trustedone.exception.ResourceUpdateException;
import com.bsg.trustedone.exception.UnauthorizedAccessException;
import com.bsg.trustedone.factory.CompanyFactory;
import com.bsg.trustedone.helper.DummyObjects;
import com.bsg.trustedone.helper.RandomUtils;
import com.bsg.trustedone.mapper.CompanyMapper;
import com.bsg.trustedone.repository.CompanyRepository;
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
    private CompanyRepository companyRepository;

    @Mock
    private CompanyMapper companyMapper;

    @Mock
    private CompanyFactory companyFactory;

    @Mock
    private ObjectProvider<PartnerService> partnerServiceProvider;

    private UserDto loggedUser;

    @BeforeEach
    public void beforeAll() {
        lenient().when(companyMapper.toDto(any(Company.class))).thenCallRealMethod();
        lenient().when(companyFactory.createEntity(any(CompanyFormDto.class), any(Long.class))).thenCallRealMethod();
        lenient().when(companyMapper.toCreationDto(any(CompanyDto.class))).thenCallRealMethod();
        lenient().when(companyRepository.save(any(Company.class))).then(invocation -> {
            var created = (Company) invocation.getArguments()[0];
            created.setCompanyId(RandomUtils.nextLong(1, 999));
            return created;
        });

        this.loggedUser = DummyObjects.newInstance(UserDto.class);
        lenient().when(userService.getLoggedUser()).thenReturn(this.loggedUser);
        lenient().when(partnerServiceProvider.getObject()).thenReturn(Mockito.mock(PartnerService.class));
    }

    @Test
    @DisplayName("Should propagate exception when company creation validate fails")
    void companyCreation_withInvalidCompanyData_shouldPropagateValidationException() {
        // Given
        var companyCreationDto = CompanyFormDto.builder().build();

        // When & Then
        assertThatThrownBy(() -> companyService.createCompany(companyCreationDto))
                .isInstanceOf(ResourceAlreadyExistsException.class);

    }

    @Test
    @DisplayName("Should throw error if company already exist")
    void companyCreation_withAlreadyRegisteredName_shouldThrowException() {
        // Given
        var company = DummyObjects.newInstance(CompanyFormDto.class);

        when(companyRepository.existsByNameAndUserId(company.getName(), loggedUser.getUserId())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> companyService.createCompany(company))
                .isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @Test
    @DisplayName("Should create company successfully when data is valid")
    void createCompany_withValidData_shouldCreateCompanySuccessfully() {
        // Given
        var companyCreationDto = DummyObjects.newInstance(CompanyFormDto.class);

        when(companyRepository.existsByNameAndUserId(companyCreationDto.getName(), loggedUser.getUserId())).thenReturn(false);
        when(companyRepository.save(any(Company.class))).then(invocation -> invocation.getArguments()[0]);

        // When
        var result = companyService.createCompany(companyCreationDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(companyCreationDto.getName());
        assertThat(result.getImage()).isEqualTo(companyCreationDto.getImage());
    }

    @Test
    @DisplayName("Should remove partner from company")
    void deleteCompany_shouldRemovePartnerFromCompany() {
        // Given
        var companyOwner = DummyObjects.newInstance(UserDto.class);

        var company = DummyObjects.newInstance(Company.class);
        company.setUserId(companyOwner.getUserId());

        // When
        companyService.deleteCompany(company.getCompanyId());

        // Then
        verify(partnerServiceProvider.getObject(), times(1)).removePartnersFromCompany(company.getCompanyId());
    }

    @Test
    @DisplayName("Should propagate exception when company update validate fails")
    void companyUpdate_withInvalidCompanyData_shouldPropagateValidationException() {
        // Given
        var updateData = CompanyFormDto.builder().build();

        // When & Then
        assertThatThrownBy(() -> companyService.updateCompany(updateData, anyLong()))
                .isInstanceOf(ResourceUpdateException.class);
    }

    @Test
    @DisplayName("Should throw exception when updating company from another user")
    void updateCompany_withUserIdDifferentThanLogged_shouldThrowException() {
        // Given
        var companyOwner = DummyObjects.newInstance(UserDto.class);

        var companyId = 999L;
        var updateData = DummyObjects.newInstance(CompanyFormDto.class);
        var company = DummyObjects.newInstance(Company.class);

        company.setCompanyId(companyId);
        company.setUserId(companyOwner.getUserId());

        when(companyRepository.findById(company.getCompanyId())).thenReturn(Optional.of(company));

        // When & Then
        assertThatThrownBy(() -> companyService.updateCompany(updateData, companyId))
                .isInstanceOf(UnauthorizedAccessException.class);
    }

    @Test
    @DisplayName("Should successfully update company data")
    void updateCompany_shouldSuccessfullyUpdate() {
        // Given
        var companyId = 999L;
        var updateData = DummyObjects.newInstance(CompanyFormDto.class);
        var existingCompany = DummyObjects.newInstance(Company.class);

        existingCompany.setCompanyId(companyId);
        existingCompany.setUserId(loggedUser.getUserId());

        when(companyRepository.findById(existingCompany.getCompanyId())).thenReturn(Optional.of(existingCompany));

        // When
        var result = companyService.updateCompany(updateData, companyId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(updateData.getName());
        assertThat(result.getImage()).isEqualTo(updateData.getImage());
    }

    @Test
    @DisplayName("Should return null CompanyDto when company is null")
    void findOrCreateCompany_shouldReturnNullCompanyDto_whenCompanyIsNull() {
        // When
        var result = companyService.findOrCreateCompany(null);

        // Then
        assertThat(result).isNull();
        verifyNoInteractions(companyRepository, companyMapper);
    }

    @Test
    @DisplayName("Should create new company when companyId is null")
    void findOrCreateCompany_shouldCreateNewCompany_whenCompanyIdIsNull() {
        // Given
        var inputCompany = DummyObjects.newInstance(CompanyDto.class);
        inputCompany.setCompanyId(null);

        // When
        var result = companyService.findOrCreateCompany(inputCompany);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCompanyId()).isNotNull();
        assertThat(result.getName()).isEqualTo(inputCompany.getName());
        assertThat(result.getImage()).isEqualTo(inputCompany.getImage());

        verify(companyMapper).toCreationDto(inputCompany);
    }

    @Test
    @DisplayName("Should return existing company when found by ID")
    void findOrCreateCompany_shouldReturnExistingCompany_whenFoundById() {
        // Given
        var inputCompany = DummyObjects.newInstance(CompanyDto.class);
        var existingCompanyEntity = DummyObjects.newInstance(Company.class);
        existingCompanyEntity.setCompanyId(inputCompany.getCompanyId());

        when(companyRepository.findById(inputCompany.getCompanyId())).thenReturn(Optional.of(existingCompanyEntity));

        // When
        var result = companyService.findOrCreateCompany(inputCompany);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCompanyId()).isEqualTo(inputCompany.getCompanyId());
        assertThat(result.getName()).isEqualTo(existingCompanyEntity.getName());
        assertThat(result.getImage()).isEqualTo(existingCompanyEntity.getImage());

        verify(companyRepository).findById(inputCompany.getCompanyId());
        verify(companyMapper).toDto(existingCompanyEntity);
        verify(companyMapper, never()).toCreationDto(any());
    }

    @Test
    @DisplayName("Should create new company when not found by ID")
    void findOrCreateCompany_shouldCreateNewCompany_whenNotFoundById() {
        // Given
        var inputCompany = DummyObjects.newInstance(CompanyDto.class);

        // When
        var result = companyService.findOrCreateCompany(inputCompany);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCompanyId()).isNotNull();
        assertThat(result.getName()).isEqualTo(inputCompany.getName());
        assertThat(result.getImage()).isEqualTo(inputCompany.getImage());

        verify(companyRepository).findById(inputCompany.getCompanyId());
        verify(companyMapper).toCreationDto(inputCompany);
    }

    @Test
    @DisplayName("Should return CompanyDto with only non-null fields when company has only companyId")
    void findOrCreateCompany_shouldHandleCompany_withOnlyCompanyId() {
        // Given
        var inputCompany = CompanyDto.builder().companyId(999L).build();
        var existingCompanyEntity = DummyObjects.newInstance(Company.class);
        existingCompanyEntity.setCompanyId(inputCompany.getCompanyId());

        when(companyRepository.findById(inputCompany.getCompanyId())).thenReturn(Optional.of(existingCompanyEntity));

        // When
        var result = companyService.findOrCreateCompany(inputCompany);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCompanyId()).isEqualTo(inputCompany.getCompanyId());
        assertThat(result.getName()).isEqualTo(existingCompanyEntity.getName());
        assertThat(result.getImage()).isEqualTo(existingCompanyEntity.getImage());

        verify(companyRepository).findById(inputCompany.getCompanyId());
        verify(companyMapper).toDto(existingCompanyEntity);
    }
}