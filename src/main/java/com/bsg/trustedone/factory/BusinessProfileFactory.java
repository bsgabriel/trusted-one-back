package com.bsg.trustedone.factory;

import com.bsg.trustedone.dto.BusinessProfileDto;
import com.bsg.trustedone.entity.BusinessProfile;
import com.bsg.trustedone.entity.Partner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BusinessProfileFactory {

    public BusinessProfile createEntity(BusinessProfileDto dto, Partner partner) {
        return BusinessProfile.builder()
                .businessProfileId(dto.getBusinessProfileId())
                .partner(partner)
                .category(dto.getCategory())
                .info(dto.getInfo())
                .build();
    }
}
