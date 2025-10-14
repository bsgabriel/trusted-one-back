package com.bsg.trustedone.factory;

import com.bsg.trustedone.dto.GainsProfileDto;
import com.bsg.trustedone.entity.GainsProfile;
import com.bsg.trustedone.entity.Partner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GainsProfileFactory {

    public GainsProfile createEntity(GainsProfileDto dto, Partner partner) {
        return GainsProfile.builder()
                .gainsProfileId(dto.getGainsProfileId())
                .partner(partner)
                .category(dto.getCategory())
                .info(dto.getInfo())
                .build();
    }
}
