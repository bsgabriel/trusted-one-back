package com.bsg.trustedone.factory;

import com.bsg.trustedone.dto.CompanyFormDto;
import com.bsg.trustedone.dto.CompanyDto;
import com.bsg.trustedone.entity.Company;
import org.springframework.stereotype.Component;

@Component
public class CompanyFactory {

    public Company createEntity(CompanyFormDto company, Long userId) {
        return Company.builder()
                .userId(userId)
                .name(company.getName())
                .image(company.getImage())
                .build();
    }

    public Company createEntity(CompanyDto company, Long userId) {
        return Company.builder()
                .companyId(company.getCompanyId())
                .userId(userId)
                .name(company.getName())
                .image(company.getImage())
                .build();
    }

}
