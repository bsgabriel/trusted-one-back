package com.bsg.trustedone.dto;

import com.bsg.trustedone.annotation.ValidContactMethod;
import com.bsg.trustedone.enums.ContactType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ValidContactMethod
public class ContactMethodFormDto {

    private Long contactMethodId;

    @NotNull(message = "{contactMethod.validation.type.required}")
    private ContactType type;

    @NotBlank(message = "{contactMethod.validation.info.required}")
    private String info;

    private Long partnerId;
}
