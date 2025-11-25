package com.anborja.tucarro.infrastructure.driven.jpa.sqlserver.adapter;

import com.anborja.tucarro.domain.model.Car;
import com.anborja.tucarro.domain.spi.ICarRepositoryPort;
import com.anborja.tucarro.infrastructure.driven.jpa.sqlserver.entity.CarEntity;
import com.anborja.tucarro.infrastructure.driven.jpa.sqlserver.entity.UserEntity;
import com.anborja.tucarro.infrastructure.driven.jpa.sqlserver.mapper.ICarEntityMapper;
import com.anborja.tucarro.infrastructure.driven.jpa.sqlserver.repository.ICarRepository;
import com.anborja.tucarro.infrastructure.driven.jpa.sqlserver.repository.IUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class CarRepositoryAdapter implements ICarRepositoryPort {

    private final ICarRepository carRepository;
    private final IUserRepository userRepository;
    private final ICarEntityMapper carEntityMapper;

    public CarRepositoryAdapter(ICarRepository carRepository,
                                IUserRepository userRepository,
                                ICarEntityMapper carEntityMapper) {
        this.carRepository = carRepository;
        this.userRepository = userRepository;
        this.carEntityMapper = carEntityMapper;
    }

    @Override
    public Car save(Car car) {
        if (car == null) {
            throw new IllegalArgumentException("El auto no puede ser nulo");
        }

        if (car.getUserId() == null) {
            throw new IllegalArgumentException("El auto debe tener un usuario asociado");
        }

        // Buscar el usuario
        UserEntity userEntity = userRepository.findById(car.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        CarEntity carEntity;

        if (car.getId() == null) {
            // Crear nuevo auto
            carEntity = carEntityMapper.domainToEntityForCreation(car);
            carEntity.setUser(userEntity);
        } else {
            // Actualizar auto existente
            Optional<CarEntity> existingEntity = carRepository.findById(car.getId());
            if (existingEntity.isPresent()) {
                carEntity = existingEntity.get();
                carEntityMapper.updateEntityFromDomain(car, carEntity);
                // Mantener la relación con el usuario
                if (!carEntity.getUser().getId().equals(car.getUserId())) {
                    carEntity.setUser(userEntity);
                }
            } else {
                carEntity = carEntityMapper.domainToEntity(car);
                carEntity.setUser(userEntity);
            }
        }

        CarEntity savedEntity = carRepository.save(carEntity);
        return carEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    public Optional<Car> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        return carRepository.findById(id)
                .map(carEntityMapper::entityToDomain);
    }

    @Override
    public Optional<Car> findByPlateNumber(String plateNumber) {
        if (plateNumber == null || plateNumber.trim().isEmpty()) {
            return Optional.empty();
        }

        return carRepository.findByPlateNumber(plateNumber.trim().toUpperCase())
                .map(carEntityMapper::entityToDomain);
    }

    @Override
    public List<Car> findByUserId(Long userId) {
        if (userId == null) {
            return List.of();
        }

        List<CarEntity> entities = carRepository.findByUserId(userId);
        return carEntityMapper.entitiesToDomain(entities);
    }

    @Override
    public boolean existsByPlateNumber(String plateNumber) {
        if (plateNumber == null || plateNumber.trim().isEmpty()) {
            return false;
        }

        return carRepository.existsByPlateNumber(plateNumber.trim().toUpperCase());
    }

    @Override
    public boolean existsByPlateNumberAndUserIdNot(String plateNumber, Long userId) {
        if (plateNumber == null || plateNumber.trim().isEmpty() || userId == null) {
            return false;
        }

        return carRepository.existsByPlateNumberAndUserIdNot(
                plateNumber.trim().toUpperCase(), userId);
    }

    @Override
    public Car update(Car car) {
        if (car == null || car.getId() == null) {
            throw new IllegalArgumentException("Auto y ID no pueden ser nulos para actualizar");
        }

        Optional<CarEntity> existingEntity = carRepository.findById(car.getId());
        if (existingEntity.isEmpty()) {
            throw new IllegalArgumentException("Auto no encontrado para actualizar");
        }

        CarEntity carEntity = existingEntity.get();
        carEntityMapper.updateEntityFromDomain(car, carEntity);

        // Si se cambió el usuario, actualizar la relación
        if (car.getUserId() != null && !carEntity.getUser().getId().equals(car.getUserId())) {
            UserEntity newUser = userRepository.findById(car.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            carEntity.setUser(newUser);
        }

        CarEntity updatedEntity = carRepository.save(carEntity);
        return carEntityMapper.entityToDomain(updatedEntity);
    }

    @Override
    public boolean deleteById(Long id) {
        if (id == null) {
            return false;
        }

        if (!carRepository.existsById(id)) {
            return false;
        }

        try {
            carRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public int deleteAllByUserId(Long userId) {
        if (userId == null) {
            return 0;
        }

        try {
            return carRepository.deleteAllByUserId(userId);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int countByUserId(Long userId) {
        if (userId == null) {
            return 0;
        }

        return carRepository.countByUserId(userId);
    }

    @Override
    public List<Car> findAll() {
        List<CarEntity> entities = carRepository.findAll();
        return carEntityMapper.entitiesToDomain(entities);
    }

    @Override
    public List<Car> findByBrand(String brand) {
        if (brand == null || brand.trim().isEmpty()) {
            return List.of();
        }

        List<CarEntity> entities = carRepository.findByBrandIgnoreCase(brand.trim());
        return carEntityMapper.entitiesToDomain(entities);
    }

    @Override
    public List<Car> findByModel(String model) {
        if (model == null || model.trim().isEmpty()) {
            return List.of();
        }

        List<CarEntity> entities = carRepository.findByModelIgnoreCase(model.trim());
        return carEntityMapper.entitiesToDomain(entities);
    }

    @Override
    public List<Car> findByYear(Integer year) {
        if (year == null) {
            return List.of();
        }

        List<CarEntity> entities = carRepository.findByYear(year);
        return carEntityMapper.entitiesToDomain(entities);
    }

    @Override
    public List<Car> findByYearBetween(Integer minYear, Integer maxYear) {
        if (minYear == null || maxYear == null) {
            return List.of();
        }

        List<CarEntity> entities = carRepository.findByYearBetween(minYear, maxYear);
        return carEntityMapper.entitiesToDomain(entities);
    }

    @Override
    public List<Car> findByColor(String color) {
        if (color == null || color.trim().isEmpty()) {
            return List.of();
        }

        List<CarEntity> entities = carRepository.findByColorIgnoreCase(color.trim());
        return carEntityMapper.entitiesToDomain(entities);
    }

    @Override
    public List<Car> findByBrandAndModelAndUserId(String brand, String model, Long userId) {
        if (brand == null || model == null || userId == null) {
            return List.of();
        }

        List<CarEntity> entities = carRepository.findByBrandAndModelAndUserId(
                brand.trim(), model.trim(), userId);
        return carEntityMapper.entitiesToDomain(entities);
    }

    @Override
    public List<Car> findByBrandContainingOrModelContainingAndUserId(String searchTerm, Long userId) {
        if (searchTerm == null || searchTerm.trim().isEmpty() || userId == null) {
            return List.of();
        }

        List<CarEntity> entities = carRepository.findByBrandContainingOrModelContainingAndUserId(
                searchTerm.trim(), userId);
        return carEntityMapper.entitiesToDomain(entities);
    }

    @Override
    public long count() {
        return carRepository.count();
    }

    /**
     * Métodos adicionales que aprovechan las capacidades del repositorio JPA
     */

    public List<Car> findVintageCarsByUserId(Long userId, Integer vintageYear) {
        if (userId == null || vintageYear == null) {
            return List.of();
        }

        List<CarEntity> entities = carRepository.findVintageCarsByUserId(userId, vintageYear);
        return carEntityMapper.entitiesToDomain(entities);
    }

    public List<Car> findNewCarsByUserId(Long userId, Integer newCarYear) {
        if (userId == null || newCarYear == null) {
            return List.of();
        }

        List<CarEntity> entities = carRepository.findNewCarsByUserId(userId, newCarYear);
        return carEntityMapper.entitiesToDomain(entities);
    }

    public List<Car> findByUserIdOrderByYearDesc(Long userId) {
        if (userId == null) {
            return List.of();
        }

        List<CarEntity> entities = carRepository.findByUserIdOrderByYearDesc(userId);
        return carEntityMapper.entitiesToDomain(entities);
    }

    public List<Car> findByUserIdOrderByYearAsc(Long userId) {
        if (userId == null) {
            return List.of();
        }

        List<CarEntity> entities = carRepository.findByUserIdOrderByYearAsc(userId);
        return carEntityMapper.entitiesToDomain(entities);
    }

    public List<Car> findByMultipleCriteria(Long userId, String brand, String model,
                                            Integer year, String color) {
        if (userId == null) {
            return List.of();
        }

        List<CarEntity> entities = carRepository.findByMultipleCriteria(
                userId, brand, model, year, color);
        return carEntityMapper.entitiesToDomain(entities);
    }

    public List<String> findMostCommonBrandByUserId(Long userId) {
        if (userId == null) {
            return List.of();
        }

        return carRepository.findMostCommonBrandByUserId(userId);
    }

    public Optional<YearStats> findYearStatsByUserId(Long userId) {
        if (userId == null) {
            return Optional.empty();
        }

        List<Object[]> results = carRepository.findYearStatsByUserId(userId);
        if (results.isEmpty() || results.get(0)[0] == null) {
            return Optional.empty();
        }

        Object[] result = results.get(0);
        Integer minYear = (Integer) result[0];
        Integer maxYear = (Integer) result[1];

        return Optional.of(new YearStats(minYear, maxYear));
    }

    public List<Car> findCarsWithPhotoByUserId(Long userId) {
        if (userId == null) {
            return List.of();
        }

        List<CarEntity> entities = carRepository.findCarsWithPhotoByUserId(userId);
        return carEntityMapper.entitiesToDomain(entities);
    }

    public List<Car> findCarsWithoutPhotoByUserId(Long userId) {
        if (userId == null) {
            return List.of();
        }

        List<CarEntity> entities = carRepository.findCarsWithoutPhotoByUserId(userId);
        return carEntityMapper.entitiesToDomain(entities);
    }

    /**
     * Clase para estadísticas de años
     */
    public static class YearStats {
        private final Integer minYear;
        private final Integer maxYear;

        public YearStats(Integer minYear, Integer maxYear) {
            this.minYear = minYear;
            this.maxYear = maxYear;
        }

        public Integer getMinYear() { return minYear; }
        public Integer getMaxYear() { return maxYear; }
    }

    @Override
    public Page<Car> findByUserIdPaginated(Long userId, Pageable pageable) {
        if (userId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }

        Page<CarEntity> entities = carRepository.findByUserId(userId, pageable);
        return entities.map(carEntityMapper::entityToDomain);
    }

    @Override
    public Page<Car> searchCarsPaginated(Long userId, String searchTerm, Pageable pageable) {
        if (userId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }

        Page<CarEntity> entities;

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            entities = carRepository.findByUserId(userId, pageable);
        } else {
            entities = carRepository.findByUserIdWithSearch(userId, searchTerm.trim(), pageable);
        }

        return entities.map(carEntityMapper::entityToDomain);
    }

    @Override
    public Page<Car> findByUserIdWithFiltersPaginated(Long userId, String brand, String model,
                                                      Integer year, String color,
                                                      Integer minYear, Integer maxYear,
                                                      Pageable pageable) {
        if (userId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }

        // Normalizar strings para búsqueda (null si están vacíos)
        String normalizedBrand = (brand != null && !brand.trim().isEmpty()) ? brand.trim() : null;
        String normalizedModel = (model != null && !model.trim().isEmpty()) ? model.trim() : null;
        String normalizedColor = (color != null && !color.trim().isEmpty()) ? color.trim() : null;

        Page<CarEntity> entities = carRepository.findByUserIdWithFilters(
                userId, normalizedBrand, normalizedModel, year, normalizedColor,
                minYear, maxYear, pageable);

        return entities.map(carEntityMapper::entityToDomain);
    }
}