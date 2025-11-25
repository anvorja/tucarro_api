package com.anborja.tucarro.domain.api;

import com.anborja.tucarro.domain.model.Car;
import com.anborja.tucarro.domain.model.CarSearchCriteria;
import com.anborja.tucarro.infrastructure.driving.http.dto.request.CarSearchRequest;
import com.anborja.tucarro.domain.exception.CarNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ICarSearchServicePort {


    /**
     * Busca autos del usuario por número de placa
     *
     * @param plateNumber el número de placa a buscar
     * @param userId el ID del usuario
     * @return lista de autos que coinciden con la placa
     */
    List<Car> searchByPlateNumber(String plateNumber, Long userId);

    /**
     * Busca autos del usuario por modelo
     *
     * @param model el modelo a buscar
     * @param userId el ID del usuario
     * @return lista de autos que coinciden con el modelo
     */
    List<Car> searchByModel(String model, Long userId);

    /**
     * Busca autos del usuario por marca
     *
     * @param brand la marca a buscar
     * @param userId el ID del usuario
     * @return lista de autos que coinciden con la marca
     */
    List<Car> searchByBrand(String brand, Long userId);

    /**
     * Filtra autos del usuario por año específico
     *
     * @param year el año a filtrar
     * @param userId el ID del usuario
     * @return lista de autos del año especificado
     */
    List<Car> filterByYear(Integer year, Long userId);

    /**
     * Filtra autos del usuario por rango de años
     *
     * @param minYear el año mínimo (inclusive)
     * @param maxYear el año máximo (inclusive)
     * @param userId el ID del usuario
     * @return lista de autos en el rango de años
     */
    List<Car> filterByYearRange(Integer minYear, Integer maxYear, Long userId);

    /**
     * Filtra autos del usuario por color
     *
     * @param color el color a filtrar
     * @param userId el ID del usuario
     * @return lista de autos del color especificado
     */
    List<Car> filterByColor(String color, Long userId);

    /**
     * Búsqueda general en marca y modelo
     *
     * @param searchTerm el término de búsqueda
     * @param userId el ID del usuario
     * @return lista de autos que coinciden en marca o modelo
     */
    List<Car> generalSearch(String searchTerm, Long userId);

    /**
     * Búsqueda avanzada con múltiples criterios
     *
     * @param searchCriteria los criterios de búsqueda
     * @param userId el ID del usuario
     * @return lista de autos que coinciden con los criterios
     */
    List<Car> advancedSearch(SearchCriteria searchCriteria, Long userId);

    /**
     * Obtiene autos vintage del usuario (más de 25 años)
     *
     * @param userId el ID del usuario
     * @return lista de autos vintage
     */
    List<Car> getVintageCars(Long userId);

    /**
     * Obtiene autos nuevos del usuario (menos de 3 años)
     *
     * @param userId el ID del usuario
     * @return lista de autos nuevos
     */
    List<Car> getNewCars(Long userId);

    /**
     * Obtiene autos ordenados por año (más nuevo primero)
     *
     * @param userId el ID del usuario
     * @return lista de autos ordenados por año descendente
     */
    List<Car> getCarsOrderedByYearDesc(Long userId);

    /**
     * Obtiene autos ordenados por año (más viejo primero)
     *
     * @param userId el ID del usuario
     * @return lista de autos ordenados por año ascendente
     */
    List<Car> getCarsOrderedByYearAsc(Long userId);

    /**
     *  Búsqueda unificada usando DTO del controlador
     * Este método conecta con el nuevo controlador REST
     *
     * @param userId el ID del usuario
     * @param searchRequest los criterios de búsqueda del DTO
     * @return lista de autos que cumplen los criterios
     */
    List<Car> searchCars(Long userId, CarSearchRequest searchRequest);

    /**
     * Buscar auto específico por placa (para endpoint directo)
     *
     * @param userId el ID del usuario
     * @param plateNumber el número de placa
     * @return el auto con esa placa si pertenece al usuario
     * @throws CarNotFoundException si no se encuentra
     */
    Car findByPlate(Long userId, String plateNumber);

    /**
     * Verificar si una placa está disponible (útil para el frontend)
     *
     * @param plateNumber el número de placa a verificar
     * @return true si la placa está disponible, false si ya existe
     */
    boolean isPlateAvailable(String plateNumber);

    /**
     * Verificar si una placa está disponible para un usuario específico
     * (permite actualizar sin conflicto)
     *
     * @param plateNumber el número de placa a verificar
     * @param userId el ID del usuario
     * @return true si la placa está disponible para ese usuario
     */
    boolean isPlateAvailableForUser(String plateNumber, Long userId);

    /**
     * Obtener autos que tienen foto
     *
     * @param userId el ID del usuario
     * @return lista de autos con foto
     */
    List<Car> getCarsWithPhoto(Long userId);

    /**
     * Obtener autos que no tienen foto
     *
     * @param userId el ID del usuario
     * @return lista de autos sin foto
     */
    List<Car> getCarsWithoutPhoto(Long userId);

    /**
     * Obtener autos ordenados por cualquier campo
     *
     * @param userId el ID del usuario
     * @param sortBy campo por el cual ordenar
     * @param ascending true para orden ascendente, false para descendente
     * @return lista de autos ordenados
     */
    List<Car> getSortedCars(Long userId, String sortBy, boolean ascending);

    /**
     * Obtener las marcas más comunes del usuario
     *
     * @param userId el ID del usuario
     * @return lista de marcas ordenadas por frecuencia
     */
    List<String> getMostCommonBrands(Long userId);

    /**
     * Obtener estadísticas de años de los autos del usuario
     *
     * @param userId el ID del usuario
     * @return objeto con estadísticas de años
     */
    YearStatistics getYearStatistics(Long userId);

    // ============================================================================
    // CLASES AUXILIARES
    // ============================================================================

    /**
     * Clase existente para criterios de búsqueda avanzada (MANTENER)
     */
    class SearchCriteria {
        private String brand;
        private String model;
        private Integer year;
        private Integer minYear;
        private Integer maxYear;
        private String color;
        private String plateNumber;
        private String generalSearchTerm;
        private SortOrder sortOrder;

        public SearchCriteria() {}

        // Getters y Setters (mantener todos los existentes)
        public String getBrand() { return brand; }
        public void setBrand(String brand) { this.brand = brand; }

        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }

        public Integer getYear() { return year; }
        public void setYear(Integer year) { this.year = year; }

        public Integer getMinYear() { return minYear; }
        public void setMinYear(Integer minYear) { this.minYear = minYear; }

        public Integer getMaxYear() { return maxYear; }
        public void setMaxYear(Integer maxYear) { this.maxYear = maxYear; }

        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }

        public String getPlateNumber() { return plateNumber; }
        public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }

        public String getGeneralSearchTerm() { return generalSearchTerm; }
        public void setGeneralSearchTerm(String generalSearchTerm) { this.generalSearchTerm = generalSearchTerm; }

        public SortOrder getSortOrder() { return sortOrder; }
        public void setSortOrder(SortOrder sortOrder) { this.sortOrder = sortOrder; }

        // Builder pattern (mantener todos los existentes)
        public SearchCriteria withBrand(String brand) {
            this.brand = brand;
            return this;
        }

        public SearchCriteria withModel(String model) {
            this.model = model;
            return this;
        }

        public SearchCriteria withYear(Integer year) {
            this.year = year;
            return this;
        }

        public SearchCriteria withYearRange(Integer minYear, Integer maxYear) {
            this.minYear = minYear;
            this.maxYear = maxYear;
            return this;
        }

        public SearchCriteria withColor(String color) {
            this.color = color;
            return this;
        }

        public SearchCriteria withPlateNumber(String plateNumber) {
            this.plateNumber = plateNumber;
            return this;
        }

        public SearchCriteria withGeneralSearch(String searchTerm) {
            this.generalSearchTerm = searchTerm;
            return this;
        }

        public SearchCriteria withSortOrder(SortOrder sortOrder) {
            this.sortOrder = sortOrder;
            return this;
        }
    }

    /**
     * Enum existente para ordenamiento (MANTENER)
     */
    enum SortOrder {
        YEAR_ASC,
        YEAR_DESC,
        BRAND_ASC,
        BRAND_DESC,
        MODEL_ASC,
        MODEL_DESC,
        CREATED_ASC,
        CREATED_DESC
    }

    /**
     * NUEVA: Clase para estadísticas de años
     */
    class YearStatistics {
        private final Integer minYear;
        private final Integer maxYear;
        private final Double averageYear;
        private final Integer totalCars;

        public YearStatistics(Integer minYear, Integer maxYear, Double averageYear, Integer totalCars) {
            this.minYear = minYear;
            this.maxYear = maxYear;
            this.averageYear = averageYear;
            this.totalCars = totalCars;
        }

        public Integer getMinYear() { return minYear; }
        public Integer getMaxYear() { return maxYear; }
        public Double getAverageYear() { return averageYear; }
        public Integer getTotalCars() { return totalCars; }

        public Integer getYearRange() {
            return (minYear != null && maxYear != null) ? maxYear - minYear : 0;
        }
    }

    /**
     * NUEVO: Búsqueda paginada unificada
     *
     * @param userId el ID del usuario
     * @param searchRequest los criterios de búsqueda del DTO
     * @param page número de página (base 0)
     * @param size tamaño de página
     * @param sortBy campo por el cual ordenar (opcional)
     * @param sortDirection dirección del ordenamiento (asc/desc)
     * @return página de autos que cumplen los criterios
     */
    Page<Car> searchCarsPaginated(Long userId, CarSearchRequest searchRequest,
                                  int page, int size, String sortBy, String sortDirection);
}