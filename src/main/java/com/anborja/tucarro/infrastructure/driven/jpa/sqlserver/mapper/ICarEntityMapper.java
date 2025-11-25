package com.anborja.tucarro.infrastructure.driven.jpa.sqlserver.mapper;

import com.anborja.tucarro.domain.model.Car;
import com.anborja.tucarro.infrastructure.driven.jpa.sqlserver.entity.CarEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ICarEntityMapper {

    /**
     * Convierte de CarEntity a Car (dominio)
     *
     * @param carEntity la entidad JPA
     * @return el modelo del dominio
     */
    @Mapping(target = "userId", source = "user.id")
    Car entityToDomain(CarEntity carEntity);

    /**
     * Convierte de Car (dominio) a CarEntity
     * Nota: El UserEntity debe ser asignado por separado
     *
     * @param car el modelo del dominio
     * @return la entidad JPA
     */
    @Mapping(target = "user", ignore = true) // El user se asigna por separado
    CarEntity domainToEntity(Car car);

    /**
     * Convierte una lista de CarEntity a lista de Car
     *
     * @param carEntities lista de entidades JPA
     * @return lista de modelos del dominio
     */
    List<Car> entitiesToDomain(List<CarEntity> carEntities);

    /**
     * Actualiza una entidad existente con datos del dominio
     * Solo actualiza campos no nulos del modelo del dominio
     *
     * @param car el modelo del dominio con datos actualizados
     * @param carEntity la entidad existente a actualizar
     */
    @Mapping(target = "id", ignore = true) // No actualizamos el ID
    @Mapping(target = "user", ignore = true) // No actualizamos la relación con user
    @Mapping(target = "createdAt", ignore = true) // No actualizamos la fecha de creación
    void updateEntityFromDomain(Car car, @MappingTarget CarEntity carEntity);

    /**
     * Crea un CarEntity para inserción (sin ID)
     *
     * @param car el modelo del dominio
     * @return la entidad JPA sin ID para inserción
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true) // Se asigna por separado
    CarEntity domainToEntityForCreation(Car car);

    /**
     * Convierte CarEntity a Car con información básica del usuario
     * Útil cuando necesitamos información del propietario
     *
     * @param carEntity la entidad JPA
     * @return el modelo del dominio con userId
     */
    @org.mapstruct.Named("withUserInfo")
    Car entityToDomainWithUserInfo(CarEntity carEntity);

    /**
     * Actualiza solo los campos básicos del auto (sin relaciones)
     *
     * @param car el modelo del dominio
     * @param carEntity la entidad a actualizar
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true) // Se actualiza automáticamente con @PreUpdate
    void updateBasicFields(Car car, @MappingTarget CarEntity carEntity);
}