package com.anborja.tucarro.infrastructure.driving.http.mapper;

import com.anborja.tucarro.domain.model.Car;
import com.anborja.tucarro.infrastructure.driving.http.dto.response.CarResponse;
import com.anborja.tucarro.shared.validation.YearValidator;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", imports = {YearValidator.class, LocalDateTime.class})
public interface ICarResponseMapper {

    /**
     * Convierte Car del dominio a CarResponse
     *
     * @param car el modelo del dominio
     * @return el DTO de respuesta
     */
    @Mapping(source = "id", target = "carId")
    @Mapping(source = "userId", target = "ownerId")
    @Mapping(target = "fullDescription", expression = "java(car.getFullDescription())")
    @Mapping(target = "isVintage", expression = "java(YearValidator.isVintage(car.getYear()))")
    @Mapping(target = "isNew", expression = "java(YearValidator.isNew(car.getYear()))")
    @Mapping(target = "ageYears", expression = "java(YearValidator.calculateAge(car.getYear()))")
    CarResponse domainToResponse(Car car);

    /**
     * Convierte una lista de Car a lista de CarResponse
     *
     * @param cars lista de modelos del dominio
     * @return lista de DTOs de respuesta
     */
    List<CarResponse> domainListToResponseList(List<Car> cars);

    /**
     * Crea CarResponse básico (sin campos calculados)
     *
     * @param car el modelo del dominio
     * @return DTO de respuesta simplificado
     */
    @Mapping(source = "id", target = "carId")
    @Mapping(source = "userId", target = "ownerId")
    @Mapping(target = "fullDescription", ignore = true)
    @Mapping(target = "isVintage", ignore = true)
    @Mapping(target = "isNew", ignore = true)
    @Mapping(target = "ageYears", ignore = true)
    @Named("basic")
    CarResponse domainToBasicResponse(Car car);

    /**
     * Convierte Car a CarResponse con información extendida
     *
     * @param car el modelo del dominio
     * @return DTO de respuesta con información adicional
     */
    @Mapping(source = "id", target = "carId")
    @Mapping(source = "userId", target = "ownerId")
    @Mapping(target = "fullDescription", expression = "java(getExtendedDescription(car))")
    @Mapping(target = "isVintage", expression = "java(YearValidator.isVintage(car.getYear()))")
    @Mapping(target = "isNew", expression = "java(YearValidator.isNew(car.getYear()))")
    @Mapping(target = "ageYears", expression = "java(YearValidator.calculateAge(car.getYear()))")
    @Named("extended")
    CarResponse domainToExtendedResponse(Car car);

    /**
     * Método helper para descripción extendida
     */
    default String getExtendedDescription(Car car) {
        StringBuilder description = new StringBuilder();
        description.append(car.getBrand()).append(" ").append(car.getModel()).append(" ").append(car.getYear());

        if (car.getColor() != null) {
            description.append(" - ").append(car.getColor());
        }

        if (YearValidator.isVintage(car.getYear())) {
            description.append(" (Clásico)");
        } else if (YearValidator.isNew(car.getYear())) {
            description.append(" (Nuevo)");
        }

        return description.toString();
    }
}