package com.bsg.trustedone.mapper;

import com.bsg.trustedone.dto.ContactMethodDto;
import com.bsg.trustedone.entity.ContactMethod;
import org.springframework.stereotype.Component;

@Component
public class ContactMethodMapper {

    public ContactMethodDto toDto(ContactMethod entity) {
        return ContactMethodDto.builder()
                .contactMethodId(entity.getContactMethodId())
                .type(entity.getType())
                .info(entity.getInfo())
                .build();
    }
}
