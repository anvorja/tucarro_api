package com.anborja.tucarro.domain.api.usecase;

import com.anborja.tucarro.domain.api.ICarServicePort;
import com.anborja.tucarro.domain.exception.CarAlreadyExistsException;
import com.anborja.tucarro.domain.exception.CarNotFoundException;
import com.anborja.tucarro.domain.exception.UserNotFoundException;
import com.anborja.tucarro.domain.model.Car;
import com.anborja.tucarro.domain.spi.ICarRepositoryPort;
import com.anborja.tucarro.domain.spi.IUserRepositoryPort;
import com.anborja.tucarro.domain.util.DomainConstants;
import com.anborja.tucarro.shared.validation.PlateValidator;
import com.anborja.tucarro.shared.validation.YearValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class CarUseCase implements ICarServicePort {

    private final ICarRepositoryPort carRepositoryPort;
    private final IUserRepositoryPort userRepositoryPort;

    public CarUseCase(ICarRepositoryPort carRepositoryPort, IUserRepositoryPort userRepositoryPort) {
        this.carRepositoryPort = carRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public Car createCar(Car car, Long userId) {
        if (car == null) {
            throw new IllegalArgumentException("El auto no puede ser nulo");
        }

        if (userId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }

        // Verificar que el usuario existe
        if (!userRepositoryPort.findById(userId).isPresent()) {
            throw new UserNotFoundException(userId);
        }

        // Validar datos del auto
        validateCar(car);

        // Normalizar y validar placa
        String normalizedPlate = PlateValidator.validateAndNormalize(car.getPlateNumber());

        // Verificar que la placa no esté en uso
        if (carRepositoryPort.existsByPlateNumber(normalizedPlate)) {
            throw CarAlreadyExistsException.withPlateNumber(normalizedPlate);
        }

        // Preparar auto para guardar
        car.setUserId(userId);
        car.setPlateNumber(normalizedPlate);
        car.setBrand(car.getBrand().trim());
        car.setModel(car.getModel().trim());
        car.setColor(car.getColor().trim());

        LocalDateTime now = LocalDateTime.now();
        car.setCreatedAt(now);
        car.setUpdatedAt(now);

        return carRepositoryPort.save(car);
    }

    @Override
    public List<Car> getCarsByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }

        // Verificar que el usuario existe
        if (!userRepositoryPort.findById(userId).isPresent()) {
            throw new UserNotFoundException(userId);
        }

        return carRepositoryPort.findByUserId(userId);
    }

    @Override
    public Car getCarById(Long carId, Long userId) {
        if (carId == null) {
            throw new IllegalArgumentException("El ID del auto no puede ser nulo");
        }

        if (userId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }

        Optional<Car> carOptional = carRepositoryPort.findById(carId);
        if (carOptional.isEmpty()) {
            throw new CarNotFoundException(carId);
        }

        Car car = carOptional.get();

        // Verificar que el auto pertenece al usuario
        if (!car.getUserId().equals(userId)) {
            throw new IllegalArgumentException(DomainConstants.UNAUTHORIZED_ACCESS_MESSAGE);
        }

        return car;
    }

    @Override
    public Car getCarByPlateNumber(String plateNumber, Long userId) {
        if (plateNumber == null || plateNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de placa no puede ser nulo o vacío");
        }

        if (userId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }

        String normalizedPlate = PlateValidator.normalizePlate(plateNumber);
        Optional<Car> carOptional = carRepositoryPort.findByPlateNumber(normalizedPlate);

        if (carOptional.isEmpty()) {
            throw new CarNotFoundException(normalizedPlate, true);
        }

        Car car = carOptional.get();

        // Verificar que el auto pertenece al usuario
        if (!car.getUserId().equals(userId)) {
            throw new IllegalArgumentException(DomainConstants.UNAUTHORIZED_ACCESS_MESSAGE);
        }

        return car;
    }

    @Override
    public Car updateCar(Long carId, Car updatedCar, Long userId) {
        if (carId == null) {
            throw new IllegalArgumentException("El ID del auto no puede ser nulo");
        }

        if (updatedCar == null) {
            throw new IllegalArgumentException("Los datos del auto no pueden ser nulos");
        }

        if (userId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }

        // Obtener auto existente (esto ya verifica propiedad)
        Car existingCar = getCarById(carId, userId);

        // Validar datos actualizados
        validateCarForUpdate(updatedCar);

        // Actualizar solo los campos que NO son nulos
        if (updatedCar.getBrand() != null && !updatedCar.getBrand().trim().isEmpty()) {
            existingCar.setBrand(updatedCar.getBrand().trim());
        }

        if (updatedCar.getModel() != null && !updatedCar.getModel().trim().isEmpty()) {
            existingCar.setModel(updatedCar.getModel().trim());
        }

        if (updatedCar.getYear() != null) {
            existingCar.setYear(updatedCar.getYear());
        }

        if (updatedCar.getColor() != null && !updatedCar.getColor().trim().isEmpty()) {
            existingCar.setColor(updatedCar.getColor().trim());
        }

        // Manejar actualización de placa
        if (updatedCar.getPlateNumber() != null && !updatedCar.getPlateNumber().trim().isEmpty()) {
            String normalizedPlate = PlateValidator.validateAndNormalize(updatedCar.getPlateNumber());

            // Solo verificar disponibilidad si la placa es diferente
            if (!existingCar.getPlateNumber().equals(normalizedPlate)) {
                if (carRepositoryPort.existsByPlateNumber(normalizedPlate)) {
                    throw CarAlreadyExistsException.withPlateNumber(normalizedPlate);
                }
                existingCar.setPlateNumber(normalizedPlate);
            }
        }

        // Actualizar URL de foto si se proporciona
        if (updatedCar.getPhotoUrl() != null) {
            // Permitir establecer photoUrl como null o vacío para eliminar la foto
            existingCar.setPhotoUrl(updatedCar.getPhotoUrl().trim());
        }

        // Actualizar timestamp
        existingCar.setUpdatedAt(LocalDateTime.now());

        return carRepositoryPort.update(existingCar);
    }

    @Override
    public boolean deleteCar(Long carId, Long userId) {
        if (carId == null) {
            throw new IllegalArgumentException("El ID del auto no puede ser nulo");
        }

        if (userId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }

        // Verificar que el auto existe y pertenece al usuario
        getCarById(carId, userId);

        return carRepositoryPort.deleteById(carId);
    }

    @Override
    public boolean carExists(Long carId) {
        if (carId == null) {
            return false;
        }

        return carRepositoryPort.findById(carId).isPresent();
    }

    @Override
    public boolean isPlateNumberAvailable(String plateNumber) {
        if (plateNumber == null || plateNumber.trim().isEmpty()) {
            return false;
        }

        try {
            String normalizedPlate = PlateValidator.validateAndNormalize(plateNumber);
            return !carRepositoryPort.existsByPlateNumber(normalizedPlate);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public boolean isPlateNumberAvailableForUpdate(String plateNumber, Long carId) {
        if (plateNumber == null || plateNumber.trim().isEmpty() || carId == null) {
            return false;
        }

        try {
            String normalizedPlate = PlateValidator.validateAndNormalize(plateNumber);

            // Obtener el auto actual
            Optional<Car> currentCar = carRepositoryPort.findById(carId);
            if (currentCar.isEmpty()) {
                return false;
            }

            // Si la placa es la misma, está disponible
            if (currentCar.get().getPlateNumber().equals(normalizedPlate)) {
                return true;
            }

            // Verificar si otro auto tiene esa placa
            return !carRepositoryPort.existsByPlateNumber(normalizedPlate);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public CarStats getCarStatsByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }

        List<Car> userCars = getCarsByUserId(userId);

        if (userCars.isEmpty()) {
            return new CarStats(userId, 0, 0, 0, null, null, null);
        }

        int totalCars = userCars.size();
        int vintageCarCount = (int) userCars.stream()
                .filter(car -> YearValidator.isVintage(car.getYear()))
                .count();

        int newCarCount = (int) userCars.stream()
                .filter(car -> YearValidator.isNew(car.getYear()))
                .count();

        // Marca más común
        String mostCommonBrand = userCars.stream()
                .collect(Collectors.groupingBy(Car::getBrand, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        // Año más nuevo y más viejo
        Integer newestYear = userCars.stream()
                .map(Car::getYear)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(null);

        Integer oldestYear = userCars.stream()
                .map(Car::getYear)
                .filter(Objects::nonNull)
                .min(Integer::compareTo)
                .orElse(null);

        return new CarStats(userId, totalCars, vintageCarCount, newCarCount,
                mostCommonBrand, newestYear, oldestYear);
    }

    @Override
    public List<Car> getAllCars() {
        return carRepositoryPort.findAll();
    }

    @Override
    public void validateCar(Car car) {
        if (car == null) {
            throw new IllegalArgumentException("El auto no puede ser nulo");
        }

        // Validar marca
        validateBrand(car.getBrand());

        // Validar modelo
        validateModel(car.getModel());

        // Validar año
        YearValidator.validateYear(car.getYear());

        // Validar placa
        PlateValidator.validateAndNormalize(car.getPlateNumber());

        // Validar color
        validateColor(car.getColor());
    }

    /**
     * Valida los datos del auto para actualización
     */
    private void validateCarForUpdate(Car car) {
        // Solo validar campos que NO son nulos Y no están vacíos
        if (car.getBrand() != null && !car.getBrand().trim().isEmpty()) {
            validateBrand(car.getBrand());
        }

        if (car.getModel() != null && !car.getModel().trim().isEmpty()) {
            validateModel(car.getModel());
        }

        if (car.getYear() != null) {
            YearValidator.validateYear(car.getYear());
        }

        if (car.getPlateNumber() != null && !car.getPlateNumber().trim().isEmpty()) {
            PlateValidator.validateAndNormalize(car.getPlateNumber());
        }

        if (car.getColor() != null && !car.getColor().trim().isEmpty()) {
            validateColor(car.getColor());
        }

        // PhotoUrl puede ser null, vacío o con valor - no necesita validación especial
    }
    /**
     * Valida la marca del auto
     */
    private void validateBrand(String brand) {
        if (brand == null || brand.trim().isEmpty()) {
            throw new IllegalArgumentException(DomainConstants.CAR_BRAND_REQUIRED);
        }

        String trimmedBrand = brand.trim();
        if (trimmedBrand.length() < DomainConstants.CAR_BRAND_MIN_LENGTH ||
                trimmedBrand.length() > DomainConstants.CAR_BRAND_MAX_LENGTH) {
            throw new IllegalArgumentException(DomainConstants.CAR_BRAND_LENGTH);
        }
    }

    /**
     * Valida el modelo del auto
     */
    private void validateModel(String model) {
        if (model == null || model.trim().isEmpty()) {
            throw new IllegalArgumentException(DomainConstants.CAR_MODEL_REQUIRED);
        }

        String trimmedModel = model.trim();
        if (trimmedModel.length() < DomainConstants.CAR_MODEL_MIN_LENGTH ||
                trimmedModel.length() > DomainConstants.CAR_MODEL_MAX_LENGTH) {
            throw new IllegalArgumentException(DomainConstants.CAR_MODEL_LENGTH);
        }
    }

    /**
     * Valida el color del auto
     */
    private void validateColor(String color) {
        if (color == null || color.trim().isEmpty()) {
            throw new IllegalArgumentException(DomainConstants.CAR_COLOR_REQUIRED);
        }

        String trimmedColor = color.trim();
        if (trimmedColor.length() < DomainConstants.CAR_COLOR_MIN_LENGTH ||
                trimmedColor.length() > DomainConstants.CAR_COLOR_MAX_LENGTH) {
            throw new IllegalArgumentException(DomainConstants.CAR_COLOR_LENGTH);
        }
    }
}