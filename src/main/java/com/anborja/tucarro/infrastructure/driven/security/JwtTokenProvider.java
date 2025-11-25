package com.anborja.tucarro.infrastructure.driven.security;

import com.anborja.tucarro.domain.spi.IJwtTokenPort;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class JwtTokenProvider implements IJwtTokenPort {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationInMs;

    // Set para almacenar tokens invalidados (en producción usar Redis o base de datos)
    private final Set<String> blacklistedTokens = new HashSet<>();

    //    private SecretKey getSigningKey() {
//        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
//    }
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

//    @Override
//    public String generateToken(String email, Long userId) {
//        if (email == null || email.trim().isEmpty()) {
//            throw new IllegalArgumentException("El email no puede ser nulo o vacío");
//        }
//
//        if (userId == null) {
//            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
//        }
//
//        Date now = new Date();
//        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
//
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("userId", userId);
//        claims.put("email", email);
//
//        return Jwts.builder()
//                .setClaims(claims)
//                .setSubject(email)
//                .setIssuedAt(now)
//                .setExpiration(expiryDate)
//                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
//                .compact();
//    }

    @Override
    public String generateToken(String email, Long userId) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede ser nulo o vacío");
        }

        if (userId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .claim("userId", userId)
                .claim("email", email)
                .subject(email)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }


    //    @Override
//    public boolean validateToken(String token) {
//        if (token == null || token.trim().isEmpty()) {
//            return false;
//        }
//
//        // Verificar si el token está en la lista negra
//        if (blacklistedTokens.contains(token)) {
//            logger.debug("Token está en la lista negra");
//            return false;
//        }
//
//        try {
//            Jwts.parserBuilder()
//                    .setSigningKey(getSigningKey())
//                    .build()
//                    .parseClaimsJws(token);
//            return true;
//        } catch (SecurityException ex) {
//            logger.error("Firma JWT inválida: {}", ex.getMessage());
//        } catch (MalformedJwtException ex) {
//            logger.error("Token JWT malformado: {}", ex.getMessage());
//        } catch (ExpiredJwtException ex) {
//            logger.error("Token JWT expirado: {}", ex.getMessage());
//        } catch (UnsupportedJwtException ex) {
//            logger.error("Token JWT no soportado: {}", ex.getMessage());
//        } catch (IllegalArgumentException ex) {
//            logger.error("JWT claims string está vacío: {}", ex.getMessage());
//        }
//
//        return false;
//    }
    @Override
    public boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        if (blacklistedTokens.contains(token)) {
            logger.debug("Token está en la lista negra");
            return false;
        }

        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException ex) {
            logger.error("Firma JWT inválida: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            logger.error("Token JWT malformado: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("Token JWT expirado: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("Token JWT no soportado: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string está vacío: {}", ex.getMessage());
        }

        return false;
    }

    @Override
    public String extractEmail(String token) {
        if (!validateToken(token)) {
            throw new IllegalArgumentException("Token inválido");
        }

        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    @Override
    public Long extractUserId(String token) {
        if (!validateToken(token)) {
            throw new IllegalArgumentException("Token inválido");
        }

        Claims claims = extractAllClaims(token);
        Object userIdClaim = claims.get("userId");

        if (userIdClaim instanceof Integer) {
            return ((Integer) userIdClaim).longValue();
        } else if (userIdClaim instanceof Long) {
            return (Long) userIdClaim;
        } else {
            throw new IllegalArgumentException("UserId no encontrado en el token");
        }
    }

    @Override
    public Date extractExpiration(String token) {
        if (!validateToken(token)) {
            throw new IllegalArgumentException("Token inválido");
        }

        Claims claims = extractAllClaims(token);
        return claims.getExpiration();
    }

    @Override
    public boolean isTokenExpired(String token) {
        if (token == null || token.trim().isEmpty()) {
            return true;
        }

        try {
            Date expiration = extractExpiration(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public boolean invalidateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        try {
            // Verificar que el token sea válido antes de invalidarlo
            if (validateToken(token)) {
                blacklistedTokens.add(token);
                logger.debug("Token invalidado exitosamente");
                return true;
            }
        } catch (Exception e) {
            logger.error("Error al invalidar token: {}", e.getMessage());
        }

        return false;
    }

    @Override
    public long getExpirationTime() {
        return jwtExpirationInMs;
    }

    @Override
    public String refreshToken(String token) {
        if (!validateToken(token)) {
            throw new IllegalArgumentException("Token inválido para refrescar");
        }

        try {
            String email = extractEmail(token);
            Long userId = extractUserId(token);

            // Invalidar el token actual
            invalidateToken(token);

            // Generar nuevo token
            return generateToken(email, userId);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al refrescar token: " + e.getMessage());
        }
    }

    /**
     * Extrae todos los claims del token
     */
//    private Claims extractAllClaims(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(getSigningKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extrae la fecha de emisión del token
     */
    public Date extractIssuedAt(String token) {
        if (!validateToken(token)) {
            throw new IllegalArgumentException("Token inválido");
        }

        Claims claims = extractAllClaims(token);
        return claims.getIssuedAt();
    }

    /**
     * Verifica si el token fue emitido antes de una fecha específica
     */
    public boolean wasTokenIssuedBefore(String token, Date date) {
        try {
            Date issuedAt = extractIssuedAt(token);
            return issuedAt.before(date);
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Obtiene información del token en formato legible
     */
    public TokenInfo getTokenInfo(String token) {
        if (!validateToken(token)) {
            throw new IllegalArgumentException("Token inválido");
        }

        Claims claims = extractAllClaims(token);

        return new TokenInfo(
                claims.getSubject(),
                extractUserId(token),
                claims.getIssuedAt(),
                claims.getExpiration(),
                isTokenExpired(token)
        );
    }

    /**
     * Limpia tokens expirados de la lista negra (llamar periódicamente)
     */
    public void cleanupExpiredTokens() {
        blacklistedTokens.removeIf(token -> {
            try {
                return isTokenExpired(token);
            } catch (Exception e) {
                // Si no se puede parsear, lo eliminamos de la lista
                return true;
            }
        });

        logger.debug("Limpieza de tokens expirados completada. Tokens en lista negra: {}",
                blacklistedTokens.size());
    }

    /**
     * Clase para información del token
     */
    public static class TokenInfo {
        private final String email;
        private final Long userId;
        private final Date issuedAt;
        private final Date expiration;
        private final boolean expired;

        public TokenInfo(String email, Long userId, Date issuedAt, Date expiration, boolean expired) {
            this.email = email;
            this.userId = userId;
            this.issuedAt = issuedAt;
            this.expiration = expiration;
            this.expired = expired;
        }

        // Getters
        public String getEmail() { return email; }
        public Long getUserId() { return userId; }
        public Date getIssuedAt() { return issuedAt; }
        public Date getExpiration() { return expiration; }
        public boolean isExpired() { return expired; }

        @Override
        public String toString() {
            return "TokenInfo{" +
                    "email='" + email + '\'' +
                    ", userId=" + userId +
                    ", issuedAt=" + issuedAt +
                    ", expiration=" + expiration +
                    ", expired=" + expired +
                    '}';
        }
    }
}