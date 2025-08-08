package com.bsg.trustedone.repository;


import com.bsg.trustedone.entity.Professional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfessionalRepository extends JpaRepository<Professional, Long> {

    List<Professional> findAllByUserId(Long userId);
}
