package com.bsg.trustedone.factory;

import com.bsg.trustedone.dto.ContactMethodCreationDto;
import com.bsg.trustedone.entity.ContactMethod;
import com.bsg.trustedone.entity.Partner;
import org.springframework.stereotype.Component;

@Component
public class ContactMethodFactory {

    public ContactMethod createEntity(ContactMethodCreationDto dto, Partner partner) {
        return ContactMethod.builder()
                .type(dto.getType())
                .info(dto.getInfo())
                .partner(partner)
                .build();
    }
}
