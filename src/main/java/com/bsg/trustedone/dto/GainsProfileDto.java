package com.bsg.trustedone.dto;

import com.bsg.trustedone.enums.GainsCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GainsProfileDto {

    private Long gainsProfileId;
    private GainsCategory category;
    private String info;
}

