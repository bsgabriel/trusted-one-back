package com.bsg.trustedone.dto.expertise;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpertiseDto {
    private Long expertiseId;
    private String name;
    private List<SpecializationListingDto> specializations;
}
