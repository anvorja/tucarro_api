package com.anborja.tucarro.infrastructure.driven.jpa.sqlserver.exception;

public class NoDataFoundException extends RuntimeException {

    private final String queryType;
    private final String criteria;

    public NoDataFoundException() {
        super("No se encontraron datos");
        this.queryType = "Unknown";
        this.criteria = null;
    }

    public NoDataFoundException(String message) {
        super(message);
        this.queryType = "Custom";
        this.criteria = null;
    }

    public NoDataFoundException(String queryType, String criteria) {
        super("No se encontraron datos para la consulta: " + queryType + " con criterios: " + criteria);
        this.queryType = queryType;
        this.criteria = criteria;
    }

    public NoDataFoundException(String message, Throwable cause) {
        super(message, cause);
        this.queryType = "Unknown";
        this.criteria = null;
    }

    public NoDataFoundException(String queryType, String criteria, Throwable cause) {
        super("No se encontraron datos para la consulta: " + queryType + " con criterios: " + criteria, cause);
        this.queryType = queryType;
        this.criteria = criteria;
    }

    // Constructores específicos para consultas comunes
    public static NoDataFoundException forUserSearch(String searchTerm) {
        return new NoDataFoundException("UserSearch", "término: " + searchTerm);
    }

    public static NoDataFoundException forCarSearch(String searchTerm) {
        return new NoDataFoundException("CarSearch", "término: " + searchTerm);
    }

    public static NoDataFoundException forCarsByUser(Long userId) {
        return new NoDataFoundException("CarsByUser", "usuario ID: " + userId);
    }

    public static NoDataFoundException forCarsByBrand(String brand) {
        return new NoDataFoundException("CarsByBrand", "marca: " + brand);
    }

    public static NoDataFoundException forCarsByModel(String model) {
        return new NoDataFoundException("CarsByModel", "modelo: " + model);
    }

    public static NoDataFoundException forCarsByYear(Integer year) {
        return new NoDataFoundException("CarsByYear", "año: " + year);
    }

    public static NoDataFoundException forCarsByYearRange(Integer minYear, Integer maxYear) {
        return new NoDataFoundException("CarsByYearRange", "años: " + minYear + " - " + maxYear);
    }

    public static NoDataFoundException forCarsByColor(String color) {
        return new NoDataFoundException("CarsByColor", "color: " + color);
    }

    public static NoDataFoundException forCarsByPlate(String plateNumber) {
        return new NoDataFoundException("CarsByPlate", "placa: " + plateNumber);
    }

    public static NoDataFoundException forVintageCars(Long userId) {
        return new NoDataFoundException("VintageCars", "usuario ID: " + userId);
    }

    public static NoDataFoundException forNewCars(Long userId) {
        return new NoDataFoundException("NewCars", "usuario ID: " + userId);
    }

    public static NoDataFoundException forUsersWithCars() {
        return new NoDataFoundException("UsersWithCars", "usuarios que tienen autos");
    }

    public static NoDataFoundException forUsersWithoutCars() {
        return new NoDataFoundException("UsersWithoutCars", "usuarios sin autos");
    }

    // Getters
    public String getQueryType() {
        return queryType;
    }

    public String getCriteria() {
        return criteria;
    }

    @Override
    public String toString() {
        return "NoDataFoundException{" +
                "queryType='" + queryType + '\'' +
                ", criteria='" + criteria + '\'' +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}