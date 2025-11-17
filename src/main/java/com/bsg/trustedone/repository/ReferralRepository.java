package com.bsg.trustedone.repository;

import com.bsg.trustedone.entity.Referral;
import com.bsg.trustedone.projection.ReferralStatsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferralRepository extends JpaRepository<Referral, Long>, JpaSpecificationExecutor<Referral> {

    @Query(value = """
            SELECT
                COUNT(*) as total,
                COUNT(*) FILTER (WHERE status = 'ACCEPTED') as accepted,
                COUNT(*) FILTER (WHERE status = 'DECLINED') as declined,
                COUNT(*) FILTER (WHERE status = 'PENDING') as pending,

                COUNT(*) FILTER (WHERE DATE_TRUNC('month', created_at) = DATE_TRUNC('month', CURRENT_DATE)) as current_month_created,
                COUNT(*) FILTER (WHERE DATE_TRUNC('month', updated_at) = DATE_TRUNC('month', CURRENT_DATE) AND status != 'PENDING') as current_month_responses,
                COUNT(*) FILTER (WHERE DATE_TRUNC('month', updated_at) = DATE_TRUNC('month', CURRENT_DATE) AND status = 'ACCEPTED') as current_month_accepted,
                COUNT(*) FILTER (WHERE DATE_TRUNC('month', updated_at) = DATE_TRUNC('month', CURRENT_DATE) AND status = 'DECLINED') as current_month_declined,

                COUNT(*) FILTER (WHERE DATE_TRUNC('month', created_at) = DATE_TRUNC('month', CURRENT_DATE - INTERVAL '1 month')) as previous_month_created,
                COUNT(*) FILTER (WHERE DATE_TRUNC('month', updated_at) = DATE_TRUNC('month', CURRENT_DATE - INTERVAL '1 month') AND status != 'PENDING') as previous_month_responses,
                COUNT(*) FILTER (WHERE DATE_TRUNC('month', updated_at) = DATE_TRUNC('month', CURRENT_DATE - INTERVAL '1 month') AND status = 'ACCEPTED') as previous_month_accepted,
                COUNT(*) FILTER (WHERE DATE_TRUNC('month', updated_at) = DATE_TRUNC('month', CURRENT_DATE - INTERVAL '1 month') AND status = 'DECLINED') as previous_month_declined
            FROM referrals
            WHERE user_id = :userId
            """, nativeQuery = true)
    ReferralStatsProjection getReferralStats(Long userId);
}
