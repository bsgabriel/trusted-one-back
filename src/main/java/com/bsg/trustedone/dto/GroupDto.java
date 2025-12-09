package com.bsg.trustedone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupDto {

    private Long groupId;
    private String name;
    private String description;

    @Builder.Default
    private List<GroupPartnerDto> partners = new ArrayList<>();

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GroupPartnerDto {
        private Long partnerId;
        private String name;
    }
}
