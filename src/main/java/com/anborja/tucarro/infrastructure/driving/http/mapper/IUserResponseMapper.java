package com.anborja.tucarro.infrastructure.driving.http.mapper;

import com.anborja.tucarro.domain.model.User;
import com.anborja.tucarro.infrastructure.driving.http.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IUserResponseMapper {

    /**
     * Convierte User del dominio a UserResponse
     *
     * @param user el modelo del dominio
     * @return el DTO de respuesta
     */
    @Mapping(source = "id", target = "userId")
    @Mapping(target = "fullName", expression = "java(user.getFullName())")
    @Mapping(target = "totalCars", ignore = true) // Se asigna por separado si es necesario
    UserResponse domainToResponse(User user);

    /**
     * Convierte User del dominio a UserResponse con número de autos
     *
     * @param user el modelo del dominio
     * @param totalCars el número total de autos
     * @return el DTO de respuesta con información de autos
     */
    @Mapping(source = "user.id", target = "userId")
    @Mapping(target = "fullName", expression = "java(user.getFullName())")
    @Mapping(source = "totalCars", target = "totalCars")
    UserResponse domainToResponseWithCars(User user, Integer totalCars);

    /**
     * Convierte una lista de User a lista de UserResponse
     *
     * @param users lista de modelos del dominio
     * @return lista de DTOs de respuesta
     */
    List<UserResponse> domainListToResponseList(List<User> users);

    /**
     * Crea UserResponse básico para autenticación
     *
     * @param user el modelo del dominio
     * @return DTO de respuesta simplificado
     */
    @Mapping(source = "id", target = "userId")
    @Mapping(target = "fullName", expression = "java(user.getFullName())")
    @Mapping(target = "totalCars", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Named("forAuth")
    UserResponse domainToAuthResponse(User user);
}