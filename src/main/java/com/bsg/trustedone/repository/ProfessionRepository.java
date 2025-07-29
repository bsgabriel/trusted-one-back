package com.bsg.trustedone.repository;

import com.bsg.trustedone.entity.Profession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfessionRepository extends JpaRepository<Profession, Long> {

    boolean existsByNameAndUserId(String name, Long userId);

    List<Profession> findAllByUserId(Long userId);

    boolean existsByNameAndParentProfessionId(String name, Long parentProfessionId);

    List<Profession> findByParentProfessionId(Long parentProfessionId);
}
