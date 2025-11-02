package com.bsg.trustedone.repository;

import com.bsg.trustedone.entity.PartnerExpertise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartnerExpertiseRepository extends JpaRepository<PartnerExpertise, Long> {

    @Query("""
            select
            	partnerExpertise
            from
            	PartnerExpertise as partnerExpertise
            where
            	partnerExpertise.partner.partnerId = :partnerId
            	and partnerExpertise.availableForReferral = true
            order by
            	partnerExpertise.expertise.name
            """)
    List<PartnerExpertise> findRecommendableExpertisesForPartner(Long partnerId);
}
