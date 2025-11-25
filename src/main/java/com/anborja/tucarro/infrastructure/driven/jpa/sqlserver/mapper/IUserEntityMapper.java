package com.anborja.tucarro.infrastructure.driven.jpa.sqlserver.mapper;

import com.anborja.tucarro.domain.model.User;
import com.anborja.tucarro.infrastructure.driven.jpa.sqlserver.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface IUserEntityMapper {

    /**
     * Convierte de UserEntity a User (dominio)
     *
     * @param userEntity la entidad JPA
     * @return el modelo del dominio
     */
    @Mapping(target = "cars", ignore = true) // Evitamos mapear la lista de autos para evitar dependencias circulares
    User entityToDomain(UserEntity userEntity);

    /**
     * Convierte de User (dominio) a UserEntity
     *
     * @param user el modelo del dominio
     * @return la entidad JPA
     */
    @Mapping(target = "cars", ignore = true) // Las cars se manejan por separado
    UserEntity domainToEntity(User user);

    /**
     * Convierte una lista de UserEntity a lista de User
     *
     * @param userEntities lista de entidades JPA
     * @return lista de modelos del dominio
     */
    List<User> entitiesToDomain(List<UserEntity> userEntities);

    /**
     * Actualiza una entidad existente con datos del dominio
     * Solo actualiza campos no nulos del modelo del dominio
     *
     * @param user el modelo del dominio con datos actualizados
     * @param userEntity la entidad existente a actualizar
     */
    @Mapping(target = "id", ignore = true) // No actualizamos el ID
    @Mapping(target = "cars", ignore = true) // No actualizamos la lista de autos
    @Mapping(target = "createdAt", ignore = true) // No actualizamos la fecha de creación
    void updateEntityFromDomain(User user, @MappingTarget UserEntity userEntity);

    /**
     * Crea un UserEntity para inserción (sin ID)
     *
     * @param user el modelo del dominio
     * @return la entidad JPA sin ID para inserción
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cars", ignore = true)
    UserEntity domainToEntityForCreation(User user);
}