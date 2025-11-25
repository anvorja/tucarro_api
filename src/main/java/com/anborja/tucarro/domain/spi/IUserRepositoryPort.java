package com.anborja.tucarro.domain.spi;

import com.anborja.tucarro.domain.model.User;
import java.util.List;
import java.util.Optional;

public interface IUserRepositoryPort {

    /**
     * Guarda un usuario en la base de datos
     *
     * @param user el usuario a guardar
     * @return el usuario guardado con su ID asignado
     */
    User save(User user);

    /**
     * Busca un usuario por su ID
     *
     * @param id el ID del usuario
     * @return un Optional con el usuario si existe, Optional.empty() si no existe
     */
    Optional<User> findById(Long id);

    /**
     * Busca un usuario por su email
     *
     * @param email el email del usuario
     * @return un Optional con el usuario si existe, Optional.empty() si no existe
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica si existe un usuario con el email dado
     *
     * @param email el email a verificar
     * @return true si existe un usuario con ese email, false en caso contrario
     */
    boolean existsByEmail(String email);

    /**
     * Obtiene todos los usuarios del sistema
     *
     * @return lista de todos los usuarios
     */
    List<User> findAll();

    /**
     * Actualiza un usuario existente
     *
     * @param user el usuario con los datos actualizados
     * @return el usuario actualizado
     */
    User update(User user);

    /**
     * Elimina un usuario por su ID
     *
     * @param id el ID del usuario a eliminar
     * @return true si se eliminó exitosamente, false si no se encontró
     */
    boolean deleteById(Long id);

    /**
     * Verifica si existe un usuario con el ID especificado
     *
     * @param id el ID del usuario
     * @return true si el usuario existe, false en caso contrario
     */
    boolean existsById(Long id);

    /**
     * Cuenta el total de usuarios en el sistema
     *
     * @return el número total de usuarios
     */
    long count();

    /**
     * Busca usuarios por nombre o apellido (búsqueda parcial)
     *
     * @param searchTerm el término de búsqueda
     * @return lista de usuarios que coinciden con el término
     */
    List<User> findByNameContaining(String searchTerm);
}