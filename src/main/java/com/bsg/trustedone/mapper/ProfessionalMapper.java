package com.bsg.trustedone.mapper;

import com.bsg.trustedone.dto.ProfessionalDto;
import com.bsg.trustedone.entity.Professional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProfessionalMapper {

    private final GroupMapper groupMapper;
    private final CompanyMapper companyMapper;

    public ProfessionalDto toDto(Professional entity) {
        return ProfessionalDto.builder()
                .professionalId(entity.getProfessionalId())
                .name(entity.getName())
                .company(companyMapper.toDto(entity.getCompany()))
                .group(groupMapper.toDto(entity.getGroup()))
                .professions(List.of())// TODO: mapear quando tiver esse dado
                .build();
    }

}
