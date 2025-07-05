package com.bsg.trustedone.repository;

import com.bsg.trustedone.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    boolean existsByNameAndUserId(String name, Long userId);
}
