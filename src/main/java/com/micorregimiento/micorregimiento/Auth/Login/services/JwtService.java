package com.micorregimiento.micorregimiento.Auth.Login.services;

import com.micorregimiento.micorregimiento.Generics.interfaces.IJwtService;
import com.micorregimiento.micorregimiento.Auth.Login.entitys.TokenValidationResponse;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JwtService implements IJwtService {

    @Value("${jwt.secret:mySecretKeyForJWTTokenGenerationThatShouldBeAtLeast256BitsLong}")
    private String secretKey;

    @Value("${jwt.access-token-expiration:3600}") // 1 hora por defecto
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration:604800}") // 7 días por defecto
    private long refreshTokenExpiration;

    // En un entorno de producción, esto debería ser Redis o una base de datos
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String generateAccessToken(Long userId, String email, List<String> roles,
                                      List<String> privileges, List<Long> neighborhoodIds) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + (accessTokenExpiration * 1000));

        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("email", email)
                .claim("roles", roles)
                .claim("privileges", privileges)
                .claim("neighborhoods", neighborhoodIds)
                .claim("type", "access")
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String generateRefreshToken(Long userId, String email) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + (refreshTokenExpiration * 1000));

        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("email", email)
                .claim("type", "refresh")
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public TokenValidationResponse validateToken(String token) {
        try {
            if (isTokenBlacklisted(token)) {
                return TokenValidationResponse.builder()
                        .valid(false)
                        .mensaje("Token ha sido revocado")
                        .build();
            }

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Long userId = claims.get("userId", Long.class);
            String email = claims.get("email", String.class);
            List<String> roles = claims.get("roles", List.class);
            List<String> privileges = claims.get("privileges", List.class);
            List<Long> neighborhoods = claims.get("neighborhoods", List.class);

            return TokenValidationResponse.builder()
                    .userId(userId)
                    .email(email)
                    .roles(roles != null ? roles : new ArrayList<>())
                    .privilegios(privileges != null ? privileges : new ArrayList<>())
                    .barrioIds(neighborhoods != null ? neighborhoods : new ArrayList<>())
                    .valid(true)
                    .mensaje("Token válido")
                    .build();

        } catch (ExpiredJwtException e) {
            return TokenValidationResponse.builder()
                    .valid(false)
                    .mensaje("Token expirado")
                    .build();
        } catch (JwtException e) {
            return TokenValidationResponse.builder()
                    .valid(false)
                    .mensaje("Token inválido")
                    .build();
        }
    }

    @Override
    public Long extractUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("userId", Long.class);
        } catch (JwtException e) {
            return null;
        }
    }

    @Override
    public String extractEmailFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (JwtException e) {
            return null;
        }
    }

    @Override
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }

    @Override
    public void blacklistToken(String token) {
        blacklistedTokens.add(token);
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
}
