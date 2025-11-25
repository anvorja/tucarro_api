package com.anborja.tucarro.infrastructure.driving.http.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO de respuesta para búsquedas de autos con información de paginación y estadísticas
 */
public class CarSearchResponse {

    @JsonProperty("cars")
    private List<CarResponse> cars;

    @JsonProperty("search_metadata")
    private SearchMetadata searchMetadata;

    @JsonProperty("search_statistics")
    private SearchStatistics searchStatistics;

    // Constructor vacío
    public CarSearchResponse() {
    }

    // Constructor completo
    public CarSearchResponse(List<CarResponse> cars, SearchMetadata searchMetadata, SearchStatistics searchStatistics) {
        this.cars = cars;
        this.searchMetadata = searchMetadata;
        this.searchStatistics = searchStatistics;
    }

    // Constructor básico
    public CarSearchResponse(List<CarResponse> cars, int totalResults, String appliedFilters) {
        this.cars = cars;
        this.searchMetadata = new SearchMetadata(totalResults, appliedFilters);
        this.searchStatistics = SearchStatistics.fromCars(cars);
    }

    // Getters y Setters
    public List<CarResponse> getCars() {
        return cars;
    }

    public void setCars(List<CarResponse> cars) {
        this.cars = cars;
    }

    public SearchMetadata getSearchMetadata() {
        return searchMetadata;
    }

    public void setSearchMetadata(SearchMetadata searchMetadata) {
        this.searchMetadata = searchMetadata;
    }

    public SearchStatistics getSearchStatistics() {
        return searchStatistics;
    }

    public void setSearchStatistics(SearchStatistics searchStatistics) {
        this.searchStatistics = searchStatistics;
    }

    /**
     * Metadatos de la búsqueda
     */
    public static class SearchMetadata {
        @JsonProperty("total_results")
        private Integer totalResults;

        @JsonProperty("applied_filters")
        private String appliedFilters;

        @JsonProperty("sort_by")
        private String sortBy;

        @JsonProperty("sort_direction")
        private String sortDirection;

        @JsonProperty("search_term")
        private String searchTerm;

        public SearchMetadata() {
        }

        public SearchMetadata(Integer totalResults, String appliedFilters) {
            this.totalResults = totalResults;
            this.appliedFilters = appliedFilters;
        }

        public SearchMetadata(Integer totalResults, String appliedFilters, String sortBy, String sortDirection, String searchTerm) {
            this.totalResults = totalResults;
            this.appliedFilters = appliedFilters;
            this.sortBy = sortBy;
            this.sortDirection = sortDirection;
            this.searchTerm = searchTerm;
        }

        // Getters y Setters
        public Integer getTotalResults() {
            return totalResults;
        }

        public void setTotalResults(Integer totalResults) {
            this.totalResults = totalResults;
        }

        public String getAppliedFilters() {
            return appliedFilters;
        }

        public void setAppliedFilters(String appliedFilters) {
            this.appliedFilters = appliedFilters;
        }

        public String getSortBy() {
            return sortBy;
        }

        public void setSortBy(String sortBy) {
            this.sortBy = sortBy;
        }

        public String getSortDirection() {
            return sortDirection;
        }

        public void setSortDirection(String sortDirection) {
            this.sortDirection = sortDirection;
        }

        public String getSearchTerm() {
            return searchTerm;
        }

        public void setSearchTerm(String searchTerm) {
            this.searchTerm = searchTerm;
        }
    }

    /**
     * Estadísticas de los resultados de búsqueda
     */
    public static class SearchStatistics {
        @JsonProperty("total_count")
        private Integer totalCount;

        @JsonProperty("vintage_count")
        private Integer vintageCount;

        @JsonProperty("new_count")
        private Integer newCount;

        @JsonProperty("with_photo_count")
        private Integer withPhotoCount;

        @JsonProperty("most_common_brand")
        private String mostCommonBrand;

        @JsonProperty("year_range")
        private YearRange yearRange;

        @JsonProperty("brand_distribution")
        private List<BrandCount> brandDistribution;

        public SearchStatistics() {
        }

        public SearchStatistics(Integer totalCount, Integer vintageCount, Integer newCount,
                                Integer withPhotoCount, String mostCommonBrand, YearRange yearRange) {
            this.totalCount = totalCount;
            this.vintageCount = vintageCount;
            this.newCount = newCount;
            this.withPhotoCount = withPhotoCount;
            this.mostCommonBrand = mostCommonBrand;
            this.yearRange = yearRange;
        }

