package com.bsg.trustedone.factory;

import com.bsg.trustedone.dto.*;
import com.bsg.trustedone.entity.Partner;
import com.bsg.trustedone.entity.PartnerExpertise;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PartnerFactory {

    private final GroupFactory groupFactory;
    private final CompanyFactory companyFactory;
    private final ExpertiseFactory expertiseFactory;
    private final ContactMethodFactory contactMethodFactory;

    public Partner createEntity(PartnerCreationDto partner, GroupDto group, CompanyDto company, UserDto loggedUser, List<ContactMethodCreationDto> contactMethods, List<ExpertiseDto> expertises) {
        var entity = Partner.builder()
                .name(partner.getName())
                .userId(loggedUser.getUserId())
                .group(groupFactory.createEntity(group, loggedUser))
                .company(companyFactory.createEntity(company, loggedUser.getUserId()))
                .build();

        entity.setContactMethods(contactMethods.stream()
                .map(c -> contactMethodFactory.createEntity(c, entity))
                .toList());

        entity.setPartnerExpertises(expertises.stream()
                .map(p -> createPartnerExpertise(p, entity, loggedUser))
                .toList());

        return entity;
    }

    private PartnerExpertise createPartnerExpertise(ExpertiseDto expertiseDto, Partner partner, UserDto loggedUser) {
        return PartnerExpertise.builder()
                .expertise(expertiseFactory.createEntity(expertiseDto, loggedUser))
                .partner(partner)
                .availableForReferrals(expertiseDto.isAvailableForReferrals())
                .build();
    }

}
