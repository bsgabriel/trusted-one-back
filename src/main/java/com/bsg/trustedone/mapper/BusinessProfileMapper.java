package com.bsg.trustedone.mapper;

import com.bsg.trustedone.dto.BusinessProfileDto;
import com.bsg.trustedone.entity.BusinessProfile;
import org.springframework.stereotype.Component;

@Component
public class BusinessProfileMapper {

    public BusinessProfileDto toDto(BusinessProfile entity) {
        return BusinessProfileDto.builder()
                .businessProfileId(entity.getBusinessProfileId())
                .category(entity.getCategory())
                .info(entity.getInfo())
                .build();
    }

}
