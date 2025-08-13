package com.bsg.trustedone.factory;

import com.bsg.trustedone.dto.*;
import com.bsg.trustedone.entity.Professional;
import com.bsg.trustedone.entity.ProfessionalProfession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProfessionalFactory {

    private final GroupFactory groupFactory;
    private final CompanyFactory companyFactory;
    private final ProfessionFactory professionFactory;
    private final ContactMethodFactory contactMethodFactory;

    public Professional createEntity(ProfessionalCreationDto professional, GroupDto group, CompanyDto company, UserDto loggedUser, List<ContactMethodCreationDto> contactMethods, List<ProfessionDto> professions) {
        var entity = Professional.builder()
                .name(professional.getName())
                .userId(loggedUser.getUserId())
                .group(groupFactory.createEntity(group, loggedUser))
                .company(companyFactory.createEntity(company, loggedUser.getUserId()))
                .build();

        entity.setContactMethods(contactMethods.stream()
                .map(c -> contactMethodFactory.createEntity(c, entity))
                .toList());

        entity.setProfessionalProfessions(professions.stream()
                .map(p -> createProfessionalProfession(p, entity, loggedUser))
                .toList());

        return entity;
    }

    private ProfessionalProfession createProfessionalProfession(ProfessionDto professionDto, Professional professional, UserDto loggedUser) {
        return ProfessionalProfession.builder()
                .profession(professionFactory.createEntity(professionDto, loggedUser))
                .professional(professional)
                .availableForReferrals(professionDto.isAvailableForReferrals())
                .build();
    }

}
