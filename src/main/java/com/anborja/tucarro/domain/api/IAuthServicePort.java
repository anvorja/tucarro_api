package com.anborja.tucarro.domain.api;

import com.anborja.tucarro.domain.model.User;
import com.anborja.tucarro.domain.exception.InvalidCredentialsException;
import com.anborja.tucarro.domain.exception.UserNotFoundException;

public interface IAuthServicePort {

    /**
     * Registra un nuevo usuario en el sistema
     *
     * @param user el usuario a registrar
     * @return el usuario registrado con su ID asignado
     * @throws IllegalArgumentException si los datos del usuario no son válidos
     * @throws RuntimeException si el usuario ya existe
     */
    User register(User user);

    /**
     * Autentica un usuario con email y contraseña
     *
     * @param email el email del usuario
     * @param password la contraseña del usuario
     * @return el token JWT si la autenticación es exitosa
     * @throws InvalidCredentialsException si las credenciales son incorrectas
     * @throws UserNotFoundException si el usuario no existe
     */
    String login(String email, String password);

    /**
     * Valida un token JWT
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
     * @throws IllegalArgumentException si el token es inválido
     */
    String extractEmailFromToken(String token);

    /**
     * Extrae el ID del usuario desde un token JWT
     *
     * @param token el token JWT
     * @return el ID del usuario
     * @throws IllegalArgumentException si el token es inválido
     */
    Long extractUserIdFromToken(String token);

    /**
     * Genera un nuevo token JWT para un usuario
     *
     * @param user el usuario para el cual generar el token
     * @return el token JWT generado
     */
    String generateToken(User user);

    /**
     * Invalida un token JWT (logout)
     *
     * @param token el token a invalidar
     * @return true si el token fue invalidado exitosamente
     */
    boolean invalidateToken(String token);
}