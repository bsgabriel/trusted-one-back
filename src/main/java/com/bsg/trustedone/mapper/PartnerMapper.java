package com.bsg.trustedone.mapper;

import com.bsg.trustedone.dto.PartnerDto;
import com.bsg.trustedone.entity.Partner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PartnerMapper {

    private final GroupMapper groupMapper;
    private final CompanyMapper companyMapper;
    private final ExpertiseMapper expertiseMapper;
    private final ContactMethodMapper contactMethodMapper;

    public PartnerDto toDto(Partner entity) {
        return PartnerDto.builder()
                .partnerId(entity.getPartnerId())
                .name(entity.getName())
                .company(companyMapper.toDto(entity.getCompany()))
                .group(groupMapper.toDto(entity.getGroup()))
                .contactMethods(entity.getContactMethods()
                        .stream()
                        .map(contactMethodMapper::toDto)
                        .toList())
                .expertises(entity.getPartnerExpertises()
                        .stream()
                        .map(expertiseMapper::toDto)
                        .toList())
                .build();
    }

}
