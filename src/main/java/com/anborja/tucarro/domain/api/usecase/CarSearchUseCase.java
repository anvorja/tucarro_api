package com.anborja.tucarro.domain.api.usecase;

import com.anborja.tucarro.domain.api.ICarSearchServicePort;
import com.anborja.tucarro.domain.exception.CarNotFoundException;
import com.anborja.tucarro.domain.exception.UserNotFoundException;
import com.anborja.tucarro.domain.model.Car;
import com.anborja.tucarro.domain.model.CarSearchCriteria;
import com.anborja.tucarro.domain.spi.ICarRepositoryPort;
import com.anborja.tucarro.domain.spi.IUserRepositoryPort;
import com.anborja.tucarro.infrastructure.driving.http.dto.request.CarSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class CarSearchUseCase implements ICarSearchServicePort {

    private final ICarRepositoryPort carRepositoryPort;
    private final IUserRepositoryPort userRepositoryPort;

    public CarSearchUseCase(ICarRepositoryPort carRepositoryPort, IUserRepositoryPort userRepositoryPort) {
        this.carRepositoryPort = carRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public List<Car> searchByPlateNumber(String plateNumber, Long userId) {
        validateUserId(userId);

        if (plateNumber == null || plateNumber.trim().isEmpty()) {
            return List.of();
        }

        return carRepositoryPort.findByPlateNumber(plateNumber.trim())
                .filter(car -> car.getUserId().equals(userId))
                .map(List::of)
                .orElse(List.of());
    }

    @Override
    public List<Car> searchByModel(String model, Long userId) {
        validateUserId(userId);

        if (model == null || model.trim().isEmpty()) {
            return carRepositoryPort.findByUserId(userId);
        }

        return carRepositoryPort.findByUserId(userId).stream()
                .filter(car -> car.getModel() != null &&
                        car.getModel().toLowerCase().contains(model.trim().toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Car> searchByBrand(String brand, Long userId) {
        validateUserId(userId);

        if (brand == null || brand.trim().isEmpty()) {
            return carRepositoryPort.findByUserId(userId);
        }

        return carRepositoryPort.findByUserId(userId).stream()
                .filter(car -> car.getBrand() != null &&
                        car.getBrand().toLowerCase().contains(brand.trim().toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Car> filterByYear(Integer year, Long userId) {
        validateUserId(userId);

        if (year == null) {
            return carRepositoryPort.findByUserId(userId);
        }

        return carRepositoryPort.findByUserId(userId).stream()
                .filter(car -> car.getYear() != null && car.getYear().equals(year))
                .collect(Collectors.toList());
    }

    @Override
    public List<Car> filterByYearRange(Integer minYear, Integer maxYear, Long userId) {
        validateUserId(userId);

        if (minYear == null && maxYear == null) {
            return carRepositoryPort.findByUserId(userId);
        }

        return carRepositoryPort.findByUserId(userId).stream()
                .filter(car -> {
                    if (car.getYear() == null) return false;

                    boolean minYearMatches = minYear == null || car.getYear() >= minYear;
                    boolean maxYearMatches = maxYear == null || car.getYear() <= maxYear;

                    return minYearMatches && maxYearMatches;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Car> filterByColor(String color, Long userId) {
        validateUserId(userId);

        if (color == null || color.trim().isEmpty()) {
            return carRepositoryPort.findByUserId(userId);
        }

        return carRepositoryPort.findByUserId(userId).stream()
                .filter(car -> car.getColor() != null &&
                        car.getColor().equalsIgnoreCase(color.trim()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Car> generalSearch(String searchTerm, Long userId) {
        validateUserId(userId);

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return carRepositoryPort.findByUserId(userId);
        }

        String term = searchTerm.trim().toLowerCase();

        return carRepositoryPort.findByUserId(userId).stream()
                .filter(car ->
                        (car.getBrand() != null && car.getBrand().toLowerCase().contains(term)) ||
                                (car.getModel() != null && car.getModel().toLowerCase().contains(term)) ||
                                (car.getColor() != null && car.getColor().toLowerCase().contains(term))
                )
                .collect(Collectors.toList());
    }

    @Override
    public List<Car> advancedSearch(SearchCriteria searchCriteria, Long userId) {
        validateUserId(userId);

        if (searchCriteria == null) {
            return carRepositoryPort.findByUserId(userId);
        }

        List<Car> cars = carRepositoryPort.findByUserId(userId);

        // Aplicar filtros secuencialmente
        cars = cars.stream()
                .filter(car -> matchesBrand(car, searchCriteria.getBrand()))
                .filter(car -> matchesModel(car, searchCriteria.getModel()))
                .filter(car -> matchesYear(car, searchCriteria.getYear()))
                .filter(car -> matchesYearRange(car, searchCriteria.getMinYear(), searchCriteria.getMaxYear()))
                .filter(car -> matchesColor(car, searchCriteria.getColor()))
                .filter(car -> matchesPlate(car, searchCriteria.getPlateNumber()))
                .filter(car -> matchesGeneralTerm(car, searchCriteria.getGeneralSearchTerm()))
                .collect(Collectors.toList());

        // Aplicar ordenamiento
        if (searchCriteria.getSortOrder() != null) {
            cars = applySortOrder(cars, searchCriteria.getSortOrder());
        }

        return cars;
    }

    @Override
    public List<Car> getVintageCars(Long userId) {
        validateUserId(userId);

        int currentYear = LocalDateTime.now().getYear();
        int vintageYear = currentYear - 25;

        return carRepositoryPort.findByUserId(userId).stream()
                .filter(car -> car.getYear() != null && car.getYear() <= vintageYear)
                .collect(Collectors.toList());
    }

    @Override
    public List<Car> getNewCars(Long userId) {
        validateUserId(userId);

        int currentYear = LocalDateTime.now().getYear();
        int newCarYear = currentYear - 3;

        return carRepositoryPort.findByUserId(userId).stream()
                .filter(car -> car.getYear() != null && car.getYear() >= newCarYear)
                .collect(Collectors.toList());
    }

    @Override
    public List<Car> getCarsOrderedByYearDesc(Long userId) {
        validateUserId(userId);

        return carRepositoryPort.findByUserId(userId).stream()
                .sorted(Comparator.comparing(Car::getYear,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    @Override
    public List<Car> getCarsOrderedByYearAsc(Long userId) {
        validateUserId(userId);

        return carRepositoryPort.findByUserId(userId).stream()
                .sorted(Comparator.comparing(Car::getYear,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    @Override
    public List<Car> searchCars(Long userId, CarSearchRequest searchRequest) {
        validateUserId(userId);

        if (searchRequest == null || !searchRequest.hasAnyFilter()) {
            return getSortedCars(userId,
                    searchRequest != null ? searchRequest.getSortBy() : "createdAt",
                    searchRequest == null || !searchRequest.isSortingDescending());
        }

        List<Car> cars;

        // Si tiene término de búsqueda general, usarlo como base
        if (searchRequest.hasSearchTerm()) {
            cars = generalSearch(searchRequest.getSearchTerm(), userId);
        } else {
            cars = carRepositoryPort.findByUserId(userId);
        }

        // Aplicar filtros específicos usando CarSearchRequest
        cars = applySearchRequestFilters(cars, searchRequest);

        // Aplicar ordenamiento
        return applySorting(cars, searchRequest.getSortBy(), !searchRequest.isSortingDescending());
    }

    @Override
    public Car findByPlate(Long userId, String plateNumber) {
        validateUserId(userId);

        if (plateNumber == null || plateNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de placa es requerido");
        }

        return carRepositoryPort.findByPlateNumber(plateNumber.trim())
                .filter(car -> car.getUserId().equals(userId))
                .orElseThrow(() -> new CarNotFoundException(plateNumber, true));
    }

    @Override
    public boolean isPlateAvailable(String plateNumber) {
        if (plateNumber == null || plateNumber.trim().isEmpty()) {
            return false;
        }

        return !carRepositoryPort.existsByPlateNumber(plateNumber.trim());
    }

    @Override
    public boolean isPlateAvailableForUser(String plateNumber, Long userId) {
        if (plateNumber == null || plateNumber.trim().isEmpty() || userId == null) {
            return false;
        }

        return !carRepositoryPort.existsByPlateNumberAndUserIdNot(plateNumber.trim(), userId);
    }

    @Override
    public List<Car> getCarsWithPhoto(Long userId) {
        validateUserId(userId);

        return carRepositoryPort.findByUserId(userId).stream()
                .filter(car -> car.getPhotoUrl() != null && !car.getPhotoUrl().trim().isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public List<Car> getCarsWithoutPhoto(Long userId) {
        validateUserId(userId);

        return carRepositoryPort.findByUserId(userId).stream()
                .filter(car -> car.getPhotoUrl() == null || car.getPhotoUrl().trim().isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public List<Car> getSortedCars(Long userId, String sortBy, boolean ascending) {
        validateUserId(userId);

        List<Car> cars = carRepositoryPort.findByUserId(userId);
        return applySorting(cars, sortBy, ascending);
    }

    @Override
    public List<String> getMostCommonBrands(Long userId) {
        validateUserId(userId);

        return carRepositoryPort.findByUserId(userId).stream()
                .map(Car::getBrand)
                .filter(brand -> brand != null && !brand.trim().isEmpty())
                .collect(Collectors.groupingBy(
                        String::toLowerCase,
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .sorted((entry1, entry2) -> Long.compare(entry2.getValue(), entry1.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public YearStatistics getYearStatistics(Long userId) {
        validateUserId(userId);

        List<Car> cars = carRepositoryPort.findByUserId(userId);

        if (cars.isEmpty()) {
            return new YearStatistics(null, null, null, 0);
        }

        List<Integer> years = cars.stream()
                .map(Car::getYear)
                .filter(Objects::nonNull)
                .toList();

        if (years.isEmpty()) {
            return new YearStatistics(null, null, null, cars.size());
        }

        Integer minYear = years.stream().min(Integer::compareTo).orElse(null);
        Integer maxYear = years.stream().max(Integer::compareTo).orElse(null);
        Double averageYear = years.stream().mapToInt(Integer::intValue).average().orElse(0.0);

        return new YearStatistics(minYear, maxYear, averageYear, cars.size());
    }

    // ============================================================================
    // MÉTODOS HELPER PRIVADOS
    // ============================================================================

    /**
     * Aplica filtros usando CarSearchRequest
     */
    private List<Car> applySearchRequestFilters(List<Car> cars, CarSearchRequest searchRequest) {
        return cars.stream()
                .filter(car -> matchesBrandFilter(car, searchRequest.getBrand()))
                .filter(car -> matchesModelFilter(car, searchRequest.getModel()))
                .filter(car -> matchesYearFilter(car, searchRequest.getYear()))
                .filter(car -> matchesYearRangeFilter(car, searchRequest.getMinYear(), searchRequest.getMaxYear()))
                .filter(car -> matchesColorFilter(car, searchRequest.getColor()))
                .filter(car -> matchesPlateFilter(car, searchRequest.getPlateNumber()))
                .filter(car -> matchesVintageFilter(car, searchRequest.getIsVintage()))
                .filter(car -> matchesNewFilter(car, searchRequest.getIsNew()))
                .filter(car -> matchesPhotoFilter(car, searchRequest.getHasPhoto()))
                .collect(Collectors.toList());
    }

    /**
     * Aplica ordenamiento usando enum SortOrder
     */
    private List<Car> applySortOrder(List<Car> cars, SortOrder sortOrder) {
        Comparator<Car> comparator = switch (sortOrder) {
            case YEAR_ASC -> Comparator.comparing(Car::getYear, Comparator.nullsLast(Integer::compareTo));
            case YEAR_DESC -> Comparator.comparing(Car::getYear, Comparator.nullsLast(Comparator.reverseOrder()));
            case BRAND_ASC -> Comparator.comparing(Car::getBrand, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
            case BRAND_DESC -> Comparator.comparing(Car::getBrand, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER.reversed()));
            case MODEL_ASC -> Comparator.comparing(Car::getModel, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
            case MODEL_DESC -> Comparator.comparing(Car::getModel, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER.reversed()));
            case CREATED_ASC -> Comparator.comparing(Car::getCreatedAt, Comparator.nullsLast(LocalDateTime::compareTo));
            case CREATED_DESC -> Comparator.comparing(Car::getCreatedAt, Comparator.nullsLast(LocalDateTime::compareTo).reversed());
        };

        return cars.stream().sorted(comparator).collect(Collectors.toList());
    }

    /**
     * Aplica ordenamiento usando string
     */
    private List<Car> applySorting(List<Car> cars, String sortBy, boolean ascending) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "createdAt";
        }

        Comparator<Car> comparator = getComparator(sortBy.trim());

        if (!ascending) {
            comparator = comparator.reversed();
        }

        return cars.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el comparador apropiado según el campo de ordenamiento
     */
    private Comparator<Car> getComparator(String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "brand" -> Comparator.comparing(Car::getBrand, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
            case "model" -> Comparator.comparing(Car::getModel, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
            case "year" -> Comparator.comparing(Car::getYear, Comparator.nullsLast(Integer::compareTo));
            case "color" -> Comparator.comparing(Car::getColor, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
            case "updatedat" -> Comparator.comparing(Car::getUpdatedAt, Comparator.nullsLast(LocalDateTime::compareTo));
            default -> Comparator.comparing(Car::getCreatedAt, Comparator.nullsLast(LocalDateTime::compareTo));
        };
    }

    // Métodos de filtro para SearchCriteria (métodos existentes)
    private boolean matchesBrand(Car car, String brand) {
        return brand == null || brand.trim().isEmpty() ||
                (car.getBrand() != null && car.getBrand().toLowerCase().contains(brand.trim().toLowerCase()));
    }

    private boolean matchesModel(Car car, String model) {
        return model == null || model.trim().isEmpty() ||
                (car.getModel() != null && car.getModel().toLowerCase().contains(model.trim().toLowerCase()));
    }

    private boolean matchesYear(Car car, Integer year) {
        return year == null || (car.getYear() != null && car.getYear().equals(year));
    }

    private boolean matchesYearRange(Car car, Integer minYear, Integer maxYear) {
        if (car.getYear() == null) return true;

        boolean minMatches = minYear == null || car.getYear() >= minYear;
        boolean maxMatches = maxYear == null || car.getYear() <= maxYear;

        return minMatches && maxMatches;
    }

    private boolean matchesColor(Car car, String color) {
        return color == null || color.trim().isEmpty() ||
                (car.getColor() != null && car.getColor().equalsIgnoreCase(color.trim()));
    }

    private boolean matchesPlate(Car car, String plateNumber) {
        return plateNumber == null || plateNumber.trim().isEmpty() ||
                (car.getPlateNumber() != null && car.getPlateNumber().equalsIgnoreCase(plateNumber.trim()));
    }

    private boolean matchesGeneralTerm(Car car, String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) return true;

        String term = searchTerm.trim().toLowerCase();
        return (car.getBrand() != null && car.getBrand().toLowerCase().contains(term)) ||
                (car.getModel() != null && car.getModel().toLowerCase().contains(term)) ||
                (car.getColor() != null && car.getColor().toLowerCase().contains(term));
    }

    // Métodos de filtro para CarSearchRequest (nuevos)
    private boolean matchesBrandFilter(Car car, String brand) {
        return brand == null || brand.trim().isEmpty() ||
                (car.getBrand() != null && car.getBrand().equalsIgnoreCase(brand.trim()));
    }

    private boolean matchesModelFilter(Car car, String model) {
        return model == null || model.trim().isEmpty() ||
                (car.getModel() != null && car.getModel().equalsIgnoreCase(model.trim()));
    }

    private boolean matchesYearFilter(Car car, Integer year) {
        return year == null || (car.getYear() != null && car.getYear().equals(year));
    }

    private boolean matchesYearRangeFilter(Car car, Integer minYear, Integer maxYear) {
        if (car.getYear() == null) return true;

        boolean minMatches = minYear == null || car.getYear() >= minYear;
        boolean maxMatches = maxYear == null || car.getYear() <= maxYear;

        return minMatches && maxMatches;
    }

    private boolean matchesColorFilter(Car car, String color) {
        return color == null || color.trim().isEmpty() ||
                (car.getColor() != null && car.getColor().equalsIgnoreCase(color.trim()));
    }

    private boolean matchesPlateFilter(Car car, String plateNumber) {
        return plateNumber == null || plateNumber.trim().isEmpty() ||
                (car.getPlateNumber() != null && car.getPlateNumber().equalsIgnoreCase(plateNumber.trim()));
    }

    private boolean matchesVintageFilter(Car car, Boolean isVintage) {
        if (isVintage == null) return true;

        boolean carIsVintage = car.isVintage();
        return isVintage.equals(carIsVintage);
    }

    private boolean matchesNewFilter(Car car, Boolean isNew) {
        if (isNew == null) return true;

        int currentYear = LocalDateTime.now().getYear();
        boolean carIsNew = car.getYear() != null && (currentYear - car.getYear()) <= 3;

        return isNew.equals(carIsNew);
    }

    private boolean matchesPhotoFilter(Car car, Boolean hasPhoto) {
        if (hasPhoto == null) return true;

        boolean carHasPhoto = car.getPhotoUrl() != null && !car.getPhotoUrl().trim().isEmpty();
        return hasPhoto.equals(carHasPhoto);
    }

    /**
     * Valida que el usuario exista
     */
    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("ID de usuario es requerido");
        }

        if (userRepositoryPort.findById(userId).isEmpty()) {
            throw new UserNotFoundException(userId);
        }
    }

    @Override
    public Page<Car> searchCarsPaginated(Long userId, CarSearchRequest searchRequest,
                                         int page, int size, String sortBy, String sortDirection) {
        validateUserId(userId);

        // Validar parámetros de paginación
        if (page < 0) page = 0;
        if (size < 1 || size > 100) size = 20; // Limitar tamaño máximo

        // Crear Sort
        Sort sort = Sort.unsorted();
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection)
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            sort = Sort.by(direction, sortBy);
        }

        // Crear Pageable
        Pageable pageable = PageRequest.of(page, size, sort);

        // Convertir a criterios de búsqueda
        CarSearchCriteria criteria = convertToSearchCriteria(searchRequest);

        // Determinar qué tipo de búsqueda realizar
        if (criteria.hasSearchTerm()) {
            // Búsqueda por término general
            return carRepositoryPort.searchCarsPaginated(userId, criteria.getSearchTerm(), pageable);
        } else if (criteria.hasFilters()) {
            // Búsqueda con filtros específicos
            return carRepositoryPort.findByUserIdWithFiltersPaginated(
                    userId, criteria.getBrand(), criteria.getModel(),
                    criteria.getYear(), criteria.getColor(),
                    criteria.getMinYear(), criteria.getMaxYear(), pageable);
        } else {
            // Sin filtros, traer todos los autos del usuario
            return carRepositoryPort.findByUserIdPaginated(userId, pageable);
        }
    }

    /**
     * Convierte CarSearchRequest a CarSearchCriteria
     */
    private CarSearchCriteria convertToSearchCriteria(CarSearchRequest request) {
        if (request == null) {
            return CarSearchCriteria.empty();
        }

        CarSearchCriteria criteria = new CarSearchCriteria();
        criteria.setSearchTerm(request.getSearchTerm());
        criteria.setBrand(request.getBrand());
        criteria.setModel(request.getModel());
        criteria.setYear(request.getYear());
        criteria.setColor(request.getColor());
        criteria.setMinYear(request.getMinYear());
        criteria.setMaxYear(request.getMaxYear());

        return criteria;
    }
}