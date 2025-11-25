package com.anborja.tucarro.infrastructure.driven.jpa.sqlserver.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cars", indexes = {
        @Index(name = "idx_car_plate", columnList = "plate_number", unique = true),
        @Index(name = "idx_car_user_id", columnList = "user_id"),
        @Index(name = "idx_car_brand", columnList = "brand"),
        @Index(name = "idx_car_year", columnList = "year")
})
public class CarEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "brand", nullable = false, length = 30)
    private String brand;

    @Column(name = "model", nullable = false, length = 50)
    private String model;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "plate_number", nullable = false, unique = true, length = 10)
    private String plateNumber;

    @Column(name = "color", nullable = false, length = 20)
    private String color;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Relación Many-to-One con UserEntity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_car_user"))
    private UserEntity user;

    // Constructor vacío requerido por JPA
    public CarEntity() {
    }

    // Constructor completo
    public CarEntity(Long id, String brand, String model, Integer year, String plateNumber,
                     String color, String photoUrl, LocalDateTime createdAt, LocalDateTime updatedAt, UserEntity user) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.plateNumber = plateNumber;
        this.color = color;
        this.photoUrl = photoUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.user = user;
    }

    // Constructor para crear auto (sin ID)
    public CarEntity(String brand, String model, Integer year, String plateNumber,
                     String color, String photoUrl, UserEntity user) {
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.plateNumber = plateNumber;
        this.color = color;
        this.photoUrl = photoUrl;
        this.user = user;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor sin foto
    public CarEntity(String brand, String model, Integer year, String plateNumber,
                     String color, UserEntity user) {
        this(brand, model, year, plateNumber, color, null, user);
    }

    // Métodos de callback JPA
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    // Métodos de conveniencia
    public String getFullDescription() {
        return brand + " " + model + " " + year;
    }

    public boolean isVintage() {
        return year != null && year < 2000;
    }

    public Long getUserId() {
        return user != null ? user.getId() : null;
    }

    public int getAge() {
        if (year == null) return 0;
        return LocalDateTime.now().getYear() - year;
    }

    // toString para debugging
    @Override
    public String toString() {
        return "CarEntity{" +
                "id=" + id +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", plateNumber='" + plateNumber + '\'' +
                ", color='" + color + '\'' +
                ", userId=" + (user != null ? user.getId() : null) +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    // equals y hashCode basados en plateNumber (único)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarEntity carEntity = (CarEntity) o;
        return plateNumber != null && plateNumber.equals(carEntity.plateNumber);
    }

    @Override
    public int hashCode() {
        return plateNumber != null ? plateNumber.hashCode() : 0;
    }
}