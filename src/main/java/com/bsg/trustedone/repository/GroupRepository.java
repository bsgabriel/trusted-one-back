package com.bsg.trustedone.repository;

import com.bsg.trustedone.entity.Group;
import com.bsg.trustedone.projection.GroupListingProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

}
