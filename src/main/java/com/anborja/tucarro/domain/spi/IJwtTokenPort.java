package com.anborja.tucarro.domain.spi;

import java.util.Date;

public interface IJwtTokenPort {

    /**
     * Genera un token JWT para un usuario
     *
     * @param email el email del usuario
     * @param userId el ID del usuario
     * @return el token JWT generado
     */
    String generateToken(String email, Long userId);

    /**
     * Valida si un token JWT es válido
     *
     * @param token el token a validar
     * @return true si el token es válido, false en caso contrario
     */
    boolean validateToken(String token);

    /**
     * Extrae el email del usuario desde un token JWT
     *
     * @param token el token JWT
     * @return el email del usuario
     */
    String extractEmail(String token);

    /**
     * Extrae el ID del usuario desde un token JWT
     *
     * @param token el token JWT
     * @return el ID del usuario
     */
    Long extractUserId(String token);

    /**
     * Extrae la fecha de expiración de un token JWT
     *
     * @param token el token JWT
     * @return la fecha de expiración
     */
    Date extractExpiration(String token);

    /**
     * Verifica si un token JWT ha expirado
     *
     * @param token el token JWT
     * @return true si el token ha expirado, false en caso contrario
     */
    boolean isTokenExpired(String token);

    /**
     * Invalida un token JWT (para logout)
     *
     * @param token el token a invalidar
     * @return true si se invalidó exitosamente
     */
    boolean invalidateToken(String token);

    /**
     * Obtiene el tiempo de expiración configurado para los tokens
     *
     * @return el tiempo de expiración en milisegundos
     */
    long getExpirationTime();

    /**
     * Refresca un token JWT (genera uno nuevo con nueva fecha de expiración)
     *
     * @param token el token a refrescar
     * @return el nuevo token JWT
     */
    String refreshToken(String token);
}