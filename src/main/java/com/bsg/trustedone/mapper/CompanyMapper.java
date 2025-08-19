package com.bsg.trustedone.mapper;

import com.bsg.trustedone.dto.CompanyCreationDto;
import com.bsg.trustedone.dto.CompanyDto;
import com.bsg.trustedone.entity.Company;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

    public CompanyDto toDto(Company company) {
        return CompanyDto.builder()
                .companyId(company.getCompanyId())
                .name(company.getName())
                .image(company.getImage())
                .build();
    }

    public CompanyCreationDto toCreationDto(CompanyDto company) {
        return CompanyCreationDto.builder()
                .name(company.getName())
                .image(company.getImage())
                .build();
    }
}
