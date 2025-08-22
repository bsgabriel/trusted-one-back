package com.bsg.trustedone.repository;

import com.bsg.trustedone.entity.Partner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, Long> {

    List<Partner> findAllByUserId(Long userId);
}
