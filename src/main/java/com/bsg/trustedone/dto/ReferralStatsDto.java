package com.bsg.trustedone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReferralStatsDto {

    private int total;
    private int accepted;
    private int declined;
    private int pending;

    private MonthlyStatsDto currentMonth;
    private MonthlyStatsDto previousMonth;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MonthlyStatsDto {
        private int created;
        private int responses;
        private int accepted;
        private int declined;
    }
}
