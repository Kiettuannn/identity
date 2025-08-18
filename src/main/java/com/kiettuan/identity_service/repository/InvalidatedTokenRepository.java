package com.kiettuan.identity_service.repository;

import java.util.Date;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kiettuan.identity_service.entity.InvalidatedToken;

@Repository
public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {
    @Modifying
    @Transactional
    @Query("DELETE FROM InvalidatedToken it WHERE it.expiryTime < :now")
    void deleteAllExpiredTokens(Date now);
}
