package com.anborja.tucarro.infrastructure.driving.http.dto.request;

import com.anborja.tucarro.domain.util.DomainConstants;
import jakarta.validation.constraints.*;

public class UpdateCarRequest {

    @Size(min = DomainConstants.CAR_BRAND_MIN_LENGTH,
            max = DomainConstants.CAR_BRAND_MAX_LENGTH,
            message = DomainConstants.CAR_BRAND_LENGTH)
    private String brand;

    @Size(min = DomainConstants.CAR_MODEL_MIN_LENGTH,
            max = DomainConstants.CAR_MODEL_MAX_LENGTH,
            message = DomainConstants.CAR_MODEL_LENGTH)
    private String model;

    @Min(value = DomainConstants.CAR_MIN_YEAR, message = DomainConstants.CAR_YEAR_RANGE)
    @Max(value = DomainConstants.CAR_MAX_YEAR, message = DomainConstants.CAR_YEAR_RANGE)
    private Integer year;

    @Size(min = DomainConstants.CAR_PLATE_MIN_LENGTH,
            max = DomainConstants.CAR_PLATE_MAX_LENGTH,
            message = DomainConstants.CAR_PLATE_LENGTH)
    @Pattern(regexp = DomainConstants.PLATE_REGEX_COLOMBIA, message = DomainConstants.CAR_PLATE_FORMAT)
    private String plateNumber;

    @Size(min = DomainConstants.CAR_COLOR_MIN_LENGTH,
            max = DomainConstants.CAR_COLOR_MAX_LENGTH,
            message = DomainConstants.CAR_COLOR_LENGTH)
    private String color;

    @Size(max = 500, message = "La URL de la foto no puede exceder 500 caracteres")
    private String photoUrl;

    // Constructor vacío
    public UpdateCarRequest() {
    }

    // Constructor completo
    public UpdateCarRequest(String brand, String model, Integer year, String plateNumber, String color, String photoUrl) {
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.plateNumber = plateNumber;
        this.color = color;
        this.photoUrl = photoUrl;
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
        return "UpdateCarRequest{" +
                "brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", plateNumber='" + plateNumber + '\'' +
                ", color='" + color + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                '}';
    }
}