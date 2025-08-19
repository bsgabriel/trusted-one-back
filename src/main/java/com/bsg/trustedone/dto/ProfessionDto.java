package com.bsg.trustedone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfessionDto {

    private Long parentProfessionId;
    private Long professionId;
    private String name;
    private boolean availableForReferrals;

}
