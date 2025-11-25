package com.anborja.tucarro.domain.api.usecase;

import com.anborja.tucarro.domain.api.IUserServicePort;
import com.anborja.tucarro.domain.exception.InvalidCredentialsException;
import com.anborja.tucarro.domain.exception.UserNotFoundException;
import com.anborja.tucarro.domain.model.User;
import com.anborja.tucarro.domain.spi.IPasswordEncoderPort;
import com.anborja.tucarro.domain.spi.IUserRepositoryPort;
import com.anborja.tucarro.domain.spi.ICarRepositoryPort;
import com.anborja.tucarro.domain.util.DomainConstants;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

public class UserUseCase implements IUserServicePort {

    private final IUserRepositoryPort userRepositoryPort;
    private final IPasswordEncoderPort passwordEncoderPort;
    private final ICarRepositoryPort carRepositoryPort;

    // Patrones para validación
    private static final Pattern EMAIL_PATTERN = Pattern.compile(DomainConstants.EMAIL_REGEX);
    private static final Pattern NAME_PATTERN = Pattern.compile(DomainConstants.NAME_REGEX);

    public UserUseCase(IUserRepositoryPort userRepositoryPort,
                       IPasswordEncoderPort passwordEncoderPort,
                       ICarRepositoryPort carRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.carRepositoryPort = carRepositoryPort;
    }

    @Override
    public User getUserProfile(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }

        return userRepositoryPort.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Override
    public User getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede ser nulo o vacío");
        }

        return userRepositoryPort.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new UserNotFoundException(email, true));
    }

    @Override
    public User updateUserProfile(Long userId, User updatedUser) {
        if (userId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }

        if (updatedUser == null) {
            throw new IllegalArgumentException("Los datos del usuario no pueden ser nulos");
        }

        // Verificar que el usuario existe
        User existingUser = getUserProfile(userId);

        // Validar los datos actualizados
        validateUserForUpdate(updatedUser, existingUser);

        // Actualizar solo los campos permitidos
        existingUser.setFirstName(updatedUser.getFirstName().trim());
        existingUser.setLastName(updatedUser.getLastName().trim());

        // Solo actualizar email si es diferente y está disponible
        String newEmail = updatedUser.getEmail().trim().toLowerCase();
        if (!existingUser.getEmail().equals(newEmail)) {
            if (userRepositoryPort.existsByEmail(newEmail)) {
                throw new IllegalArgumentException("El email ya está en uso por otro usuario");
            }
            existingUser.setEmail(newEmail);
        }

        // Actualizar timestamp
        existingUser.setUpdatedAt(LocalDateTime.now());

        return userRepositoryPort.update(existingUser);
    }

    @Override
    public boolean changePassword(Long userId, String currentPassword, String newPassword) {
        if (userId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }

        if (currentPassword == null || currentPassword.isEmpty()) {
            throw new IllegalArgumentException("La contraseña actual es requerida");
        }

        if (newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("La nueva contraseña es requerida");
        }

        // Obtener usuario
        User user = getUserProfile(userId);

        // Verificar contraseña actual
        if (!passwordEncoderPort.matches(currentPassword, user.getPassword())) {
            throw new InvalidCredentialsException("La contraseña actual es incorrecta");
        }

        // Validar nueva contraseña
        validatePassword(newPassword);

        // Verificar que la nueva contraseña sea diferente
        if (passwordEncoderPort.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("La nueva contraseña debe ser diferente a la actual");
        }

        // Codificar y actualizar contraseña
        user.setPassword(passwordEncoderPort.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());

        userRepositoryPort.update(user);
        return true;
    }

    @Override
    public boolean deleteUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }

        // Verificar que el usuario existe
        if (!userRepositoryPort.findById(userId).isPresent()) {
            throw new UserNotFoundException(userId);
        }

        // Eliminar todos los autos del usuario primero
        carRepositoryPort.deleteAllByUserId(userId);

        // Eliminar el usuario
        return userRepositoryPort.deleteById(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepositoryPort.findAll();
    }

    @Override
    public List<User> searchUsers(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw new IllegalArgumentException("El término de búsqueda no puede ser vacío");
        }

        String cleanSearchTerm = searchTerm.trim();
        return userRepositoryPort.findByNameContaining(cleanSearchTerm);
    }

    @Override
    public boolean userExists(Long userId) {
        if (userId == null) {
            return false;
        }

        return userRepositoryPort.findById(userId).isPresent();
    }

    @Override
    public boolean isEmailAvailable(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        return !userRepositoryPort.existsByEmail(email.trim().toLowerCase());
    }

    @Override
    public UserStats getUserStats(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }

        User user = getUserProfile(userId);
        int totalCars = carRepositoryPort.countByUserId(userId);

        return new UserStats(
                user.getId(),
                totalCars,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    /**
     * Valida los datos del usuario para actualización
     */
    private void validateUserForUpdate(User updatedUser, User existingUser) {
        // Validar nombre
        if (updatedUser.getFirstName() != null) {
            validateName(updatedUser.getFirstName(), "nombre",
                    DomainConstants.USER_FIRST_NAME_REQUIRED,
                    DomainConstants.USER_FIRST_NAME_LENGTH,
                    DomainConstants.USER_FIRST_NAME_FORMAT);
        }

        // Validar apellido
        if (updatedUser.getLastName() != null) {
            validateName(updatedUser.getLastName(), "apellido",
                    DomainConstants.USER_LAST_NAME_REQUIRED,
                    DomainConstants.USER_LAST_NAME_LENGTH,
                    DomainConstants.USER_LAST_NAME_FORMAT);
        }

        // Validar email
        if (updatedUser.getEmail() != null) {
            validateEmail(updatedUser.getEmail());
        }
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