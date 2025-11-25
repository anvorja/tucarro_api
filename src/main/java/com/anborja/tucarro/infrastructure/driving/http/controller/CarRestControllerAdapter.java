package com.anborja.tucarro.infrastructure.driving.http.controller;

import com.anborja.tucarro.domain.api.ICarSearchServicePort;
import com.anborja.tucarro.domain.api.ICarServicePort;
import com.anborja.tucarro.domain.model.Car;
import com.anborja.tucarro.infrastructure.documentation.ApiDocumentation;
import com.anborja.tucarro.infrastructure.driving.http.dto.request.CreateCarRequest;
import com.anborja.tucarro.infrastructure.driving.http.dto.request.UpdateCarRequest;
import com.anborja.tucarro.infrastructure.driving.http.dto.response.CarResponse;
import com.anborja.tucarro.infrastructure.driving.http.mapper.ICarRequestMapper;
import com.anborja.tucarro.infrastructure.driving.http.mapper.ICarResponseMapper;
import com.anborja.tucarro.shared.constant.AppConstants;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(AppConstants.API_VERSION + AppConstants.CARS_ENDPOINT)
@Tag(name = "🚗 Gestión de Autos", description = "CRUD completo de autos para usuarios autenticados")
public class CarRestControllerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(CarRestControllerAdapter.class);

    private final ICarServicePort carServicePort;
    private final ICarSearchServicePort carSearchServicePort;
    private final ICarRequestMapper carRequestMapper;
    private final ICarResponseMapper carResponseMapper;

    public CarRestControllerAdapter(ICarServicePort carServicePort,
                                    ICarSearchServicePort carSearchServicePort,
                                    ICarRequestMapper carRequestMapper,
                                    ICarResponseMapper carResponseMapper) {
        this.carServicePort = carServicePort;
        this.carSearchServicePort = carSearchServicePort;
        this.carRequestMapper = carRequestMapper;
        this.carResponseMapper = carResponseMapper;
    }

    /**
     * Crea un nuevo auto para el usuario autenticado
     */
    @PostMapping
    @ApiDocumentation.CreateCarDocumentation
    public ResponseEntity<Map<String, Object>> createCar(@Valid @RequestBody CreateCarRequest createCarRequest,
                                                         HttpServletRequest request) {
        logger.info("Creando auto para usuario");

        try {
            Long userId = extractUserIdFromRequest(request);

            // Convertir DTO a modelo del dominio
            Car car = carRequestMapper.createRequestToDomainWithUserId(createCarRequest, userId);

            // Crear auto
            Car createdCar = carServicePort.createCar(car, userId);

            // Convertir a DTO de respuesta
            CarResponse carResponse = carResponseMapper.domainToResponse(createdCar);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", AppConstants.CREATED_MESSAGE);
            response.put("data", carResponse);

            logger.info("Auto creado exitosamente con ID: {} para usuario ID: {}", createdCar.getId(), userId);
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            logger.error("Error al crear auto: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Obtiene todos los autos del usuario autenticado
     */
    @GetMapping
    @ApiDocumentation.GetUserCarsDocumentation
    public ResponseEntity<Map<String, Object>> getUserCars(HttpServletRequest request,
                                                           @RequestParam(defaultValue = "created") String sortBy,
                                                           @RequestParam(defaultValue = "desc") String sortOrder) {
        logger.info("Obteniendo autos del usuario");

        try {
            Long userId = extractUserIdFromRequest(request);

            List<Car> cars;

            // Aplicar ordenamiento según parámetros
            switch (sortBy.toLowerCase()) {
                case "year":
                    if ("asc".equalsIgnoreCase(sortOrder)) {
                        cars = carSearchServicePort.getCarsOrderedByYearAsc(userId);
                    } else {
                        cars = carSearchServicePort.getCarsOrderedByYearDesc(userId);
                    }
                    break;
                default:
                    cars = carServicePort.getCarsByUserId(userId);
            }

            // Convertir a DTOs
            List<CarResponse> carResponses = carResponseMapper.domainListToResponseList(cars);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Autos obtenidos exitosamente");
            response.put("data", carResponses);
            response.put("total", carResponses.size());

            logger.info("Obtenidos {} autos para usuario ID: {}", carResponses.size(), userId);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error al obtener autos: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Obtiene un auto específico por ID
     */
    @GetMapping("/{carId}")
    @ApiDocumentation.GetCarByIdDocumentation
    public ResponseEntity<Map<String, Object>> getCarById(@PathVariable Long carId,
                                                          HttpServletRequest request) {
        logger.info("Obteniendo auto con ID: {}", carId);

        try {
            Long userId = extractUserIdFromRequest(request);

            // Obtener auto
            Car car = carServicePort.getCarById(carId, userId);

            // Convertir a DTO
            CarResponse carResponse = carResponseMapper.domainToResponse(car);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Auto obtenido exitosamente");
            response.put("data", carResponse);

            logger.info("Auto obtenido exitosamente con ID: {}", carId);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error al obtener auto: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Actualiza un auto existente
     */
    @PutMapping("/{carId}")
    public ResponseEntity<Map<String, Object>> updateCar(@PathVariable Long carId,
                                                         @Valid @RequestBody UpdateCarRequest updateCarRequest,
                                                         HttpServletRequest request) {
        logger.info("Actualizando auto con ID: {}", carId);

        try {
            Long userId = extractUserIdFromRequest(request);

            // Convertir DTO a modelo del dominio
            Car carUpdates = carRequestMapper.updateRequestToDomain(updateCarRequest);

            // Actualizar auto
            Car updatedCar = carServicePort.updateCar(carId, carUpdates, userId);

            // Convertir a DTO
            CarResponse carResponse = carResponseMapper.domainToResponse(updatedCar);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", AppConstants.UPDATED_MESSAGE);
            response.put("data", carResponse);

            logger.info("Auto actualizado exitosamente con ID: {}", carId);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error al actualizar auto: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Elimina un auto
     */
    @DeleteMapping("/{carId}")
    public ResponseEntity<Map<String, Object>> deleteCar(@PathVariable Long carId,
                                                         HttpServletRequest request) {
        logger.info("Eliminando auto con ID: {}", carId);

        try {
            Long userId = extractUserIdFromRequest(request);

            // Eliminar auto
            boolean deleted = carServicePort.deleteCar(carId, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", deleted);
            response.put("message", deleted ? AppConstants.DELETED_MESSAGE : "Error al eliminar auto");

            if (deleted) {
                logger.info("Auto eliminado exitosamente con ID: {}", carId);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {
            logger.error("Error al eliminar auto: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Busca autos por término general
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchCars(@RequestParam String q,
                                                          HttpServletRequest request) {
        logger.info("Buscando autos con término: {}", q);

        try {
            Long userId = extractUserIdFromRequest(request);

            // Buscar autos
            List<Car> cars = carSearchServicePort.generalSearch(q, userId);

            // Convertir a DTOs
            List<CarResponse> carResponses = carResponseMapper.domainListToResponseList(cars);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Búsqueda completada");
            response.put("data", carResponses);
            response.put("total", carResponses.size());
            response.put("search_term", q);

            logger.info("Búsqueda completada. Encontrados {} autos", carResponses.size());
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error en búsqueda de autos: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Filtra autos por marca
     */
    @GetMapping("/filter/brand")
    public ResponseEntity<Map<String, Object>> filterByBrand(@RequestParam String brand,
                                                             HttpServletRequest request) {
        logger.info("Filtrando autos por marca: {}", brand);

        try {
            Long userId = extractUserIdFromRequest(request);

            List<Car> cars = carSearchServicePort.searchByBrand(brand, userId);
            List<CarResponse> carResponses = carResponseMapper.domainListToResponseList(cars);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Filtrado por marca completado");
            response.put("data", carResponses);
            response.put("total", carResponses.size());
            response.put("filter", Map.of("type", "brand", "value", brand));

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error al filtrar por marca: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Filtra autos por modelo
     */
    @GetMapping("/filter/model")
    public ResponseEntity<Map<String, Object>> filterByModel(@RequestParam String model,
                                                             HttpServletRequest request) {
        logger.info("Filtrando autos por modelo: {}", model);

        try {
            Long userId = extractUserIdFromRequest(request);

            List<Car> cars = carSearchServicePort.searchByModel(model, userId);
            List<CarResponse> carResponses = carResponseMapper.domainListToResponseList(cars);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Filtrado por modelo completado");
            response.put("data", carResponses);
            response.put("total", carResponses.size());
            response.put("filter", Map.of("type", "model", "value", model));

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error al filtrar por modelo: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Filtra autos por año
     */
    @GetMapping("/filter/year")
    public ResponseEntity<Map<String, Object>> filterByYear(@RequestParam Integer year,
                                                            HttpServletRequest request) {
        logger.info("Filtrando autos por año: {}", year);

        try {
            Long userId = extractUserIdFromRequest(request);

            List<Car> cars = carSearchServicePort.filterByYear(year, userId);
            List<CarResponse> carResponses = carResponseMapper.domainListToResponseList(cars);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Filtrado por año completado");
            response.put("data", carResponses);
            response.put("total", carResponses.size());
            response.put("filter", Map.of("type", "year", "value", year));

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error al filtrar por año: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Filtra autos por rango de años
     */
    @GetMapping("/filter/year-range")
    public ResponseEntity<Map<String, Object>> filterByYearRange(@RequestParam Integer minYear,
                                                                 @RequestParam Integer maxYear,
                                                                 HttpServletRequest request) {
        logger.info("Filtrando autos por rango de años: {} - {}", minYear, maxYear);

        try {
            Long userId = extractUserIdFromRequest(request);

            List<Car> cars = carSearchServicePort.filterByYearRange(minYear, maxYear, userId);
            List<CarResponse> carResponses = carResponseMapper.domainListToResponseList(cars);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Filtrado por rango de años completado");
            response.put("data", carResponses);
            response.put("total", carResponses.size());
            response.put("filter", Map.of("type", "year_range", "min_year", minYear, "max_year", maxYear));

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error al filtrar por rango de años: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Filtra autos por color
     */
    @GetMapping("/filter/color")
    public ResponseEntity<Map<String, Object>> filterByColor(@RequestParam String color,
                                                             HttpServletRequest request) {
        logger.info("Filtrando autos por color: {}", color);

        try {
            Long userId = extractUserIdFromRequest(request);

            List<Car> cars = carSearchServicePort.filterByColor(color, userId);
            List<CarResponse> carResponses = carResponseMapper.domainListToResponseList(cars);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Filtrado por color completado");
            response.put("data", carResponses);
            response.put("total", carResponses.size());
            response.put("filter", Map.of("type", "color", "value", color));

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error al filtrar por color: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Obtiene autos vintage del usuario
     */
    @GetMapping("/vintage")
    public ResponseEntity<Map<String, Object>> getVintageCars(HttpServletRequest request) {
        logger.info("Obteniendo autos vintage");

        try {
            Long userId = extractUserIdFromRequest(request);

            List<Car> cars = carSearchServicePort.getVintageCars(userId);
            List<CarResponse> carResponses = carResponseMapper.domainListToResponseList(cars);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Autos vintage obtenidos");
            response.put("data", carResponses);
            response.put("total", carResponses.size());
            response.put("category", "vintage");

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error al obtener autos vintage: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Obtiene autos nuevos del usuario
     */
    @GetMapping("/new")
    public ResponseEntity<Map<String, Object>> getNewCars(HttpServletRequest request) {
        logger.info("Obteniendo autos nuevos");

        try {
            Long userId = extractUserIdFromRequest(request);

            List<Car> cars = carSearchServicePort.getNewCars(userId);
            List<CarResponse> carResponses = carResponseMapper.domainListToResponseList(cars);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Autos nuevos obtenidos");
            response.put("data", carResponses);
            response.put("total", carResponses.size());
            response.put("category", "new");

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error al obtener autos nuevos: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Obtiene estadísticas de los autos del usuario
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getCarStats(HttpServletRequest request) {
        logger.info("Obteniendo estadísticas de autos");

        try {
            Long userId = extractUserIdFromRequest(request);

            ICarServicePort.CarStats carStats = carServicePort.getCarStatsByUserId(userId);

            Map<String, Object> statsData = new HashMap<>();
            statsData.put("user_id", carStats.getUserId());
            statsData.put("total_cars", carStats.getTotalCars());
            statsData.put("vintage_car_count", carStats.getVintageCarCount());
            statsData.put("new_car_count", carStats.getNewCarCount());
            statsData.put("most_common_brand", carStats.getMostCommonBrand());
            statsData.put("newest_car_year", carStats.getNewestCarYear());
            statsData.put("oldest_car_year", carStats.getOldestCarYear());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Estadísticas obtenidas exitosamente");
            response.put("data", statsData);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error al obtener estadísticas: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Verifica si una placa está disponible
     */
    @GetMapping("/plate-available")
    public ResponseEntity<Map<String, Object>> checkPlateAvailability(@RequestParam String plateNumber) {
        logger.info("Verificando disponibilidad de placa: {}", plateNumber);

        try {
            boolean available = carServicePort.isPlateNumberAvailable(plateNumber);

            Map<String, Object> response = new HashMap<>();
            response.put("plate_number", plateNumber);
            response.put("available", available);
            response.put("message", available ? "Placa disponible" : "Placa ya está en uso");

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error al verificar placa: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Busca un auto por placa (del usuario autenticado)
     */
    @GetMapping("/plate/{plateNumber}")
    public ResponseEntity<Map<String, Object>> getCarByPlate(@PathVariable String plateNumber,
                                                             HttpServletRequest request) {
        logger.info("Buscando auto por placa: {}", plateNumber);

        try {
            Long userId = extractUserIdFromRequest(request);

            List<Car> cars = carSearchServicePort.searchByPlateNumber(plateNumber, userId);
            List<CarResponse> carResponses = carResponseMapper.domainListToResponseList(cars);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Búsqueda por placa completada");
            response.put("data", carResponses);
            response.put("total", carResponses.size());
            response.put("plate_number", plateNumber);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error al buscar por placa: {}", e.getMessage());
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
}