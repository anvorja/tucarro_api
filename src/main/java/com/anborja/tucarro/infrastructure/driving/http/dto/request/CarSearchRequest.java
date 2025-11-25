package com.anborja.tucarro.infrastructure.driving.http.dto.request;

import jakarta.validation.constraints.*;

/**
 * DTO para realizar búsquedas y filtros de autos
 */
public class CarSearchRequest {

    // Búsqueda general (búsqueda en marca, modelo, color)
    @Size(max = 100, message = "El término de búsqueda no puede exceder 100 caracteres")
    private String searchTerm;

    // Filtros específicos
    @Size(max = 30, message = "La marca no puede exceder 30 caracteres")
    private String brand;

    @Size(max = 50, message = "El modelo no puede exceder 50 caracteres")
    private String model;

    @Min(value = 1950, message = "El año mínimo debe ser 1950 o posterior")
    @Max(value = 2030, message = "El año máximo no puede ser superior a 2030")
    private Integer year;

    @Min(value = 1950, message = "El año mínimo debe ser 1950 o posterior")
    @Max(value = 2030, message = "El año mínimo no puede ser superior a 2030")
    private Integer minYear;

    @Min(value = 1950, message = "El año máximo debe ser 1950 o posterior")
    @Max(value = 2030, message = "El año máximo no puede ser superior a 2030")
    private Integer maxYear;

    @Size(max = 20, message = "El color no puede exceder 20 caracteres")
    private String color;

    @Size(max = 10, message = "La placa no puede exceder 10 caracteres")
    private String plateNumber;

    // Parámetros de ordenamiento
    @Pattern(regexp = "brand|model|year|color|createdAt|updatedAt",
            message = "Campo de ordenamiento debe ser: brand, model, year, color, createdAt, updatedAt")
    private String sortBy = "createdAt";

    @Pattern(regexp = "asc|desc|ASC|DESC",
            message = "Dirección de ordenamiento debe ser: asc o desc")
    private String sortDirection = "desc";

    // Filtros adicionales
    private Boolean isVintage; // Autos de más de 25 años
    private Boolean isNew;     // Autos de 3 años o menos
    private Boolean hasPhoto;  // Autos con foto

    // Constructor vacío
    public CarSearchRequest() {
    }

    // Constructor para búsqueda simple
    public CarSearchRequest(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    // Constructor para filtros básicos
    public CarSearchRequest(String brand, String model, Integer year, String color) {
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.color = color;
    }

    // Getters y Setters
    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
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

    public Boolean getIsVintage() {
        return isVintage;
    }

    public void setIsVintage(Boolean isVintage) {
        this.isVintage = isVintage;
    }

    public Boolean getIsNew() {
        return isNew;
    }

    public void setIsNew(Boolean isNew) {
        this.isNew = isNew;
    }

    public Boolean getHasPhoto() {
        return hasPhoto;
    }

    public void setHasPhoto(Boolean hasPhoto) {
        this.hasPhoto = hasPhoto;
    }

    // Métodos de utilidad
    public boolean hasSearchTerm() {
        return searchTerm != null && !searchTerm.trim().isEmpty();
    }

    public boolean hasBrandFilter() {
        return brand != null && !brand.trim().isEmpty();
    }

    public boolean hasModelFilter() {
        return model != null && !model.trim().isEmpty();
    }

    public boolean hasYearFilter() {
        return year != null;
    }

    public boolean hasYearRangeFilter() {
        return minYear != null || maxYear != null;
    }

    public boolean hasColorFilter() {
        return color != null && !color.trim().isEmpty();
    }

    public boolean hasPlateFilter() {
        return plateNumber != null && !plateNumber.trim().isEmpty();
    }

    public boolean hasAnyFilter() {
        return hasSearchTerm() || hasBrandFilter() || hasModelFilter() ||
                hasYearFilter() || hasYearRangeFilter() || hasColorFilter() ||
                hasPlateFilter() || isVintage != null || isNew != null || hasPhoto != null;
    }

    public boolean isSortingDescending() {
        return "desc".equalsIgnoreCase(sortDirection) || "DESC".equals(sortDirection);
    }

    @Override
    public String toString() {
        return "CarSearchRequest{" +
                "searchTerm='" + searchTerm + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", minYear=" + minYear +
                ", maxYear=" + maxYear +
                ", color='" + color + '\'' +
                ", plateNumber='" + plateNumber + '\'' +
                ", sortBy='" + sortBy + '\'' +
                ", sortDirection='" + sortDirection + '\'' +
                ", isVintage=" + isVintage +
                ", isNew=" + isNew +
                ", hasPhoto=" + hasPhoto +
                '}';
    }
}