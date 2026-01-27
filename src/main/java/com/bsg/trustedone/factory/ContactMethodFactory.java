package com.bsg.trustedone.factory;

import com.bsg.trustedone.dto.ContactMethodFormDto;
import com.bsg.trustedone.entity.ContactMethod;
import com.bsg.trustedone.entity.Partner;
import org.springframework.stereotype.Component;

@Component
public class ContactMethodFactory {

    public ContactMethod createEntity(ContactMethodFormDto dto, Partner partner) {
        return ContactMethod.builder()
                .type(dto.getType())
                .info(dto.getInfo())
                .partner(partner)
                .build();
    }
}
