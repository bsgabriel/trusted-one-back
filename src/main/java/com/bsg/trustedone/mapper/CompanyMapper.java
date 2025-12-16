package com.bsg.trustedone.mapper;

import com.bsg.trustedone.dto.CompanyCreationDto;
import com.bsg.trustedone.dto.CompanyDto;
import com.bsg.trustedone.dto.CompanyListingDto;
import com.bsg.trustedone.entity.Company;
import com.bsg.trustedone.projection.CompanyListingProjection;
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

    public CompanyListingDto toListingDto(CompanyListingProjection projection) {
        return CompanyListingDto.builder()
                .companyId(projection.getCompanyId())
                .name(projection.getName())
                .partnerCount(projection.getPartnerCount() == null ? 0 : projection.getPartnerCount())
                .build();
    }
}
