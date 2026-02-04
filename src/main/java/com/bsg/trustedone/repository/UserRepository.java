package com.bsg.trustedone.repository;

import com.bsg.trustedone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Modifying
    @Query("""
            update
                User
            set
                password = :newPassword
            where
                userId = :userId
            """)
    void updatePassword(String newPassword, Long userId);
}
