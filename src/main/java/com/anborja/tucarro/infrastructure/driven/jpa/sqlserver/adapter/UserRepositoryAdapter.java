package com.anborja.tucarro.infrastructure.driven.jpa.sqlserver.adapter;

import com.anborja.tucarro.domain.model.User;
import com.anborja.tucarro.domain.spi.IUserRepositoryPort;
import com.anborja.tucarro.infrastructure.driven.jpa.sqlserver.entity.UserEntity;
import com.anborja.tucarro.infrastructure.driven.jpa.sqlserver.mapper.IUserEntityMapper;
import com.anborja.tucarro.infrastructure.driven.jpa.sqlserver.repository.IUserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserRepositoryAdapter implements IUserRepositoryPort {

    private final IUserRepository userRepository; // inyecta implementación JPA
    private final IUserEntityMapper userEntityMapper;

    public UserRepositoryAdapter(IUserRepository userRepository,
                                 IUserEntityMapper userEntityMapper) {
        this.userRepository = userRepository;
        this.userEntityMapper = userEntityMapper;
    }

    @Override
    public User save(User user) {
        UserEntity userEntity;

        if (user.getId() == null) {
            // Crear nuevo usuario
            userEntity = userEntityMapper.domainToEntityForCreation(user);
        } else {
            // Actualizar usuario existente
            // Buscarlo en la base de datos
            Optional<UserEntity> existingEntity = userRepository.findById(user.getId());
            if (existingEntity.isPresent()) {
                userEntity = existingEntity.get();
                userEntityMapper.updateEntityFromDomain(user, userEntity);
            } else {
                userEntity = userEntityMapper.domainToEntity(user);
            }
        }

        UserEntity savedEntity = userRepository.save(userEntity);
        return userEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        return userRepository.findById(id)
                .map(userEntityMapper::entityToDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }

        return userRepository.findByEmail(email.trim().toLowerCase())
                .map(userEntityMapper::entityToDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        return userRepository.existsByEmail(email.trim().toLowerCase());
    }

    @Override
    public List<User> findAll() {
        List<UserEntity> entities = userRepository.findAll();
        return userEntityMapper.entitiesToDomain(entities);
    }

    @Override
    public User update(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("Usuario y ID no pueden ser nulos para actualizar");
        }

        Optional<UserEntity> existingEntity = userRepository.findById(user.getId());
        if (existingEntity.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado para actualizar");
        }

        UserEntity userEntity = existingEntity.get();
        userEntityMapper.updateEntityFromDomain(user, userEntity);

        UserEntity updatedEntity = userRepository.save(userEntity);
        return userEntityMapper.entityToDomain(updatedEntity);
    }

    @Override
    public boolean deleteById(Long id) {
        if (id == null) {
            return false;
        }

        if (!userRepository.existsById(id)) {
            return false;
        }

        try {
            userRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public long count() {
        return userRepository.count();
    }

    @Override
    public List<User> findByNameContaining(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return List.of();
        }

        List<UserEntity> entities = userRepository.findByNameContaining(searchTerm.trim());
        return userEntityMapper.entitiesToDomain(entities);
    }

    @Override
    public boolean existsById(Long id) {
        if (id == null) {
            return false;
        }
        return userRepository.existsById(id);
    }

    /**
     * Métodos adicionales que aprovechan las capacidades del repositorio JPA
     */

    public Optional<User> findByIdWithCars(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        return userRepository.findByIdWithCars(id)
                .map(userEntityMapper::entityToDomain);
    }

    public List<User> findUsersWithCars() {
        List<UserEntity> entities = userRepository.findUsersWithCars();
        return userEntityMapper.entitiesToDomain(entities);
    }

    public List<User> findUsersWithoutCars() {
        List<UserEntity> entities = userRepository.findUsersWithoutCars();
        return userEntityMapper.entitiesToDomain(entities);
    }

    public List<User> findByFullNameContaining(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return List.of();
        }

        List<UserEntity> entities = userRepository.findByFullNameContaining(searchTerm.trim());
        return userEntityMapper.entitiesToDomain(entities);
    }

    public List<User> findByEmailContaining(String emailPart) {
        if (emailPart == null || emailPart.trim().isEmpty()) {
            return List.of();
        }

        List<UserEntity> entities = userRepository.findByEmailContaining(emailPart.trim());
        return userEntityMapper.entitiesToDomain(entities);
    }

    public long countUsersCreatedBetween(java.time.LocalDateTime startDate,
                                         java.time.LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }

        return userRepository.countUsersCreatedBetween(startDate, endDate);
    }
}