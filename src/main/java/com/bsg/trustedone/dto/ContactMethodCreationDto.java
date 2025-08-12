package com.bsg.trustedone.dto;

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
public class ContactMethodCreationDto {

    private Long contactMethodId;

    @NotNull(message = "Contact type not provided")
    private ContactType type;

    @NotBlank(message = "Contact information not provided")
    private String info;

    private Long professionalId;
}
