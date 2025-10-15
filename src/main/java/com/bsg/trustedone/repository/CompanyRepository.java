package com.bsg.trustedone.repository;

import com.bsg.trustedone.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    boolean existsByNameAndUserId(String name, Long userId);

    List<Company> findAllByUserIdOrderByName(Long userId);

}
