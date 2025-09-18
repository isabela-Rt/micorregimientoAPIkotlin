package com.micorregimiento.micorregimiento.Generics.interfaces;

import com.micorregimiento.micorregimiento.Auth.Login.entitys.TokenValidationResponse;

import java.util.List;

public interface IJwtService {
    String generateAccessToken(Long userId, String email, List<String> roles, List<String> privileges, List<Long> neighborhoodIds);
    String generateRefreshToken(Long userId, String email);
    TokenValidationResponse validateToken(String token);
    Long extractUserIdFromToken(String token);
    String extractEmailFromToken(String token);
    boolean isTokenExpired(String token);
    void blacklistToken(String token);
    boolean isTokenBlacklisted(String token);
}
