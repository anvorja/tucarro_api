package com.anborja.tucarro.domain.api.usecase;

import com.anborja.tucarro.domain.api.IAuthServicePort;
import com.anborja.tucarro.domain.exception.InvalidCredentialsException;
import com.anborja.tucarro.domain.exception.UserNotFoundException;
import com.anborja.tucarro.domain.model.User;
import com.anborja.tucarro.domain.spi.IJwtTokenPort;
import com.anborja.tucarro.domain.spi.IPasswordEncoderPort;
import com.anborja.tucarro.domain.spi.IUserRepositoryPort;
import com.anborja.tucarro.domain.util.DomainConstants;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

public class AuthUseCase implements IAuthServicePort {

    private final IUserRepositoryPort userRepositoryPort;
    private final IPasswordEncoderPort passwordEncoderPort;
    private final IJwtTokenPort jwtTokenPort;

    // Patrón para validación de email
    private static final Pattern EMAIL_PATTERN = Pattern.compile(DomainConstants.EMAIL_REGEX);
    private static final Pattern NAME_PATTERN = Pattern.compile(DomainConstants.NAME_REGEX);

    public AuthUseCase(IUserRepositoryPort userRepositoryPort,
                       IPasswordEncoderPort passwordEncoderPort,
                       IJwtTokenPort jwtTokenPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.jwtTokenPort = jwtTokenPort;
    }

    @Override
    public User register(User user) {
        // Validar datos del usuario
        validateUserForRegistration(user);

        // Verificar que el email no esté en uso
        if (userRepositoryPort.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException(DomainConstants.USER_ALREADY_EXISTS_MESSAGE);
        }

        // Codificar la contraseña
        String encodedPassword = passwordEncoderPort.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // Establecer timestamps
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        // Guardar y retornar el usuario
        return userRepositoryPort.save(user);
    }

    @Override
    public String login(String email, String password) {
        // Validar entrada
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidCredentialsException("El email es requerido");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new InvalidCredentialsException("La contraseña es requerida");
        }

        // Buscar usuario por email
        Optional<User> userOptional = userRepositoryPort.findByEmail(email.trim().toLowerCase());
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(email, true);
        }

        User user = userOptional.get();

        // Verificar contraseña
        if (!passwordEncoderPort.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException(email, "Contraseña incorrecta");
        }

        // Generar y retornar token JWT
        return jwtTokenPort.generateToken(user.getEmail(), user.getId());
    }

    @Override
    public boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        try {
            return jwtTokenPort.validateToken(token);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String extractEmailFromToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token no puede ser nulo o vacío");
        }

        if (!validateToken(token)) {
            throw new IllegalArgumentException("Token inválido");
        }

        return jwtTokenPort.extractEmail(token);
    }

    @Override
    public Long extractUserIdFromToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token no puede ser nulo o vacío");
        }

        if (!validateToken(token)) {
            throw new IllegalArgumentException("Token inválido");
        }

        return jwtTokenPort.extractUserId(token);
    }

    @Override
    public String generateToken(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Usuario no puede ser nulo");
        }

        if (user.getEmail() == null || user.getId() == null) {
            throw new IllegalArgumentException("Usuario debe tener email e ID");
        }

        return jwtTokenPort.generateToken(user.getEmail(), user.getId());
    }

    @Override
    public boolean invalidateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        try {
            return jwtTokenPort.invalidateToken(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Valida los datos del usuario para el registro
     *
     * @param user el usuario a validar
     * @throws IllegalArgumentException si algún dato es inválido
     */
    private void validateUserForRegistration(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Usuario no puede ser nulo");
        }

        // Validar nombre
        validateName(user.getFirstName(), "nombre", DomainConstants.USER_FIRST_NAME_REQUIRED,
                DomainConstants.USER_FIRST_NAME_LENGTH, DomainConstants.USER_FIRST_NAME_FORMAT);

        // Validar apellido
        validateName(user.getLastName(), "apellido", DomainConstants.USER_LAST_NAME_REQUIRED,
                DomainConstants.USER_LAST_NAME_LENGTH, DomainConstants.USER_LAST_NAME_FORMAT);

        // Validar email
        validateEmail(user.getEmail());

        // Validar contraseña
        validatePassword(user.getPassword());
    }

    /**
     * Valida un nombre (firstName o lastName)
     */
    private void validateName(String name, String fieldName, String requiredMessage,
                              String lengthMessage, String formatMessage) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(requiredMessage);
        }

        String trimmedName = name.trim();

        if (fieldName.equals("nombre")) {
            if (trimmedName.length() < DomainConstants.USER_FIRST_NAME_MIN_LENGTH ||
                    trimmedName.length() > DomainConstants.USER_FIRST_NAME_MAX_LENGTH) {
                throw new IllegalArgumentException(lengthMessage);
            }
        } else {
            if (trimmedName.length() < DomainConstants.USER_LAST_NAME_MIN_LENGTH ||
                    trimmedName.length() > DomainConstants.USER_LAST_NAME_MAX_LENGTH) {
                throw new IllegalArgumentException(lengthMessage);
            }
        }

        if (!NAME_PATTERN.matcher(trimmedName).matches()) {
            throw new IllegalArgumentException(formatMessage);
        }
    }

    /**
     * Valida el email del usuario
     */
    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException(DomainConstants.USER_EMAIL_REQUIRED);
        }

        String trimmedEmail = email.trim().toLowerCase();

        if (trimmedEmail.length() > DomainConstants.USER_EMAIL_MAX_LENGTH) {
            throw new IllegalArgumentException(DomainConstants.USER_EMAIL_LENGTH);
        }

        if (!EMAIL_PATTERN.matcher(trimmedEmail).matches()) {
            throw new IllegalArgumentException(DomainConstants.USER_EMAIL_FORMAT);
        }
    }

    /**
     * Valida la contraseña del usuario
     */
    private void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException(DomainConstants.USER_PASSWORD_REQUIRED);
        }

        if (password.length() < DomainConstants.USER_PASSWORD_MIN_LENGTH ||
                password.length() > DomainConstants.USER_PASSWORD_MAX_LENGTH) {
            throw new IllegalArgumentException(DomainConstants.USER_PASSWORD_LENGTH);
        }
    }
}