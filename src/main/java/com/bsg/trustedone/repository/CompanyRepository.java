package com.bsg.trustedone.repository;

import com.bsg.trustedone.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    boolean existsByNameAndUserId(String name, Long userId);

}
