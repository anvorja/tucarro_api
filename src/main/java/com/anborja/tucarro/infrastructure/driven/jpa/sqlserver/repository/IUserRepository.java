package com.anborja.tucarro.infrastructure.driven.jpa.sqlserver.repository;

import com.anborja.tucarro.infrastructure.driven.jpa.sqlserver.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Busca un usuario por su email
     *
     * @param email el email del usuario
     * @return Optional con el usuario si existe
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Verifica si existe un usuario con el email dado
     *
     * @param email el email a verificar
     * @return true si existe un usuario con ese email
     */
    boolean existsByEmail(String email);

    // JPQL (agnóstico de BD):

    /**
     * Busca usuarios por nombre o apellido que contengan el término de búsqueda
     *
     * @param searchTerm el término de búsqueda
     * @return lista de usuarios que coinciden
     */
    @Query("SELECT u FROM UserEntity u WHERE " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<UserEntity> findByNameContaining(@Param("searchTerm") String searchTerm);

    /**
     * Busca usuarios por nombre completo (nombre + apellido)
     *
     * @param searchTerm el término de búsqueda
     * @return lista de usuarios que coinciden
     */
    @Query("SELECT u FROM UserEntity u WHERE " +
            "LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<UserEntity> findByFullNameContaining(@Param("searchTerm") String searchTerm);

    /**
     * Busca usuarios creados después de una fecha específica
     *
     * @param date la fecha de referencia
     * @return lista de usuarios creados después de la fecha
     */
    @Query("SELECT u FROM UserEntity u WHERE u.createdAt >= :date ORDER BY u.createdAt DESC")
    List<UserEntity> findUsersCreatedAfter(@Param("date") java.time.LocalDateTime date);

    /**
     * Obtiene usuarios con sus autos cargados (evita N+1 queries)
     *
     * @return lista de usuarios con autos
     */
    @Query("SELECT DISTINCT u FROM UserEntity u LEFT JOIN FETCH u.cars")
    List<UserEntity> findAllWithCars();

    /**
     * Busca un usuario por ID con sus autos cargados
     *
     * @param id el ID del usuario
     * @return Optional con el usuario y sus autos si existe
     */
    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.cars WHERE u.id = :id")
    Optional<UserEntity> findByIdWithCars(@Param("id") Long id);

    /**
     * Obtiene usuarios que tienen al menos un auto
     *
     * @return lista de usuarios con autos
     */
    @Query("SELECT DISTINCT u FROM UserEntity u WHERE EXISTS (SELECT 1 FROM CarEntity c WHERE c.user = u)")
    List<UserEntity> findUsersWithCars();

    /**
     * Obtiene usuarios que no tienen autos
     *
     * @return lista de usuarios sin autos
     */
    @Query("SELECT u FROM UserEntity u WHERE NOT EXISTS (SELECT 1 FROM CarEntity c WHERE c.user = u)")
    List<UserEntity> findUsersWithoutCars();

    /**
     * Cuenta usuarios creados en un período específico
     *
     * @param startDate fecha de inicio
     * @param endDate fecha de fin
     * @return número de usuarios creados en el período
     */
    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    long countUsersCreatedBetween(@Param("startDate") java.time.LocalDateTime startDate,
                                  @Param("endDate") java.time.LocalDateTime endDate);

    /**
     * Busca usuarios por email que contenga el término (para búsquedas parciales)
     *
     * @param emailPart parte del email a buscar
     * @return lista de usuarios cuyo email contiene el término
     */
    @Query("SELECT u FROM UserEntity u WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :emailPart, '%'))")
    List<UserEntity> findByEmailContaining(@Param("emailPart") String emailPart);
}