package com.anborja.tucarro.infrastructure.driving.http.controller;

import com.anborja.tucarro.domain.api.ICarSearchServicePort;
import com.anborja.tucarro.domain.model.Car;
import com.anborja.tucarro.infrastructure.documentation.ApiDocumentation;
import com.anborja.tucarro.infrastructure.driving.http.dto.request.CarSearchRequest;
import com.anborja.tucarro.infrastructure.driving.http.dto.response.CarResponse;
import com.anborja.tucarro.infrastructure.driving.http.dto.response.CarSearchResponse;
import com.anborja.tucarro.infrastructure.driving.http.dto.response.PagedResponse;
import com.anborja.tucarro.infrastructure.driving.http.mapper.ICarResponseMapper;
import com.anborja.tucarro.shared.constant.AppConstants;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(AppConstants.API_VERSION + AppConstants.CARS_ENDPOINT + "/search")
@Tag(name = "🔍 Búsqueda de Autos", description = "Búsquedas avanzadas y filtros para autos del usuario")
public class CarSearchControllerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(CarSearchControllerAdapter.class);

    private final ICarSearchServicePort carSearchServicePort;
    private final ICarResponseMapper carResponseMapper;

    public CarSearchControllerAdapter(ICarSearchServicePort carSearchServicePort,
                                      ICarResponseMapper carResponseMapper) {
        this.carSearchServicePort = carSearchServicePort;
        this.carResponseMapper = carResponseMapper;
    }

    /**
     * Búsqueda avanzada con múltiples criterios
     * POST /api/v1/cars/search
     */
    @PostMapping
    @ApiDocumentation.AdvancedSearchDocumentation
    public ResponseEntity<Map<String, Object>> searchCars(
            @Valid @RequestBody CarSearchRequest searchRequest,
            HttpServletRequest request) {

        Long userId = extractUserIdFromRequest(request);
        logger.info("Búsqueda avanzada para usuario ID: {} con criterios: {}", userId, searchRequest);

        try {
            List<Car> cars = carSearchServicePort.searchCars(userId, searchRequest);
            List<CarResponse> carResponses = carResponseMapper.domainListToResponseList(cars);

            // Construir respuesta con estadísticas
            String appliedFilters = buildAppliedFiltersString(searchRequest);
            CarSearchResponse.SearchMetadata metadata = new CarSearchResponse.SearchMetadata(
                    cars.size(), appliedFilters, searchRequest.getSortBy(),
                    searchRequest.getSortDirection(), searchRequest.getSearchTerm()
            );

            CarSearchResponse.SearchStatistics statistics = CarSearchResponse.SearchStatistics.fromCars(carResponses);
            CarSearchResponse searchResponse = new CarSearchResponse(carResponses, metadata, statistics);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Búsqueda completada exitosamente");
            response.put("data", searchResponse);

            logger.info("Búsqueda completada. Encontrados {} autos", cars.size());
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error durante la búsqueda: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Búsqueda rápida por término general
     * GET /api/v1/cars/search/quick?term=texto
     */
    @GetMapping("/quick")
    @ApiDocumentation.QuickSearchDocumentation
    public ResponseEntity<Map<String, Object>> quickSearch(
            @RequestParam(value = "term", required = false) String searchTerm,
            HttpServletRequest request) {

        Long userId = extractUserIdFromRequest(request);
        logger.info("Búsqueda rápida para usuario ID: {} con término: '{}'", userId, searchTerm);

        try {
            List<Car> cars = carSearchServicePort.generalSearch(searchTerm, userId);
            List<CarResponse> carResponses = carResponseMapper.domainListToResponseList(cars);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Búsqueda rápida completada");
            response.put("data", carResponses);
            response.put("total_results", cars.size());
            response.put("search_term", searchTerm);

            logger.info("Búsqueda rápida completada. Encontrados {} autos", cars.size());
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error durante la búsqueda rápida: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Buscar por placa específica
     * GET /api/v1/cars/search/plate/{plateNumber}
     */
    @GetMapping("/plate/{plateNumber}")
    public ResponseEntity<Map<String, Object>> findByPlate(
            @PathVariable String plateNumber,
            HttpServletRequest request) {

        Long userId = extractUserIdFromRequest(request);
        logger.info("Búsqueda por placa '{}' para usuario ID: {}", plateNumber, userId);

        try {
            Car car = carSearchServicePort.findByPlate(userId, plateNumber);
            CarResponse carResponse = carResponseMapper.domainToResponse(car);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Auto encontrado");
            response.put("data", carResponse);

            logger.info("Auto encontrado por placa '{}'", plateNumber);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error al buscar por placa '{}': {}", plateNumber, e.getMessage());
            throw e;
        }
    }

    /**
     * Verificar disponibilidad de placa (útil para el frontend)
     * GET /api/v1/cars/search/plate-available?plate=ABC123
     */
    @GetMapping("/plate-available")
    @ApiDocumentation.CheckPlateAvailabilityDocumentation
    public ResponseEntity<Map<String, Object>> checkPlateAvailability(
            @RequestParam("plate") String plateNumber,
            @RequestParam(value = "excludeUserId", required = false) Long excludeUserId) {

        logger.info("Verificando disponibilidad de placa: '{}'", plateNumber);

        try {
            boolean isAvailable;

            if (excludeUserId != null) {
                isAvailable = carSearchServicePort.isPlateAvailableForUser(plateNumber, excludeUserId);
            } else {
                isAvailable = carSearchServicePort.isPlateAvailable(plateNumber);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("available", isAvailable);
            response.put("plate_number", plateNumber);
            response.put("message", isAvailable ? "Placa disponible" : "Placa ya registrada");

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error al verificar disponibilidad de placa '{}': {}", plateNumber, e.getMessage());
            throw e;
        }
    }

    /**
     * Filtrar por marca
     * GET /api/v1/cars/search/brand/{brand}
     */
    @GetMapping("/brand/{brand}")
    @ApiDocumentation.FilterByBrandDocumentation
    public ResponseEntity<Map<String, Object>> filterByBrand(
            @PathVariable String brand,
            HttpServletRequest request) {

        Long userId = extractUserIdFromRequest(request);
        logger.info("Filtro por marca '{}' para usuario ID: {}", brand, userId);

        try {
            List<Car> cars = carSearchServicePort.searchByBrand(brand, userId);
            List<CarResponse> carResponses = carResponseMapper.domainListToResponseList(cars);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Filtro por marca aplicado");
            response.put("data", carResponses);
            response.put("total_results", cars.size());
            response.put("filter_brand", brand);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error al filtrar por marca '{}': {}", brand, e.getMessage());
            throw e;
        }
    }

    /**
     * Filtrar por modelo
     * GET /api/v1/cars/search/model/{model}
     */
    @GetMapping("/model/{model}")
    @ApiDocumentation.FilterByModelDocumentation
    public ResponseEntity<Map<String, Object>> filterByModel(
            @PathVariable String model,
            HttpServletRequest request) {

        Long userId = extractUserIdFromRequest(request);
        logger.info("Filtro por modelo '{}' para usuario ID: {}", model, userId);

        try {
            List<Car> cars = carSearchServicePort.searchByModel(model, userId);
            List<CarResponse> carResponses = carResponseMapper.domainListToResponseList(cars);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Filtro por modelo aplicado");
            response.put("data", carResponses);
            response.put("total_results", cars.size());
            response.put("filter_model", model);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error al filtrar por modelo '{}': {}", model, e.getMessage());
            throw e;
        }
    }

    /**
     * Filtrar por año
     * GET /api/v1/cars/search/year/{year}
     */
    @GetMapping("/year/{year}")
    @ApiDocumentation.FilterByYearDocumentation
    public ResponseEntity<Map<String, Object>> filterByYear(
            @PathVariable Integer year,
            HttpServletRequest request) {

        Long userId = extractUserIdFromRequest(request);
        logger.info("Filtro por año {} para usuario ID: {}", year, userId);

        try {
            List<Car> cars = carSearchServicePort.filterByYear(year, userId);
            List<CarResponse> carResponses = carResponseMapper.domainListToResponseList(cars);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Filtro por año aplicado");
            response.put("data", carResponses);
            response.put("total_results", cars.size());
            response.put("filter_year", year);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error al filtrar por año {}: {}", year, e.getMessage());
            throw e;
        }
    }

    /**
     * Obtener autos vintage (más de 25 años)
     * GET /api/v1/cars/search/vintage
     */
    @GetMapping("/vintage")
    @ApiDocumentation.GetVintageCarsDocumentation
    public ResponseEntity<Map<String, Object>> getVintageCars(HttpServletRequest request) {
        Long userId = extractUserIdFromRequest(request);
        logger.info("Obteniendo autos vintage para usuario ID: {}", userId);

        try {
            List<Car> cars = carSearchServicePort.getVintageCars(userId);
            List<CarResponse> carResponses = carResponseMapper.domainListToResponseList(cars);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Autos vintage obtenidos");
            response.put("data", carResponses);
            response.put("total_results", cars.size());
            response.put("filter_type", "vintage");

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error al obtener autos vintage: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Obtener autos nuevos (3 años o menos)
     * GET /api/v1/cars/search/new
     */
    @GetMapping("/new")
    @ApiDocumentation.GetNewCarsDocumentation
    public ResponseEntity<Map<String, Object>> getNewCars(HttpServletRequest request) {
        Long userId = extractUserIdFromRequest(request);
        logger.info("Obteniendo autos nuevos para usuario ID: {}", userId);

        try {
            List<Car> cars = carSearchServicePort.getNewCars(userId);
            List<CarResponse> carResponses = carResponseMapper.domainListToResponseList(cars);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Autos nuevos obtenidos");
            response.put("data", carResponses);
            response.put("total_results", cars.size());
            response.put("filter_type", "new");

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error al obtener autos nuevos: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Obtener estadísticas de autos del usuario
     * GET /api/v1/cars/search/statistics
     */
    @GetMapping("/statistics")
    @ApiDocumentation.CarStatisticsDocumentation
    public ResponseEntity<Map<String, Object>> getUserCarStatistics(HttpServletRequest request) {
        Long userId = extractUserIdFromRequest(request);
        logger.info("Obteniendo estadísticas para usuario ID: {}", userId);

        try {
            ICarSearchServicePort.YearStatistics yearStats = carSearchServicePort.getYearStatistics(userId);
            List<String> commonBrands = carSearchServicePort.getMostCommonBrands(userId);
            List<Car> vintageCars = carSearchServicePort.getVintageCars(userId);
            List<Car> newCars = carSearchServicePort.getNewCars(userId);
            List<Car> carsWithPhoto = carSearchServicePort.getCarsWithPhoto(userId);

            Map<String, Object> statistics = new HashMap<>();
            statistics.put("total_cars", yearStats.getTotalCars());
            statistics.put("vintage_count", vintageCars.size());
            statistics.put("new_count", newCars.size());
            statistics.put("with_photo_count", carsWithPhoto.size());
            statistics.put("min_year", yearStats.getMinYear());
            statistics.put("max_year", yearStats.getMaxYear());
            statistics.put("average_year", yearStats.getAverageYear());
            statistics.put("year_range", yearStats.getYearRange());
            statistics.put("most_common_brands", commonBrands);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Estadísticas obtenidas exitosamente");
            response.put("data", statistics);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error al obtener estadísticas: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Health check del servicio de búsqueda
     * GET /api/v1/cars/search/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Car Search Service");
        response.put("timestamp", System.currentTimeMillis());
        response.put("endpoints_available", List.of(
                "POST /search", "GET /quick", "GET /plate/{plate}",
                "GET /brand/{brand}", "GET /model/{model}", "GET /year/{year}",
                "GET /vintage", "GET /new", "GET /statistics"
        ));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * NUEVO: Búsqueda paginada principal
     * GET /api/v1/cars/search/paginated
     */
    @GetMapping("/paginated")
    @ApiDocumentation.PaginatedSearchDocumentation
    public ResponseEntity<Map<String, Object>> searchCarsPaginated(
            // Parámetros de paginación
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,

            // Parámetros de búsqueda
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) Integer minYear,
            @RequestParam(required = false) Integer maxYear,

            HttpServletRequest request) {

        Long userId = extractUserIdFromRequest(request);
        logger.info("Búsqueda paginada para usuario {}: page={}, size={}, sortBy={}",
                userId, page, size, sortBy);

        try {
            // Crear request de búsqueda
            CarSearchRequest searchRequest = new CarSearchRequest();
            searchRequest.setSearchTerm(searchTerm);
            searchRequest.setBrand(brand);
            searchRequest.setModel(model);
            searchRequest.setYear(year);
            searchRequest.setColor(color);
            searchRequest.setMinYear(minYear);
            searchRequest.setMaxYear(maxYear);

            // Ejecutar búsqueda paginada
            Page<Car> carsPage = carSearchServicePort.searchCarsPaginated(
                    userId, searchRequest, page, size, sortBy, sortDirection);

            // Convertir a DTOs
            List<CarResponse> carResponses = carsPage.getContent().stream()
                    .map(carResponseMapper::domainToResponse)
                    .collect(Collectors.toList());

            // Crear respuesta paginada
            PagedResponse<CarResponse> pagedResponse = PagedResponse.<CarResponse>builder()
                    .content(carResponses)
                    .pageInfo(PagedResponse.PageInfo.builder()
                            .page(carsPage.getNumber())
                            .size(carsPage.getSize())
                            .totalPages(carsPage.getTotalPages())
                            .totalElements(carsPage.getTotalElements())
                            .first(carsPage.isFirst())
                            .last(carsPage.isLast())
                            .hasNext(carsPage.hasNext())
                            .hasPrevious(carsPage.hasPrevious())
                            .sort(PagedResponse.SortInfo.builder()
                                    .sorted(carsPage.getSort().isSorted())
                                    .sortBy(carsPage.getSort().isSorted() ?
                                            carsPage.getSort().iterator().next().getProperty() : null)
                                    .direction(carsPage.getSort().isSorted() ?
                                            carsPage.getSort().iterator().next().getDirection().name() : null)
                                    .build())
                            .build())
                    .build();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", String.format("Se encontraron %d autos", carsPage.getTotalElements()));
            response.put("data", pagedResponse);

            logger.info("Búsqueda paginada completada: {} autos encontrados", carsPage.getTotalElements());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error en búsqueda paginada: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Método helper para extraer el ID del usuario desde el request
     */
    private Long extractUserIdFromRequest(HttpServletRequest request) {
        Object userIdAttribute = request.getAttribute("userId");

        if (userIdAttribute instanceof Long) {
            return (Long) userIdAttribute;
        } else if (userIdAttribute instanceof Integer) {
            return ((Integer) userIdAttribute).longValue();
        } else {
            throw new IllegalArgumentException("ID de usuario no encontrado en el request");
        }
    }

    /**
     * Construye una cadena descriptiva de los filtros aplicados
     */
    private String buildAppliedFiltersString(CarSearchRequest searchRequest) {
        if (!searchRequest.hasAnyFilter()) {
            return "Sin filtros";
        }

        StringBuilder filters = new StringBuilder();

        if (searchRequest.hasSearchTerm()) {
            filters.append("Término: '").append(searchRequest.getSearchTerm()).append("'");
        }

        if (searchRequest.hasBrandFilter()) {
            if (!filters.isEmpty()) filters.append(", ");
            filters.append("Marca: ").append(searchRequest.getBrand());
        }

        if (searchRequest.hasModelFilter()) {
            if (!filters.isEmpty()) filters.append(", ");
            filters.append("Modelo: ").append(searchRequest.getModel());
        }

        if (searchRequest.hasYearFilter()) {
            if (!filters.isEmpty()) filters.append(", ");
            filters.append("Año: ").append(searchRequest.getYear());
        }

        if (searchRequest.hasYearRangeFilter()) {
            if (!filters.isEmpty()) filters.append(", ");
            filters.append("Años: ").append(searchRequest.getMinYear()).append("-").append(searchRequest.getMaxYear());
        }

        if (searchRequest.hasColorFilter()) {
            if (!filters.isEmpty()) filters.append(", ");
            filters.append("Color: ").append(searchRequest.getColor());
        }

        if (searchRequest.getIsVintage() != null) {
            if (!filters.isEmpty()) filters.append(", ");
            filters.append("Vintage: ").append(searchRequest.getIsVintage());
        }

        if (searchRequest.getIsNew() != null) {
            if (!filters.isEmpty()) filters.append(", ");
            filters.append("Nuevo: ").append(searchRequest.getIsNew());
        }

        return filters.toString();
    }
}