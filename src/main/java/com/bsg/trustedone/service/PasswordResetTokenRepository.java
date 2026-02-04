package com.bsg.trustedone.service;

import com.bsg.trustedone.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    @Modifying
    @Query("""
            delete from
                PasswordResetToken
            where
                userId = :userId
            """)
    void deleteOldTokensForUser(Long userId);

    @Modifying
    @Query("""
            update
                PasswordResetToken
            set
                usedAt = :usedAt
            where
                token = :token
            """)
    void consumeToken(String token, Instant usedAt);
}
