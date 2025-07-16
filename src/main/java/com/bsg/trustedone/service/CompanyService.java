package com.bsg.trustedone.service;

import com.bsg.trustedone.dto.CompanyCreationDto;
import com.bsg.trustedone.dto.CompanyDto;
import com.bsg.trustedone.exception.ResourceAlreadyExistsException;
import com.bsg.trustedone.exception.UnauthorizedAccessException;
import com.bsg.trustedone.factory.CompanyFactory;
import com.bsg.trustedone.mapper.CompanyMapper;
import com.bsg.trustedone.repository.CompanyRepository;
import com.bsg.trustedone.validator.CompanyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final UserService userService;
    private final CompanyMapper companyMapper;
    private final CompanyFactory companyFactory;
    private final CompanyValidator companyValidator;
    private final CompanyRepository companyRepository;

    public CompanyDto createCompany(CompanyCreationDto company) {
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
        return companyRepository.findAllByUserId(loggedUser.getUserId())
                .stream()
                .map(companyMapper::toDto)
                .toList();
    }

    public void deleteCompany(Long companyId) {
        var opt = companyRepository.findById(companyId);

        if (opt.isEmpty()) {
            return;
        }

        var loggedUser = userService.getLoggedUser();
        var company = opt.get();

        if (!company.getUserId().equals(loggedUser.getUserId())) {
            throw new UnauthorizedAccessException("An error ocurred while deleting company");
        }

        companyRepository.delete(company);
    }

    public Optional<CompanyDto> updateCopmany(CompanyCreationDto request, Long companyId) {
        companyValidator.validateCompanyUpdate(request);

        return companyRepository.findById(companyId)
                .map(company -> {
                    var loggedUserId = userService.getLoggedUser().getUserId();

                    if (!company.getUserId().equals(loggedUserId)) {
                        throw new UnauthorizedAccessException("An error ocurred while updating company");
                    }

                    company.setName(request.getName());
                    company.setImage(request.getImage());
                    return companyMapper.toDto(companyRepository.save(company));
                });
    }


}
