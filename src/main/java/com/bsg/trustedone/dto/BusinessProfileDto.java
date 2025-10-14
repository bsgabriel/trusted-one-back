package com.bsg.trustedone.dto;

import com.bsg.trustedone.entity.Partner;
import com.bsg.trustedone.enums.BusinessProfileCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BusinessProfileDto {

    private Long businessProfileId;
    private Partner partner;
    private BusinessProfileCategory category;
    private String info;
}