        // Factory method para crear estadísticas desde lista de autos
        public static SearchStatistics fromCars(List<CarResponse> cars) {
            if (cars == null || cars.isEmpty()) {
                return new SearchStatistics(0, 0, 0, 0, null, null);
            }

            int totalCount = cars.size();
            int vintageCount = 0;
            int newCount = 0;
            int withPhotoCount = 0;
            Integer minYear = null;
            Integer maxYear = null;

            // Conteo de marcas para encontrar la más común
            java.util.Map<String, Integer> brandCounts = new java.util.HashMap<>();

            for (CarResponse car : cars) {
                // Conteo vintage/new
                if (Boolean.TRUE.equals(car.getIsVintage())) {
                    vintageCount++;
                }
                if (Boolean.TRUE.equals(car.getIsNew())) {
                    newCount++;
                }

                // Conteo con foto
                if (car.getPhotoUrl() != null && !car.getPhotoUrl().trim().isEmpty()) {
                    withPhotoCount++;
                }

                // Rango de años
                if (car.getYear() != null) {
                    if (minYear == null || car.getYear() < minYear) {
                        minYear = car.getYear();
                    }
                    if (maxYear == null || car.getYear() > maxYear) {
                        maxYear = car.getYear();
                    }
                }

                // Conteo de marcas
                if (car.getBrand() != null) {
                    brandCounts.put(car.getBrand(), brandCounts.getOrDefault(car.getBrand(), 0) + 1);
                }
            }

            // Encontrar marca más común
            String mostCommonBrand = brandCounts.entrySet().stream()
                    .max(java.util.Map.Entry.comparingByValue())
                    .map(java.util.Map.Entry::getKey)
                    .orElse(null);

            YearRange yearRange = (minYear != null && maxYear != null)
                    ? new YearRange(minYear, maxYear)
                    : null;

            return new SearchStatistics(totalCount, vintageCount, newCount, withPhotoCount, mostCommonBrand, yearRange);
        }

        // Getters y Setters
        public Integer getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(Integer totalCount) {
            this.totalCount = totalCount;
        }

        public Integer getVintageCount() {
            return vintageCount;
        }

        public void setVintageCount(Integer vintageCount) {
            this.vintageCount = vintageCount;
        }

        public Integer getNewCount() {
            return newCount;
        }

        public void setNewCount(Integer newCount) {
            this.newCount = newCount;
        }

        public Integer getWithPhotoCount() {
            return withPhotoCount;
        }

        public void setWithPhotoCount(Integer withPhotoCount) {
            this.withPhotoCount = withPhotoCount;
        }

        public String getMostCommonBrand() {
            return mostCommonBrand;
        }

        public void setMostCommonBrand(String mostCommonBrand) {
            this.mostCommonBrand = mostCommonBrand;
        }

        public YearRange getYearRange() {
            return yearRange;
        }

        public void setYearRange(YearRange yearRange) {
            this.yearRange = yearRange;
        }

        public List<BrandCount> getBrandDistribution() {
            return brandDistribution;
        }

        public void setBrandDistribution(List<BrandCount> brandDistribution) {
            this.brandDistribution = brandDistribution;
        }
    }

    /**
     * Rango de años en los resultados
     */
    public static class YearRange {
        @JsonProperty("min_year")
        private Integer minYear;

        @JsonProperty("max_year")
        private Integer maxYear;

        public YearRange() {
        }

        public YearRange(Integer minYear, Integer maxYear) {
            this.minYear = minYear;
            this.maxYear = maxYear;
        }

        public Integer getMinYear() {
            return minYear;
        }

        public void setMinYear(Integer minYear) {
            this.minYear = minYear;
        }

        public Integer getMaxYear() {
            return maxYear;
        }

        public void setMaxYear(Integer maxYear) {
            this.maxYear = maxYear;
        }
    }

    /**
     * Distribución de marcas
     */
    public static class BrandCount {
        private String brand;
        private Integer count;
        private Double percentage;

        public BrandCount() {
        }

        public BrandCount(String brand, Integer count, Double percentage) {
            this.brand = brand;
            this.count = count;
            this.percentage = percentage;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public Double getPercentage() {
            return percentage;
        }

        public void setPercentage(Double percentage) {
            this.percentage = percentage;
        }
    }

    @Override
    public String toString() {
        return "CarSearchResponse{" +
                "carsCount=" + (cars != null ? cars.size() : 0) +
                ", searchMetadata=" + searchMetadata +
                ", searchStatistics=" + searchStatistics +
                '}';
    }
}