package com.bsg.trustedone.service;

import com.bsg.trustedone.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    @Modifying
    @Query("""
            delete from
                PasswordResetToken
            where
                userId = :userId
            """)
    void deleteOldTokensForUser(Long userId);

}
