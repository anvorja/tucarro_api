package com.anborja.tucarro.infrastructure.driving.http.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class CarResponse {

    @JsonProperty("car_id")
    private Long carId;

    private String brand;

    private String model;

    private Integer year;

    @JsonProperty("plate_number")
    private String plateNumber;

    private String color;

    @JsonProperty("photo_url")
    private String photoUrl;

    @JsonProperty("full_description")
    private String fullDescription;

    @JsonProperty("is_vintage")
    private Boolean isVintage;

    @JsonProperty("is_new")
    private Boolean isNew;

    @JsonProperty("age_years")
    private Integer ageYears;

    @JsonProperty("owner_id")
    private Long ownerId;

    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // Constructor vacío
    public CarResponse() {
    }

    // Constructor completo
    public CarResponse(Long carId, String brand, String model, Integer year, String plateNumber,
                       String color, String photoUrl, Long ownerId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.carId = carId;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.plateNumber = plateNumber;
        this.color = color;
        this.photoUrl = photoUrl;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

        // Calcular campos derivados
        updateDerivedFields();
    }

    // Getters y Setters
    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
        updateDerivedFields();
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
        updateDerivedFields();
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
        updateDerivedFields();
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

    public String getFullDescription() {
        return fullDescription;
    }

    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription;
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

    public Integer getAgeYears() {
        return ageYears;
    }

    public void setAgeYears(Integer ageYears) {
        this.ageYears = ageYears;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Método para actualizar campos derivados
    private void updateDerivedFields() {
        // Descripción completa
        if (brand != null && model != null && year != null) {
            this.fullDescription = brand + " " + model + " " + year;
        }

        // Verificar si es vintage (más de 25 años)
        if (year != null) {
            int currentYear = LocalDateTime.now().getYear();
            this.ageYears = currentYear - year;
            this.isVintage = this.ageYears >= 25;
            this.isNew = this.ageYears <= 3;
        }
    }

    @Override
    public String toString() {
        return "CarResponse{" +
                "carId=" + carId +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", plateNumber='" + plateNumber + '\'' +
                ", color='" + color + '\'' +
                ", fullDescription='" + fullDescription + '\'' +
                ", isVintage=" + isVintage +
                ", isNew=" + isNew +
                ", ageYears=" + ageYears +
                ", ownerId=" + ownerId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}