package com.bsg.trustedone.factory;

import com.bsg.trustedone.dto.CompanyDto;
import com.bsg.trustedone.dto.GroupDto;
import com.bsg.trustedone.dto.ProfessionalCreationDto;
import com.bsg.trustedone.dto.UserDto;
import com.bsg.trustedone.entity.Professional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfessionalFactory {

    private final GroupFactory groupFactory;
    private final CompanyFactory companyFactory;

    public Professional createEntity(ProfessionalCreationDto professional, GroupDto group, CompanyDto company, UserDto loggedUser) {
        return Professional.builder()
                .name(professional.getName())
                .userId(loggedUser.getUserId())
                .group(groupFactory.createEntity(group, loggedUser))
                .company(companyFactory.createEntity(company, loggedUser.getUserId()))
                .build();
    }
}
