package com.anborja.tucarro.infrastructure.driving.http.controller;

import com.anborja.tucarro.domain.api.ICarServicePort;
import com.anborja.tucarro.domain.model.Car;
import com.anborja.tucarro.shared.constant.AppConstants;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(AppConstants.API_VERSION + AppConstants.CARS_ENDPOINT + "/filter-options")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "🔍 Filtros de Autos", description = "Opciones disponibles para filtrar autos del usuario")
public class CarFilterOptionsController {

    private final ICarServicePort carServicePort;

    /**
     * Obtiene todas las opciones disponibles para filtros
     * GET /api/v1/cars/filter-options
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getFilterOptions(HttpServletRequest request) {
        log.info("Obteniendo opciones de filtros para usuario");

        try {
            Long userId = extractUserIdFromRequest(request);

            // Obtener todos los carros del usuario
            List<Car> allUserCars = carServicePort.getCarsByUserId(userId);

            // Extraer valores únicos
            Set<String> brands = allUserCars.stream()
                    .map(Car::getBrand)
                    .filter(Objects::nonNull)
                    .filter(brand -> !brand.trim().isEmpty())
                    .collect(Collectors.toSet());

            Set<String> models = allUserCars.stream()
                    .map(Car::getModel)
                    .filter(Objects::nonNull)
                    .filter(model -> !model.trim().isEmpty())
                    .collect(Collectors.toSet());

            Set<String> colors = allUserCars.stream()
                    .map(Car::getColor)
                    .filter(Objects::nonNull)
                    .filter(color -> !color.trim().isEmpty())
                    .collect(Collectors.toSet());

            Set<Integer> years = allUserCars.stream()
                    .map(Car::getYear)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // Convertir a listas ordenadas
            List<String> sortedBrands = brands.stream()
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .collect(Collectors.toList());

            List<String> sortedModels = models.stream()
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .collect(Collectors.toList());

            List<String> sortedColors = colors.stream()
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .collect(Collectors.toList());

            List<Integer> sortedYears = years.stream()
                    .sorted(Collections.reverseOrder()) // Años más recientes primero
                    .collect(Collectors.toList());

            // Preparar respuesta
            Map<String, Object> filterOptions = new HashMap<>();
            filterOptions.put("brands", sortedBrands);
            filterOptions.put("models", sortedModels);
            filterOptions.put("colors", sortedColors);
            filterOptions.put("years", sortedYears);

            // Estadísticas adicionales
            Map<String, Object> stats = new HashMap<>();
            stats.put("total_cars", allUserCars.size());
            stats.put("unique_brands", brands.size());
            stats.put("unique_models", models.size());
            stats.put("unique_colors", colors.size());
            stats.put("year_range", years.isEmpty() ? null :
                    Map.of("min", Collections.min(years), "max", Collections.max(years)));

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Opciones de filtros obtenidas exitosamente");
            response.put("data", filterOptions);
            response.put("stats", stats);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error al obtener opciones de filtros: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al obtener opciones de filtros");
            errorResponse.put("error", e.getMessage());

            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene opciones específicas por tipo de filtro
     * GET /api/v1/cars/filter-options/{type}
     */
    @GetMapping("/{type}")
    public ResponseEntity<Map<String, Object>> getSpecificFilterOptions(
            @PathVariable String type,
            HttpServletRequest request) {

        log.info("Obteniendo opciones de filtro específico: {}", type);

        try {
            Long userId = extractUserIdFromRequest(request);
            List<Car> allUserCars = carServicePort.getCarsByUserId(userId);

            List<String> options;

            switch (type.toLowerCase()) {
                case "brands":
                    options = allUserCars.stream()
                            .map(Car::getBrand)
                            .filter(Objects::nonNull)
                            .filter(brand -> !brand.trim().isEmpty())
                            .distinct()
                            .sorted(String.CASE_INSENSITIVE_ORDER)
                            .collect(Collectors.toList());
                    break;

                case "models":
                    options = allUserCars.stream()
                            .map(Car::getModel)
                            .filter(Objects::nonNull)
                            .filter(model -> !model.trim().isEmpty())
                            .distinct()
                            .sorted(String.CASE_INSENSITIVE_ORDER)
                            .collect(Collectors.toList());
                    break;

                case "colors":
                    options = allUserCars.stream()
                            .map(Car::getColor)
                            .filter(Objects::nonNull)
                            .filter(color -> !color.trim().isEmpty())
                            .distinct()
                            .sorted(String.CASE_INSENSITIVE_ORDER)
                            .collect(Collectors.toList());
                    break;

                case "years":
                    options = allUserCars.stream()
                            .map(Car::getYear)
                            .filter(Objects::nonNull)
                            .map(String::valueOf)
                            .distinct()
                            .sorted((a, b) -> Integer.compare(Integer.parseInt(b), Integer.parseInt(a)))
                            .collect(Collectors.toList());
                    break;

                default:
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("success", false);
                    errorResponse.put("message", "Tipo de filtro no válido: " + type);
                    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Opciones de filtro " + type + " obtenidas");
            response.put("data", options);
            response.put("total", options.size());
            response.put("filter_type", type);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error al obtener opciones de filtro {}: {}", type, e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al obtener opciones de filtro " + type);
            errorResponse.put("error", e.getMessage());

            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Extrae el ID del usuario desde el request attribute (establecido por JwtAuthenticationFilter)
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