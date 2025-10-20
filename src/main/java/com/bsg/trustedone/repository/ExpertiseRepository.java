package com.bsg.trustedone.repository;

import com.bsg.trustedone.entity.Expertise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpertiseRepository extends JpaRepository<Expertise, Long> {

    boolean existsByNameAndUserId(String name, Long userId);

    List<Expertise> findAllByUserId(Long userId);

    boolean existsByNameAndParentExpertiseExpertiseId(String name, Long parentExpertiseId);

    List<Expertise> findByParentExpertiseExpertiseId(Long parentExpertiseId);

    List<Expertise> findByUserIdAndParentExpertiseExpertiseIdOrderByName(Long userId, Long parentExpertiseId);

    Optional<Expertise> findByNameAndUserId(String name, Long userId);
}
