package com.bsg.trustedone.mapper;

import com.bsg.trustedone.dto.GainsProfileDto;
import com.bsg.trustedone.entity.GainsProfile;
import org.springframework.stereotype.Component;

@Component
public class GainsProfileMapper {

    public GainsProfileDto toDto(GainsProfile entity) {
        return GainsProfileDto.builder()
                .gainsProfileId(entity.getGainsProfileId())
                .category(entity.getCategory())
                .info(entity.getInfo())
                .build();
    }

}
