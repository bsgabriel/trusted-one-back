package com.bsg.trustedone.repository;

import com.bsg.trustedone.entity.Company;
import com.bsg.trustedone.projection.CompanyListingProjection;
import com.bsg.trustedone.projection.CompanyWithPartnersProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    boolean existsByNameAndUserId(String name, Long userId);

    List<Company> findAllByUserIdOrderByName(Long userId);

    @Query("""
            select
                company.companyId as companyId,
                company.name as name,
                COUNT(partner) as partnerCount
            from
                Company company
            left join Partner partner on
                partner.company.companyId = company.companyId
                and partner.active
            where
                company.userId = :userId
                AND (
                    COALESCE(:name, '') = ''
                    OR LOWER(company.name) LIKE LOWER(CONCAT('%', :name, '%'))
                )
            group by
                company.companyId,
                company.name
            """)
    Page<CompanyListingProjection> listCompanies(Long userId, String name, Pageable pageable);

    @Query("""
            select
                company.companyId as companyId,
                company.name as companyName,
                partner.partnerId as partnerId,
                partner.name as partnerName
            from
                Company company
            left join Partner partner ON
                partner.company.id = company.companyId
                and partner.active
            where
                company.companyId = :companyId
            order by
                partner.name
            """)
    List<CompanyWithPartnersProjection> findCompanyWithPartners(Long companyId);

    @Modifying
    @Query("""
            delete from
                Company c
            where
                c.companyId = :companyId
                and c.userId = :userId
            """)
    void deleteByCompanyIdAndUserId(@Param("companyId") Long companyId, @Param("userId") Long userId);

}
