package com.anborja.tucarro.infrastructure.driving.http.mapper;

import com.anborja.tucarro.domain.model.User;
import com.anborja.tucarro.infrastructure.driving.http.dto.request.RegisterRequest;
import com.anborja.tucarro.infrastructure.driving.http.dto.request.UpdateUserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface IUserRequestMapper {

    /**
     * Convierte RegisterRequest a User del dominio
     *
     * @param registerRequest el DTO de registro
     * @return el modelo del dominio
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "cars", ignore = true)
    User registerRequestToDomain(RegisterRequest registerRequest);

    /**
     * Convierte UpdateUserRequest a User del dominio
     * Solo mapea campos no nulos
     *
     * @param updateUserRequest el DTO de actualización
     * @return el modelo del dominio
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "cars", ignore = true)
    User updateRequestToDomain(UpdateUserRequest updateUserRequest);
}