package com.anborja.tucarro.domain.model;

public class CarSearchCriteria {

    private String searchTerm;
    private String brand;
    private String model;
    private Integer year;
    private String color;
    private Integer minYear;
    private Integer maxYear;

    // Constructor vacío
    public CarSearchCriteria() {}

    // Constructor completo
    public CarSearchCriteria(String searchTerm, String brand, String model, Integer year,
                             String color, Integer minYear, Integer maxYear) {
        this.searchTerm = searchTerm;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.color = color;
        this.minYear = minYear;
        this.maxYear = maxYear;
    }

    // Getters
    public String getSearchTerm() { return searchTerm; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public Integer getYear() { return year; }
    public String getColor() { return color; }
    public Integer getMinYear() { return minYear; }
    public Integer getMaxYear() { return maxYear; }

    // Setters
    public void setSearchTerm(String searchTerm) { this.searchTerm = searchTerm; }
    public void setBrand(String brand) { this.brand = brand; }
    public void setModel(String model) { this.model = model; }
    public void setYear(Integer year) { this.year = year; }
    public void setColor(String color) { this.color = color; }
    public void setMinYear(Integer minYear) { this.minYear = minYear; }
    public void setMaxYear(Integer maxYear) { this.maxYear = maxYear; }

    /**
     * Verifica si hay un término de búsqueda general
     */
    public boolean hasSearchTerm() {
        return searchTerm != null && !searchTerm.trim().isEmpty();
    }

    /**
     * Verifica si hay filtros específicos aplicados
     */
    public boolean hasFilters() {
        return (brand != null && !brand.trim().isEmpty()) ||
                (model != null && !model.trim().isEmpty()) ||
                year != null ||
                (color != null && !color.trim().isEmpty()) ||
                minYear != null ||
                maxYear != null;
    }

    /**
     * Verifica si no hay ningún criterio de búsqueda
     */
    public boolean isEmpty() {
        return !hasSearchTerm() && !hasFilters();
    }

    /**
     * Crea una instancia vacía de criterios de búsqueda
     */
    public static CarSearchCriteria empty() {
        return new CarSearchCriteria();
    }

    /**
     * Crea criterios de búsqueda solo con término general
     */
    public static CarSearchCriteria withSearchTerm(String searchTerm) {
        CarSearchCriteria criteria = new CarSearchCriteria();
        criteria.setSearchTerm(searchTerm);
        return criteria;
    }

    /**
     * Crea criterios de búsqueda con filtros específicos
     */
    public static CarSearchCriteria withFilters(String brand, String model, Integer year,
                                                String color, Integer minYear, Integer maxYear) {
        return new CarSearchCriteria(null, brand, model, year, color, minYear, maxYear);
    }
}