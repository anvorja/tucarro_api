package com.anborja.tucarro.infrastructure.driven.jpa.sqlserver.repository;

import com.anborja.tucarro.infrastructure.driven.jpa.sqlserver.entity.CarEntity;
import com.anborja.tucarro.infrastructure.driven.jpa.sqlserver.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ICarRepository extends JpaRepository<CarEntity, Long> {

    /**
     * Busca un auto por su número de placa
     *
     * @param plateNumber el número de placa
     * @return Optional con el auto si existe
     */
    Optional<CarEntity> findByPlateNumber(String plateNumber);

    /**
     * Busca todos los autos de un usuario específico
     *
     * @param user la entidad del usuario
     * @return lista de autos del usuario
     */
    List<CarEntity> findByUser(UserEntity user);

    /**
     * Busca todos los autos de un usuario por ID
     *
     * @param userId el ID del usuario
     * @return lista de autos del usuario
     */
    @Query("SELECT c FROM CarEntity c WHERE c.user.id = :userId ORDER BY c.createdAt DESC")
    List<CarEntity> findByUserId(@Param("userId") Long userId);

    /**
     * Verifica si existe un auto con la placa dada
     *
     * @param plateNumber el número de placa
     * @return true si existe un auto con esa placa
     */
    boolean existsByPlateNumber(String plateNumber);

    /**
     * Verifica si existe un auto con la placa dada para un usuario diferente
     *
     * @param plateNumber el número de placa
     * @param userId el ID del usuario actual
     * @return true si otro usuario tiene un auto con esa placa
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CarEntity c WHERE c.plateNumber = :plateNumber AND c.user.id != :userId")
    boolean existsByPlateNumberAndUserIdNot(@Param("plateNumber") String plateNumber, @Param("userId") Long userId);

    /**
     * Elimina todos los autos de un usuario
     *
     * @param userId el ID del usuario
     * @return el número de autos eliminados
     */
    @Modifying
    @Query("DELETE FROM CarEntity c WHERE c.user.id = :userId")
    int deleteAllByUserId(@Param("userId") Long userId);

    /**
     * Cuenta el número de autos de un usuario
     *
     * @param userId el ID del usuario
     * @return el número de autos del usuario
     */
    @Query("SELECT COUNT(c) FROM CarEntity c WHERE c.user.id = :userId")
    int countByUserId(@Param("userId") Long userId);

    /**
     * Busca todos los autos de un usuario con paginación
     */
    @Query("SELECT c FROM CarEntity c WHERE c.user.id = :userId")
    Page<CarEntity> findByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * Busca autos de un usuario con término de búsqueda y paginación
     */
    @Query("SELECT c FROM CarEntity c WHERE c.user.id = :userId AND " +
            "(LOWER(c.brand) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.model) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.plateNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.color) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<CarEntity> findByUserIdWithSearch(@Param("userId") Long userId,
                                           @Param("searchTerm") String searchTerm,
                                           Pageable pageable);

    /**
     * Busca autos de un usuario con filtros específicos y paginación
     */
    @Query("SELECT c FROM CarEntity c WHERE c.user.id = :userId " +
            "AND (:brand IS NULL OR LOWER(c.brand) = LOWER(:brand)) " +
            "AND (:model IS NULL OR LOWER(c.model) = LOWER(:model)) " +
            "AND (:year IS NULL OR c.year = :year) " +
            "AND (:color IS NULL OR LOWER(c.color) = LOWER(:color)) " +
            "AND (:minYear IS NULL OR c.year >= :minYear) " +
            "AND (:maxYear IS NULL OR c.year <= :maxYear)")
    Page<CarEntity> findByUserIdWithFilters(@Param("userId") Long userId,
                                            @Param("brand") String brand,
                                            @Param("model") String model,
                                            @Param("year") Integer year,
                                            @Param("color") String color,
                                            @Param("minYear") Integer minYear,
                                            @Param("maxYear") Integer maxYear,
                                            Pageable pageable);

    /**
     * Busca autos por marca
     *
     * @param brand la marca a buscar
     * @return lista de autos de la marca especificada
     */
    List<CarEntity> findByBrandIgnoreCase(String brand);

    /**
     * Busca autos por modelo
     *
     * @param model el modelo a buscar
     * @return lista de autos del modelo especificado
     */
    List<CarEntity> findByModelIgnoreCase(String model);

    /**
     * Busca autos por año
     *
     * @param year el año a buscar
     * @return lista de autos del año especificado
     */
    List<CarEntity> findByYear(Integer year);

    /**
     * Busca autos por rango de años
     *
     * @param minYear el año mínimo (inclusive)
     * @param maxYear el año máximo (inclusive)
     * @return lista de autos en el rango de años especificado
     */
    List<CarEntity> findByYearBetween(Integer minYear, Integer maxYear);

    /**
     * Busca autos por color
     *
     * @param color el color a buscar
     * @return lista de autos del color especificado
     */
    List<CarEntity> findByColorIgnoreCase(String color);

    /**
     * Busca autos por marca y modelo para un usuario específico
     *
     * @param brand la marca
     * @param model el modelo
     * @param userId el ID del usuario
     * @return lista de autos que coinciden con los criterios
     */
    @Query("SELECT c FROM CarEntity c WHERE LOWER(c.brand) = LOWER(:brand) AND LOWER(c.model) = LOWER(:model) AND c.user.id = :userId")
    List<CarEntity> findByBrandAndModelAndUserId(@Param("brand") String brand,
                                                 @Param("model") String model,
                                                 @Param("userId") Long userId);

    /**
     * Busca autos por término de búsqueda en marca o modelo
     *
     * @param searchTerm el término de búsqueda
     * @param userId el ID del usuario
     * @return lista de autos que coinciden con el término de búsqueda
     */
    @Query("SELECT c FROM CarEntity c WHERE c.user.id = :userId AND " +
            "(LOWER(c.brand) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.model) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<CarEntity> findByBrandContainingOrModelContainingAndUserId(@Param("searchTerm") String searchTerm,
                                                                    @Param("userId") Long userId);

    /**
     * Obtiene autos vintage (más de 25 años) de un usuario
     *
     * @param userId el ID del usuario
     * @param vintageYear el año que define autos vintage
     * @return lista de autos vintage
     */
    @Query("SELECT c FROM CarEntity c WHERE c.user.id = :userId AND c.year < :vintageYear ORDER BY c.year ASC")
    List<CarEntity> findVintageCarsByUserId(@Param("userId") Long userId, @Param("vintageYear") Integer vintageYear);

    /**
     * Obtiene autos nuevos (menos de 3 años) de un usuario
     *
     * @param userId el ID del usuario
     * @param newCarYear el año que define autos nuevos
     * @return lista de autos nuevos
     */
    @Query("SELECT c FROM CarEntity c WHERE c.user.id = :userId AND c.year >= :newCarYear ORDER BY c.year DESC")
    List<CarEntity> findNewCarsByUserId(@Param("userId") Long userId, @Param("newCarYear") Integer newCarYear);

    /**
     * Obtiene autos de un usuario ordenados por año descendente
     *
     * @param userId el ID del usuario
     * @return lista de autos ordenados por año descendente
     */
    @Query("SELECT c FROM CarEntity c WHERE c.user.id = :userId ORDER BY c.year DESC NULLS LAST")
    List<CarEntity> findByUserIdOrderByYearDesc(@Param("userId") Long userId);

    /**
     * Obtiene autos de un usuario ordenados por año ascendente
     *
     * @param userId el ID del usuario
     * @return lista de autos ordenados por año ascendente
     */
    @Query("SELECT c FROM CarEntity c WHERE c.user.id = :userId ORDER BY c.year ASC NULLS LAST")
    List<CarEntity> findByUserIdOrderByYearAsc(@Param("userId") Long userId);

    /**
     * Busca autos por múltiples criterios de un usuario
     *
     * @param userId el ID del usuario
     * @param brand la marca (opcional)
     * @param model el modelo (opcional)
     * @param year el año (opcional)
     * @param color el color (opcional)
     * @return lista de autos que coinciden con los criterios
     */
    @Query("SELECT c FROM CarEntity c WHERE c.user.id = :userId " +
            "AND (:brand IS NULL OR LOWER(c.brand) LIKE LOWER(CONCAT('%', :brand, '%'))) " +
            "AND (:model IS NULL OR LOWER(c.model) LIKE LOWER(CONCAT('%', :model, '%'))) " +
            "AND (:year IS NULL OR c.year = :year) " +
            "AND (:color IS NULL OR LOWER(c.color) LIKE LOWER(CONCAT('%', :color, '%')))")
    List<CarEntity> findByMultipleCriteria(@Param("userId") Long userId,
                                           @Param("brand") String brand,
                                           @Param("model") String model,
                                           @Param("year") Integer year,
                                           @Param("color") String color);

    /**
     * Obtiene la marca más común de los autos de un usuario
     *
     * @param userId el ID del usuario
     * @return la marca más común
     */
    @Query("SELECT c.brand FROM CarEntity c WHERE c.user.id = :userId " +
            "GROUP BY c.brand ORDER BY COUNT(c.brand) DESC")
    List<String> findMostCommonBrandByUserId(@Param("userId") Long userId);

    /**
     * Obtiene estadísticas de años de autos de un usuario
     *
     * @param userId el ID del usuario
     * @return lista con [año_mínimo, año_máximo]
     */
    @Query("SELECT MIN(c.year), MAX(c.year) FROM CarEntity c WHERE c.user.id = :userId")
    List<Object[]> findYearStatsByUserId(@Param("userId") Long userId);

    /**
     * Busca autos con foto
     *
     * @param userId el ID del usuario
     * @return lista de autos que tienen foto
     */
    @Query("SELECT c FROM CarEntity c WHERE c.user.id = :userId AND c.photoUrl IS NOT NULL AND c.photoUrl != ''")
    List<CarEntity> findCarsWithPhotoByUserId(@Param("userId") Long userId);

    /**
     * Busca autos sin foto
     *
     * @param userId el ID del usuario
     * @return lista de autos que no tienen foto
     */
    @Query("SELECT c FROM CarEntity c WHERE c.user.id = :userId AND (c.photoUrl IS NULL OR c.photoUrl = '')")
    List<CarEntity> findCarsWithoutPhotoByUserId(@Param("userId") Long userId);
}