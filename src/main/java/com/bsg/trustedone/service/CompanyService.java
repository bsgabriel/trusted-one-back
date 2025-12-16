package com.bsg.trustedone.service;

import com.bsg.trustedone.dto.CompanyFormDto;
import com.bsg.trustedone.dto.CompanyDto;
import com.bsg.trustedone.dto.CompanyListingDto;
import com.bsg.trustedone.dto.PageResponse;
import com.bsg.trustedone.exception.ResourceAlreadyExistsException;
import com.bsg.trustedone.exception.ResourceNotFoundException;
import com.bsg.trustedone.exception.UnauthorizedAccessException;
import com.bsg.trustedone.factory.CompanyFactory;
import com.bsg.trustedone.mapper.CompanyMapper;
import com.bsg.trustedone.repository.CompanyRepository;
import com.bsg.trustedone.validator.CompanyValidator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final UserService userService;
    private final CompanyMapper companyMapper;
    private final CompanyFactory companyFactory;
    private final CompanyValidator companyValidator;
    private final CompanyRepository companyRepository;
    private final ObjectProvider<PartnerService> partnerServiceProvider;

    public CompanyDto createCompany(CompanyFormDto company) {
        company.setName(company.getName().trim());
        companyValidator.validateCompanyCreate(company);

        var loggedUser = userService.getLoggedUser();

        if (companyRepository.existsByNameAndUserId(company.getName(), loggedUser.getUserId())) {
            throw new ResourceAlreadyExistsException("A company with this name already exists. Please choose a different name.");
        }

        var entity = companyFactory.createEntity(company, loggedUser.getUserId());
        return companyMapper.toDto(companyRepository.save(entity));
    }

    public List<CompanyDto> getAllCompanies() {
        var loggedUser = userService.getLoggedUser();
        return companyRepository.findAllByUserIdOrderByName(loggedUser.getUserId())
                .stream()
                .map(companyMapper::toDto)
                .toList();
    }

    @Transactional
    public void deleteCompany(Long companyId) {
        var loggedUserId = userService.getLoggedUser().getUserId();
        partnerServiceProvider.getObject().removePartnersFromCompany(companyId);
        companyRepository.deleteByCompanyIdAndUserId(companyId, loggedUserId);
    }

    @Transactional
    public CompanyDto updateCompany(CompanyFormDto request, Long companyId) {
        companyValidator.validateCompanyUpdate(request);

        var company = companyRepository.findById(companyId).orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        if (!company.getUserId().equals(userService.getLoggedUser().getUserId())) {
            throw new UnauthorizedAccessException("An error ocurred while updating company");
        }

        company.setName(request.getName());
        company.setImage(request.getImage());
        this.syncPartnersWithCompany(companyId, request.getPartners());
        return companyMapper.toDto(companyRepository.save(company));
    }

    public CompanyDto findOrCreateCompany(CompanyDto company) {
        if (isNull(company)) {
            return null;
        }

        if (isNull(company.getCompanyId())) {
            return this.createCompany(companyMapper.toCreationDto(company));
        }

        return this.companyRepository.findById(company.getCompanyId())
                .map(companyMapper::toDto)
                .orElseGet(() -> this.createCompany(companyMapper.toCreationDto(company)));
    }

    public PageResponse<CompanyListingDto> listCompanies(String search, Pageable pageable) {
        var loggedUser = userService.getLoggedUser();
        var sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("name").ascending());
        var searchParam = StringUtils.isBlank(search) ? null : search.trim();

        var page = companyRepository.listCompanies(loggedUser.getUserId(), searchParam, sortedPageable);
        return PageResponse.from(page.map(companyMapper::toListingDto));
    }

    public CompanyDto findById(Long companyId) {
        var companyProjections = companyRepository.findCompanyWithPartners(companyId);

        if (CollectionUtils.isEmpty(companyProjections)) {
            throw new ResourceNotFoundException("Company not found");
        }

        return companyMapper.toDto(companyProjections);
    }

    private void syncPartnersWithCompany(Long groupId, List<Long> partnerIds) {
        partnerServiceProvider.getObject().removePartnersFromCompany(groupId);

        if (!partnerIds.isEmpty()) {
            partnerServiceProvider.getObject().addPartnersToCompany(partnerIds, groupId);
        }
    }

}
