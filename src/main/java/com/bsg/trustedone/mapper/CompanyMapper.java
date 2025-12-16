package com.bsg.trustedone.mapper;

import com.bsg.trustedone.dto.CompanyCreationDto;
import com.bsg.trustedone.dto.CompanyDto;
import com.bsg.trustedone.dto.CompanyListingDto;
import com.bsg.trustedone.entity.Company;
import com.bsg.trustedone.projection.CompanyListingProjection;
import com.bsg.trustedone.projection.CompanyWithPartnersProjection;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
public class CompanyMapper {

    public CompanyDto toDto(Company company) {
        return CompanyDto.builder()
                .companyId(company.getCompanyId())
                .name(company.getName())
                .image(company.getImage())
                .build();
    }

    public CompanyDto toDto(List<CompanyWithPartnersProjection> projections) {
        var first = projections.getFirst();

        return CompanyDto.builder()
                .companyId(first.getCompanyId())
                .name(first.getCompanyName())
                .partners(projections
                        .stream()
                        .filter(p -> p.getPartnerId() != null && isNotBlank(p.getPartnerName()))
                        .map(p -> CompanyDto.CompanyPartnerDto.builder()
                                .partnerId(p.getPartnerId())
                                .name(p.getPartnerName())
                                .build())
                        .toList())
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
