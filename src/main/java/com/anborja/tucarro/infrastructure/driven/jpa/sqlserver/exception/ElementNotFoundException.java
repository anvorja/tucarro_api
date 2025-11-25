package com.anborja.tucarro.infrastructure.driven.jpa.sqlserver.exception;

public class ElementNotFoundException extends RuntimeException {

    private final String entityType;
    private final Object identifier;

    public ElementNotFoundException(String entityType, Object identifier) {
        super("Elemento no encontrado: " + entityType + " con identificador: " + identifier);
        this.entityType = entityType;
        this.identifier = identifier;
    }

    public ElementNotFoundException(String message) {
        super(message);
        this.entityType = "Unknown";
        this.identifier = null;
    }

    public ElementNotFoundException(String entityType, Object identifier, String customMessage) {
        super(customMessage + " - " + entityType + " con identificador: " + identifier);
        this.entityType = entityType;
        this.identifier = identifier;
    }

    public ElementNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.entityType = "Unknown";
        this.identifier = null;
    }

    public ElementNotFoundException(String entityType, Object identifier, Throwable cause) {
        super("Elemento no encontrado: " + entityType + " con identificador: " + identifier, cause);
        this.entityType = entityType;
        this.identifier = identifier;
    }

    // Constructores específicos para entidades comunes
    public static ElementNotFoundException forUser(Long userId) {
        return new ElementNotFoundException("User", userId);
    }

    public static ElementNotFoundException forUser(String email) {
        return new ElementNotFoundException("User", email, "Usuario no encontrado");
    }

    public static ElementNotFoundException forCar(Long carId) {
        return new ElementNotFoundException("Car", carId);
    }

    public static ElementNotFoundException forCar(String plateNumber) {
        return new ElementNotFoundException("Car", plateNumber, "Auto no encontrado");
    }

    // Getters
    public String getEntityType() {
        return entityType;
    }

    public Object getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return "ElementNotFoundException{" +
                "entityType='" + entityType + '\'' +
                ", identifier=" + identifier +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}