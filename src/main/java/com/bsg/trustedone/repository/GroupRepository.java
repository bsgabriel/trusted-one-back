package com.bsg.trustedone.repository;

import com.bsg.trustedone.entity.Group;
import com.bsg.trustedone.projection.GroupListingProjection;
import com.bsg.trustedone.projection.GroupWithPartnersProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    boolean existsByNameAndUserId(String name, Long userId);

    List<Group> findAllByUserIdOrderByName(Long userId);

    @Query("""
            select
                group.groupId as groupId,
                group.name as name,
                group.description as description,
                COUNT(partner) as partnerCount
            from
                Group group
            left join Partner partner on
                partner.group.groupId = group.groupId
                and partner.active
            where
                group.userId = :userId
                AND (
                    COALESCE(:name, '') = ''
                    OR LOWER(group.name) LIKE LOWER(CONCAT('%', :name, '%'))
                )
            group by
                group.groupId,
                group.name,
                group.description
            """)
    Page<GroupListingProjection> listGroups(Long userId, String name, Pageable pageable);

    @Query("""
            select
                group.groupId as groupId,
                group.name as groupName,
                group.description as groupDescription,
                partner.partnerId as partnerId,
                partner.name as partnerName
            from
                Group group
            left join Partner partner ON
                partner.group.id = group.groupId
                and partner.active
            where
                group.groupId = :id
            order by
                partner.name
            """)
    List<GroupWithPartnersProjection> findGroupWithPartners(Long id);

    @Modifying
    @Query("""
            delete from
                Group g
            where
                g.groupId = :groupId
                and g.userId = :userId
            """)
    void deleteByGroupIdAndUserId(@Param("groupId") Long groupId, @Param("userId") Long userId);

}
