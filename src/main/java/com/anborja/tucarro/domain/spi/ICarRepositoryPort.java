package com.anborja.tucarro.domain.spi;

import com.anborja.tucarro.domain.model.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ICarRepositoryPort {

    /**
     * Guarda un auto en la base de datos
     *
     * @param car el auto a guardar
     * @return el auto guardado con su ID asignado
     */
    Car save(Car car);

    /**
     * Busca un auto por su ID
     *
     * @param id el ID del auto
     * @return un Optional con el auto si existe, Optional.empty() si no existe
     */
    Optional<Car> findById(Long id);

    /**
     * Busca un auto por su número de placa
     *
     * @param plateNumber el número de placa del auto
     * @return un Optional con el auto si existe, Optional.empty() si no existe
     */
    Optional<Car> findByPlateNumber(String plateNumber);

    /**
     * Busca todos los autos de un usuario específico
     *
     * @param userId el ID del usuario
     * @return lista de autos del usuario
     */
    List<Car> findByUserId(Long userId);

    /**
     * Verifica si existe un auto con la placa dada
     *
     * @param plateNumber el número de placa a verificar
     * @return true si existe un auto con esa placa, false en caso contrario
     */
    boolean existsByPlateNumber(String plateNumber);

    /**
     * Verifica si existe un auto con la placa dada para un usuario diferente
     *
     * @param plateNumber el número de placa
     * @param userId el ID del usuario actual
     * @return true si otro usuario tiene un auto con esa placa
     */
    boolean existsByPlateNumberAndUserIdNot(String plateNumber, Long userId);

    /**
     * Actualiza un auto existente
     *
     * @param car el auto con los datos actualizados
     * @return el auto actualizado
     */
    Car update(Car car);

    /**
     * Elimina un auto por su ID
     *
     * @param id el ID del auto a eliminar
     * @return true si se eliminó exitosamente, false si no se encontró
     */
    boolean deleteById(Long id);

    /**
     * Elimina todos los autos de un usuario
     *
     * @param userId el ID del usuario
     * @return el número de autos eliminados
     */
    int deleteAllByUserId(Long userId);

    /**
     * Cuenta el número de autos de un usuario
     *
     * @param userId el ID del usuario
     * @return el número de autos del usuario
     */
    int countByUserId(Long userId);

    /**
     * Obtiene todos los autos del sistema
     *
     * @return lista de todos los autos
     */
    List<Car> findAll();

    /**
     * Busca autos por marca
     *
     * @param brand la marca a buscar
     * @return lista de autos de la marca especificada
     */
    List<Car> findByBrand(String brand);

    /**
     * Busca autos por modelo
     *
     * @param model el modelo a buscar
     * @return lista de autos del modelo especificado
     */
    List<Car> findByModel(String model);

    /**
     * Busca autos por año
     *
     * @param year el año a buscar
     * @return lista de autos del año especificado
     */
    List<Car> findByYear(Integer year);

    /**
     * Busca autos por rango de años
     *
     * @param minYear el año mínimo (inclusive)
     * @param maxYear el año máximo (inclusive)
     * @return lista de autos en el rango de años especificado
     */
    List<Car> findByYearBetween(Integer minYear, Integer maxYear);

    /**
     * Busca autos por color
     *
     * @param color el color a buscar
     * @return lista de autos del color especificado
     */
    List<Car> findByColor(String color);

    /**
     * Busca autos por marca y modelo (para un usuario específico)
     *
     * @param brand la marca
     * @param model el modelo
     * @param userId el ID del usuario
     * @return lista de autos que coinciden con los criterios
     */
    List<Car> findByBrandAndModelAndUserId(String brand, String model, Long userId);

    /**
     * Busca autos por término de búsqueda en marca o modelo
     *
     * @param searchTerm el término de búsqueda
     * @param userId el ID del usuario
     * @return lista de autos que coinciden con el término de búsqueda
     */
    List<Car> findByBrandContainingOrModelContainingAndUserId(String searchTerm, Long userId);

    /**
     * Cuenta el total de autos en el sistema
     *
     * @return el número total de autos
     */
    long count();

    /**
     * Busca todos los autos de un usuario con paginación
     *
     * @param userId el ID del usuario
     * @param pageable configuración de paginación y ordenamiento
     * @return página de autos del usuario
     */
    Page<Car> findByUserIdPaginated(Long userId, Pageable pageable);

    /**
     * Busca autos de un usuario con término de búsqueda y paginación
     *
     * @param userId el ID del usuario
     * @param searchTerm término de búsqueda
     * @param pageable configuración de paginación y ordenamiento
     * @return página de autos que coinciden con la búsqueda
     */
    Page<Car> searchCarsPaginated(Long userId, String searchTerm, Pageable pageable);

    /**
     * Busca autos de un usuario con filtros específicos y paginación
     *
     * @param userId el ID del usuario
     * @param brand filtro por marca (opcional)
     * @param model filtro por modelo (opcional)
     * @param year filtro por año (opcional)
     * @param color filtro por color (opcional)
     * @param minYear año mínimo (opcional)
     * @param maxYear año máximo (opcional)
     * @param pageable configuración de paginación y ordenamiento
     * @return página de autos que coinciden con los filtros
     */
    Page<Car> findByUserIdWithFiltersPaginated(Long userId, String brand, String model,
                                               Integer year, String color,
                                               Integer minYear, Integer maxYear,
                                               Pageable pageable);
}