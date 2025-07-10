package com.velazco.velazco_backend.repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.velazco.velazco_backend.entities.RefreshToken;
import com.velazco.velazco_backend.entities.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  Optional<RefreshToken> findByToken(String token);

  Optional<RefreshToken> findByTokenAndRevokedFalse(String token);

  @Modifying
  @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.user = :user")
  void revokeAllByUser(@Param("user") User user);

  @Modifying
  @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :now")
  void deleteExpiredTokens(@Param("now") LocalDateTime now);

  @Modifying
  @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.token = :token")
  void revokeByToken(@Param("token") String token);
}
