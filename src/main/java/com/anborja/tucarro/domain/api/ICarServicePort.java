package com.anborja.tucarro.domain.api;

import com.anborja.tucarro.domain.model.Car;
import com.anborja.tucarro.domain.exception.UserNotFoundException;
import com.anborja.tucarro.domain.exception.CarNotFoundException;
import java.util.List;

public interface ICarServicePort {

    /**
     * Crea un nuevo auto para un usuario
     *
     * @param car el auto a crear
     * @param userId el ID del usuario propietario
     * @return el auto creado con su ID asignado
     * @throws IllegalArgumentException si los datos del auto no son válidos
     * @throws RuntimeException si ya existe un auto con esa placa
     * @throws UserNotFoundException si el usuario no existe
     */
    Car createCar(Car car, Long userId);

    /**
     * Obtiene todos los autos de un usuario específico
     *
     * @param userId el ID del usuario
     * @return lista de autos del usuario
     * @throws UserNotFoundException si el usuario no existe
     */
    List<Car> getCarsByUserId(Long userId);

    /**
     * Obtiene un auto específico por su ID
     *
     * @param carId el ID del auto
     * @param userId el ID del usuario (para verificar propiedad)
     * @return el auto encontrado
     * @throws CarNotFoundException si el auto no existe
     * @throws IllegalArgumentException si el auto no pertenece al usuario
     */
    Car getCarById(Long carId, Long userId);

    /**
     * Obtiene un auto por su número de placa
     *
     * @param plateNumber el número de placa
     * @param userId el ID del usuario (para verificar propiedad)
     * @return el auto encontrado
     * @throws CarNotFoundException si el auto no existe
     * @throws IllegalArgumentException si el auto no pertenece al usuario
     */
    Car getCarByPlateNumber(String plateNumber, Long userId);

    /**
     * Actualiza los datos de un auto existente
     *
     * @param carId el ID del auto a actualizar
     * @param updatedCar los datos actualizados del auto
     * @param userId el ID del usuario (para verificar propiedad)
     * @return el auto actualizado
     * @throws CarNotFoundException si el auto no existe
     * @throws IllegalArgumentException si los datos no son válidos o el auto no pertenece al usuario
     */
    Car updateCar(Long carId, Car updatedCar, Long userId);

    /**
     * Elimina un auto
     *
     * @param carId el ID del auto a eliminar
     * @param userId el ID del usuario (para verificar propiedad)
     * @return true si se eliminó exitosamente
     * @throws CarNotFoundException si el auto no existe
     * @throws IllegalArgumentException si el auto no pertenece al usuario
     */
    boolean deleteCar(Long carId, Long userId);

    /**
     * Verifica si un auto existe por su ID
     *
     * @param carId el ID del auto
     * @return true si el auto existe, false en caso contrario
     */
    boolean carExists(Long carId);

    /**
     * Verifica si una placa está disponible para un nuevo auto
     *
     * @param plateNumber el número de placa a verificar
     * @return true si la placa está disponible, false si ya está en uso
     */
    boolean isPlateNumberAvailable(String plateNumber);

    /**
     * Verifica si una placa está disponible para actualizar un auto existente
     *
     * @param plateNumber el número de placa
     * @param carId el ID del auto actual (para excluirlo de la verificación)
     * @return true si la placa está disponible, false si ya está en uso por otro auto
     */
    boolean isPlateNumberAvailableForUpdate(String plateNumber, Long carId);

    /**
     * Obtiene estadísticas de los autos de un usuario
     *
     * @param userId el ID del usuario
     * @return objeto con estadísticas de los autos del usuario
     */
    CarStats getCarStatsByUserId(Long userId);

    /**
     * Obtiene todos los autos del sistema (solo para administradores)
     *
     * @return lista de todos los autos
     */
    List<Car> getAllCars();

    /**
     * Valida los datos de un auto sin guardarlo
     *
     * @param car el auto a validar
     * @throws IllegalArgumentException si algún dato es inválido
     */
    void validateCar(Car car);

    /**
     * Clase interna para estadísticas de autos
     */
    class CarStats {
        private final Long userId;
        private final int totalCars;
        private final int vintageCarCount;
        private final int newCarCount;
        private final String mostCommonBrand;
        private final Integer newestCarYear;
        private final Integer oldestCarYear;

        public CarStats(Long userId, int totalCars, int vintageCarCount, int newCarCount,
                        String mostCommonBrand, Integer newestCarYear, Integer oldestCarYear) {
            this.userId = userId;
            this.totalCars = totalCars;
            this.vintageCarCount = vintageCarCount;
            this.newCarCount = newCarCount;
            this.mostCommonBrand = mostCommonBrand;
            this.newestCarYear = newestCarYear;
            this.oldestCarYear = oldestCarYear;
        }

        // Getters
        public Long getUserId() { return userId; }
        public int getTotalCars() { return totalCars; }
        public int getVintageCarCount() { return vintageCarCount; }
        public int getNewCarCount() { return newCarCount; }
        public String getMostCommonBrand() { return mostCommonBrand; }
        public Integer getNewestCarYear() { return newestCarYear; }
        public Integer getOldestCarYear() { return oldestCarYear; }
    }
}