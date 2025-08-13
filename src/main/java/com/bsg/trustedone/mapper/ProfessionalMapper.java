package com.bsg.trustedone.mapper;

import com.bsg.trustedone.dto.ProfessionalDto;
import com.bsg.trustedone.entity.Professional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfessionalMapper {

    private final GroupMapper groupMapper;
    private final CompanyMapper companyMapper;
    private final ProfessionMapper professionMapper;
    private final ContactMethodMapper contactMethodMapper;

    public ProfessionalDto toDto(Professional entity) {
        return ProfessionalDto.builder()
                .professionalId(entity.getProfessionalId())
                .name(entity.getName())
                .company(companyMapper.toDto(entity.getCompany()))
                .group(groupMapper.toDto(entity.getGroup()))
                .contactMethods(entity.getContactMethods()
                        .stream()
                        .map(contactMethodMapper::toDto)
                        .toList())
                .professions(entity.getProfessionalProfessions()
                        .stream()
                        .map(professionMapper::toDto)
                        .toList())
                .build();
    }

}
