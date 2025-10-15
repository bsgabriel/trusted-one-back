package com.bsg.trustedone.repository;

import com.bsg.trustedone.entity.Expertise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpertiseRepository extends JpaRepository<Expertise, Long> {

    boolean existsByNameAndUserId(String name, Long userId);

    List<Expertise> findAllByUserId(Long userId);

    boolean existsByNameAndParentExpertiseId(String name, Long parentExpertiseId);

    List<Expertise> findByParentExpertiseId(Long parentExpertiseId);

    List<Expertise> findByUserIdAndParentExpertiseIdOrderByName(Long userId, Long parentExpertiseId);
}
