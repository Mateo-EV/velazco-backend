package com.velazco.velazco_backend.services;

import com.velazco.velazco_backend.entities.RefreshToken;
import com.velazco.velazco_backend.entities.User;

public interface RefreshTokenService {

  RefreshToken createRefreshToken(User user, String deviceInfo);

  RefreshToken verifyExpiration(RefreshToken token);

  RefreshToken findByToken(String token);

  void revokeRefreshToken(String token);

  void revokeAllUserTokens(User user);

  void deleteExpiredTokens();
}
