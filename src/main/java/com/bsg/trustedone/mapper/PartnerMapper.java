package com.bsg.trustedone.mapper;

import com.bsg.trustedone.dto.PartnerDto;
import com.bsg.trustedone.dto.PartnerListingDto;
import com.bsg.trustedone.entity.Partner;
import com.bsg.trustedone.entity.Referral;
import com.bsg.trustedone.enums.ReferralStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PartnerMapper {

    private final GroupMapper groupMapper;
    private final CompanyMapper companyMapper;
    private final ExpertiseMapper expertiseMapper;
    private final GainsProfileMapper gainsProfileMapper;
    private final ContactMethodMapper contactMethodMapper;
    private final BusinessProfileMapper businessProfileMapper;

    public PartnerDto toDto(Partner entity) {
        return PartnerDto.builder()
                .partnerId(entity.getPartnerId())
                .name(entity.getName())
                .company(Optional.ofNullable(entity.getCompany())
                        .map(companyMapper::toDto)
                        .orElse(null))
                .group(Optional.ofNullable(entity.getGroup())
                        .map(groupMapper::toDto)
                        .orElse(null))
                .contactMethods(entity.getContactMethods()
                        .stream()
                        .map(contactMethodMapper::toDto)
                        .toList())
                .expertises(entity.getPartnerExpertises()
                        .stream()
                        .map(expertiseMapper::toDto)
                        .toList())
                .gainsProfile(entity.getGainsProfile()
                        .stream()
                        .map(gainsProfileMapper::toDto)
                        .toList())
                .businessProfile(entity.getBusinessProfile()
                        .stream()
                        .map(businessProfileMapper::toDto)
                        .toList())
                .build();
    }

    public PartnerListingDto toListingDto(Partner partner) {
        var counts = partner.getReferrals()
                .stream()
                .map(Referral::getStatus)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return PartnerListingDto.builder()
                .partnerId(partner.getPartnerId())
                .name(partner.getName())
                .group(Optional.ofNullable(partner.getGroup())
                        .map(groupMapper::toDto)
                        .orElse(null))
                .company(Optional.ofNullable(partner.getCompany())
                        .map(companyMapper::toDto)
                        .orElse(null))
                .metrics(PartnerListingDto.PartnerMetricsDto.builder()
                        .acceptedReferrals(counts.get(ReferralStatus.ACCEPTED).intValue())
                        .rejectedReferrals(counts.get(ReferralStatus.DECLINED).intValue())
                        .pendingReferrals(counts.get(ReferralStatus.PENDING).intValue())
                        .build())
                .build();
    }
}
