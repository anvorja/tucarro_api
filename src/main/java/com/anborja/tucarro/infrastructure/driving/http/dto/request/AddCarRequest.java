package com.anborja.tucarro.infrastructure.driving.http.dto.request;

import com.anborja.tucarro.domain.util.DomainConstants;
import jakarta.validation.constraints.*;

/**
 * DTO para agregar un nuevo auto
 * Nota: Este es un alias de CreateCarRequest para mantener consistencia en el naming
 */
public class AddCarRequest {

    @NotBlank(message = DomainConstants.CAR_BRAND_REQUIRED)
    @Size(min = DomainConstants.CAR_BRAND_MIN_LENGTH,
            max = DomainConstants.CAR_BRAND_MAX_LENGTH,
            message = DomainConstants.CAR_BRAND_LENGTH)
    private String brand;

    @NotBlank(message = DomainConstants.CAR_MODEL_REQUIRED)
    @Size(min = DomainConstants.CAR_MODEL_MIN_LENGTH,
            max = DomainConstants.CAR_MODEL_MAX_LENGTH,
            message = DomainConstants.CAR_MODEL_LENGTH)
    private String model;

    @NotNull(message = DomainConstants.CAR_YEAR_REQUIRED)
    @Min(value = DomainConstants.CAR_MIN_YEAR, message = DomainConstants.CAR_YEAR_RANGE)
    @Max(value = DomainConstants.CAR_MAX_YEAR, message = DomainConstants.CAR_YEAR_RANGE)
    private Integer year;

    @NotBlank(message = DomainConstants.CAR_PLATE_REQUIRED)
    @Size(min = DomainConstants.CAR_PLATE_MIN_LENGTH,
            max = DomainConstants.CAR_PLATE_MAX_LENGTH,
            message = DomainConstants.CAR_PLATE_LENGTH)
    @Pattern(regexp = DomainConstants.PLATE_REGEX_COLOMBIA, message = DomainConstants.CAR_PLATE_FORMAT)
    private String plateNumber;

    @NotBlank(message = DomainConstants.CAR_COLOR_REQUIRED)
    @Size(min = DomainConstants.CAR_COLOR_MIN_LENGTH,
            max = DomainConstants.CAR_COLOR_MAX_LENGTH,
            message = DomainConstants.CAR_COLOR_LENGTH)
    private String color;

    @Size(max = 500, message = "La URL de la foto no puede exceder 500 caracteres")
    private String photoUrl;

    // Constructor vacío
    public AddCarRequest() {
    }

    // Constructor completo
    public AddCarRequest(String brand, String model, Integer year, String plateNumber, String color, String photoUrl) {
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.plateNumber = plateNumber;
        this.color = color;
        this.photoUrl = photoUrl;
    }

    // Constructor sin foto
    public AddCarRequest(String brand, String model, Integer year, String plateNumber, String color) {
        this(brand, model, year, plateNumber, color, null);
    }

    // Getters y Setters
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

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Override
    public String toString() {
        return "AddCarRequest{" +
                "brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", plateNumber='" + plateNumber + '\'' +
                ", color='" + color + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                '}';
    }
}