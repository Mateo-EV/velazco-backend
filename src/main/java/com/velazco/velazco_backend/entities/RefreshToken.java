package com.velazco.velazco_backend.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "refresh_tokens")
@Data
@NoArgsConstructor
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "token", nullable = false, unique = true, length = 500)
  private String token;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "expires_at", nullable = false)
  private LocalDateTime expiresAt;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "revoked", nullable = false)
  private Boolean revoked = false;

  @Column(name = "device_info", length = 255)
  private String deviceInfo;

  public RefreshToken(String token, User user, LocalDateTime expiresAt, String deviceInfo) {
    this.token = token;
    this.user = user;
    this.expiresAt = expiresAt;
    this.createdAt = LocalDateTime.now();
    this.revoked = false;
    this.deviceInfo = deviceInfo;
  }

  public boolean isExpired() {
    return LocalDateTime.now().isAfter(expiresAt);
  }

  public boolean isValid() {
    return !revoked && !isExpired();
  }
}
