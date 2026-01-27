package com.bsg.trustedone.projection;

public interface ReferralStatsProjection {
    Long getTotal();
    Long getAccepted();
    Long getDeclined();
    Long getPending();
    Long getCurrentMonthCreated();
    Long getCurrentMonthResponses();
    Long getCurrentMonthAccepted();
    Long getCurrentMonthDeclined();
    Long getPreviousMonthCreated();
    Long getPreviousMonthResponses();
    Long getPreviousMonthAccepted();
    Long getPreviousMonthDeclined();
}
