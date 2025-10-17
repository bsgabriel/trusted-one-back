package com.bsg.trustedone.factory;

import com.bsg.trustedone.dto.*;
import com.bsg.trustedone.entity.Partner;
import com.bsg.trustedone.entity.PartnerExpertise;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PartnerFactory {

    private final GroupFactory groupFactory;
    private final CompanyFactory companyFactory;
    private final ExpertiseFactory expertiseFactory;
    private final GainsProfileFactory gainsProfileFactory;
    private final ContactMethodFactory contactMethodFactory;
    private final BusinessProfileFactory businessProfileFactory;

    public Partner createEntity(PartnerCreationDto partner, GroupDto group, CompanyDto company, UserDto loggedUser, List<ContactMethodCreationDto> contactMethods, List<ExpertiseDto> expertises, List<GainsProfileDto> gainsProfile, List<BusinessProfileDto> businessProfile) {
        var entity = Partner.builder()
                .name(partner.getName())
                .userId(loggedUser.getUserId())
                .group(Optional.ofNullable(group)
                        .map(g -> groupFactory.createEntity(g, loggedUser))
                        .orElse(null))
                .company(Optional.ofNullable(company)
                        .map(c -> companyFactory.createEntity(company, loggedUser.getUserId()))
                        .orElse(null))
                .build();

        entity.setContactMethods(contactMethods.stream()
                .map(c -> contactMethodFactory.createEntity(c, entity))
                .toList());

        entity.setPartnerExpertises(expertises.stream()
                .map(p -> createPartnerExpertise(p, entity, loggedUser))
                .toList());

        entity.setGainsProfile(Optional.ofNullable(gainsProfile)
                .orElse(new ArrayList<>())
                .stream()
                .map(g -> gainsProfileFactory.createEntity(g, entity))
                .toList());

        entity.setBusinessProfile(Optional.ofNullable(businessProfile)
                .orElse(new ArrayList<>())
                .stream()
                .map(b -> businessProfileFactory.createEntity(b, entity))
                .toList());
        return entity;
    }

    private PartnerExpertise createPartnerExpertise(ExpertiseDto expertiseDto, Partner partner, UserDto loggedUser) {
        return PartnerExpertise.builder()
                .expertise(expertiseFactory.createEntity(expertiseDto, loggedUser))
                .partner(partner)
                .availableForReferral(expertiseDto.isAvailableForReferral())
                .build();
    }

}
