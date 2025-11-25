package com.anborja.tucarro.domain.api;

import com.anborja.tucarro.domain.model.User;
import com.anborja.tucarro.domain.exception.UserNotFoundException;
import com.anborja.tucarro.domain.exception.InvalidCredentialsException;
import java.util.List;

public interface IUserServicePort {

    /**
     * Obtiene el perfil de un usuario por su ID
     *
     * @param userId el ID del usuario
     * @return el usuario encontrado
     * @throws UserNotFoundException si el usuario no existe
     */
    User getUserProfile(Long userId);

    /**
     * Obtiene un usuario por su email
     *
     * @param email el email del usuario
     * @return el usuario encontrado
     * @throws UserNotFoundException si el usuario no existe
     */
    User getUserByEmail(String email);

    /**
     * Actualiza el perfil de un usuario
     *
     * @param userId el ID del usuario a actualizar
     * @param updatedUser los datos actualizados del usuario
     * @return el usuario actualizado
     * @throws UserNotFoundException si el usuario no existe
     * @throws IllegalArgumentException si los datos son inválidos
     */
    User updateUserProfile(Long userId, User updatedUser);

    /**
     * Cambia la contraseña de un usuario
     *
     * @param userId el ID del usuario
     * @param currentPassword la contraseña actual
     * @param newPassword la nueva contraseña
     * @return true si se cambió exitosamente
     * @throws UserNotFoundException si el usuario no existe
     * @throws InvalidCredentialsException si la contraseña actual es incorrecta
     * @throws IllegalArgumentException si la nueva contraseña no es válida
     */
    boolean changePassword(Long userId, String currentPassword, String newPassword);

    /**
     * Elimina un usuario y todos sus autos asociados
     *
     * @param userId el ID del usuario a eliminar
     * @return true si se eliminó exitosamente
     * @throws UserNotFoundException si el usuario no existe
     */
    boolean deleteUser(Long userId);

    /**
     * Obtiene todos los usuarios del sistema (solo para administradores)
     *
     * @return lista de todos los usuarios
     */
    List<User> getAllUsers();

    /**
     * Busca usuarios por nombre o apellido
     *
     * @param searchTerm el término de búsqueda
     * @return lista de usuarios que coinciden con el término
     */
    List<User> searchUsers(String searchTerm);

    /**
     * Verifica si un usuario existe por su ID
     *
     * @param userId el ID del usuario
     * @return true si el usuario existe, false en caso contrario
     */
    boolean userExists(Long userId);

    /**
     * Verifica si un email está disponible para registro
     *
     * @param email el email a verificar
     * @return true si el email está disponible, false si ya está en uso
     */
    boolean isEmailAvailable(String email);

    /**
     * Obtiene estadísticas básicas del usuario
     *
     * @param userId el ID del usuario
     * @return objeto con estadísticas del usuario (número de autos, fecha de registro, etc.)
     */
    UserStats getUserStats(Long userId);

    /**
     * Clase interna para estadísticas del usuario
     */
    class UserStats {
        private final Long userId;
        private final int totalCars;
        private final java.time.LocalDateTime registrationDate;
        private final java.time.LocalDateTime lastUpdate;

        public UserStats(Long userId, int totalCars,
                         java.time.LocalDateTime registrationDate,
                         java.time.LocalDateTime lastUpdate) {
            this.userId = userId;
            this.totalCars = totalCars;
            this.registrationDate = registrationDate;
            this.lastUpdate = lastUpdate;
        }

        // Getters
        public Long getUserId() { return userId; }
        public int getTotalCars() { return totalCars; }
        public java.time.LocalDateTime getRegistrationDate() { return registrationDate; }
        public java.time.LocalDateTime getLastUpdate() { return lastUpdate; }
    }
}