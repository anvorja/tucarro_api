package com.anborja.tucarro.infrastructure.driving.http.mapper;

import com.anborja.tucarro.domain.model.Car;
import com.anborja.tucarro.infrastructure.driving.http.dto.request.CreateCarRequest;
import com.anborja.tucarro.infrastructure.driving.http.dto.request.UpdateCarRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ICarRequestMapper {

    /**
     * Convierte CreateCarRequest a Car del dominio
     *
     * @param createCarRequest el DTO de creación
     * @return el modelo del dominio
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true) // Se asigna por separado en el caso de uso
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Car createRequestToDomain(CreateCarRequest createCarRequest);

    /**
     * Convierte CreateCarRequest a Car del dominio con userId
     *
     * @param createCarRequest el DTO de creación
     * @param userId el ID del usuario propietario
     * @return el modelo del dominio
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "userId", target = "userId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Car createRequestToDomainWithUserId(CreateCarRequest createCarRequest, Long userId);

    /**
     * Convierte UpdateCarRequest a Car del dominio
     * Solo mapea campos no nulos
     *
     * @param updateCarRequest el DTO de actualización
     * @return el modelo del dominio
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Car updateRequestToDomain(UpdateCarRequest updateCarRequest);

    /**
     * Convierte UpdateCarRequest a Car del dominio con ID y userId
     *
     * @param updateCarRequest el DTO de actualización
     * @param carId el ID del auto
     * @param userId el ID del usuario propietario
     * @return el modelo del dominio
     */
    @Mapping(source = "carId", target = "id")
    @Mapping(source = "userId", target = "userId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Car updateRequestToDomainWithIds(UpdateCarRequest updateCarRequest, Long carId, Long userId);
}