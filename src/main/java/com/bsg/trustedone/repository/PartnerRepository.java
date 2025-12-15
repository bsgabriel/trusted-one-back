package com.bsg.trustedone.repository;

import com.bsg.trustedone.entity.Partner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, Long>, JpaSpecificationExecutor<Partner> {

    List<Partner> findAllByUserId(Long userId);

    @Modifying
    @Query("""
            update
                Partner p
            set
                p.group = null
            where
                p.group.groupId = :groupId
                and p.userId = :userId
            """)
    void removePartnersFromGroup(@Param("groupId") Long groupId, @Param("userId") Long userId);

    @Modifying
    @Query("""
            update
                Partner p
            set
                p.group.groupId = :groupId
            where
                p.partnerId in :partnerIds
            """)
    void addPartnersToGroup(List<Long> partnerIds, Long groupId);
}
