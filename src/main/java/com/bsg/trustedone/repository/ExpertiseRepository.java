package com.bsg.trustedone.repository;

import com.bsg.trustedone.entity.Expertise;
import com.bsg.trustedone.projection.SpecializationListingProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpertiseRepository extends JpaRepository<Expertise, Long>, JpaSpecificationExecutor<Expertise> {

    boolean existsByNameAndUserId(String name, Long userId);

    List<Expertise> findByParentExpertiseExpertiseId(Long parentExpertiseId);

    Optional<Expertise> findByNameAndUserId(String name, Long userId);

    @Query("""
            select
                e.parentExpertise.expertiseId as parentExpertiseId,
                e.expertiseId as expertiseId,
                e.name as name,
                count(pe) as partnerCount
            from
                Expertise e
                left join e.partnerExpertises pe
            where
                e.parentExpertise.expertiseId = :parentExpertiseId
                and e.userId = :userId
            group by
                e.parentExpertise.expertiseId, e.expertiseId, e.name
            order by
                e.name
            """)
    List<SpecializationListingProjection> listSpecializations(Long parentExpertiseId, Long userId);
}
