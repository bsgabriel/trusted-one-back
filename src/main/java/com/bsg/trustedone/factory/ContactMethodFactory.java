package com.bsg.trustedone.factory;

import com.bsg.trustedone.dto.ContactMethodCreationDto;
import com.bsg.trustedone.entity.ContactMethod;
import com.bsg.trustedone.entity.Professional;
import org.springframework.stereotype.Component;

@Component
public class ContactMethodFactory {

    public ContactMethod createEntity(ContactMethodCreationDto dto, Professional professional) {
        return ContactMethod.builder()
                .type(dto.getType())
                .info(dto.getInfo())
                .professional(professional)
                .build();
    }
}
