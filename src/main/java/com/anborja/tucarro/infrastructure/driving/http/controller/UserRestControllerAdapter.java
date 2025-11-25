package com.anborja.tucarro.infrastructure.driving.http.controller;

import com.anborja.tucarro.domain.api.IUserServicePort;
import com.anborja.tucarro.domain.model.User;
import com.anborja.tucarro.infrastructure.documentation.ApiDocumentation;
import com.anborja.tucarro.infrastructure.driving.http.dto.request.ChangePasswordRequest;
import com.anborja.tucarro.infrastructure.driving.http.dto.request.UpdateUserRequest;
import com.anborja.tucarro.infrastructure.driving.http.dto.response.UserResponse;
import com.anborja.tucarro.infrastructure.driving.http.mapper.IUserRequestMapper;
import com.anborja.tucarro.infrastructure.driving.http.mapper.IUserResponseMapper;
import com.anborja.tucarro.shared.constant.AppConstants;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(AppConstants.API_VERSION + AppConstants.USERS_ENDPOINT)
@Tag(name = "👤 Gestión de Usuario", description = "Operaciones del perfil y configuración personal")
public class UserRestControllerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(UserRestControllerAdapter.class);

    private final IUserServicePort userServicePort;
    private final IUserRequestMapper userRequestMapper;
    private final IUserResponseMapper userResponseMapper;

    public UserRestControllerAdapter(IUserServicePort userServicePort,
                                     IUserRequestMapper userRequestMapper,
                                     IUserResponseMapper userResponseMapper) {
        this.userServicePort = userServicePort;
        this.userRequestMapper = userRequestMapper;
        this.userResponseMapper = userResponseMapper;
    }

    /**
     * Obtiene el perfil del usuario autenticado
     */
    @GetMapping("/profile")
    @ApiDocumentation.GetUserProfileDocumentation
    public ResponseEntity<Map<String, Object>> getUserProfile(HttpServletRequest request) {
        logger.info("Obteniendo perfil de usuario");

        try {
            Long userId = extractUserIdFromRequest(request);

            // Obtener usuario y estadísticas
            User user = userServicePort.getUserProfile(userId);
            IUserServicePort.UserStats userStats = userServicePort.getUserStats(userId);

            // Convertir a DTO
            UserResponse userResponse = userResponseMapper.domainToResponseWithCars(user, userStats.getTotalCars());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Perfil obtenido exitosamente");
            response.put("data", userResponse);

            logger.info("Perfil obtenido para usuario ID: {}", userId);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error al obtener perfil: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Actualiza el perfil del usuario autenticado
     */
    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateUserProfile(@Valid @RequestBody UpdateUserRequest updateRequest,
                                                                 HttpServletRequest request) {
        logger.info("Actualizando perfil de usuario");

        try {
            Long userId = extractUserIdFromRequest(request);

            // Convertir DTO a modelo del dominio
            User userUpdates = userRequestMapper.updateRequestToDomain(updateRequest);

            // Actualizar usuario
            User updatedUser = userServicePort.updateUserProfile(userId, userUpdates);

            // Obtener estadísticas actualizadas
            IUserServicePort.UserStats userStats = userServicePort.getUserStats(userId);

            // Convertir a DTO
            UserResponse userResponse = userResponseMapper.domainToResponseWithCars(updatedUser, userStats.getTotalCars());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", AppConstants.UPDATED_MESSAGE);
            response.put("data", userResponse);

            logger.info("Perfil actualizado para usuario ID: {}", userId);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error al actualizar perfil: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Cambia la contraseña del usuario autenticado
     */
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest,
                                                              HttpServletRequest request) {
        logger.info("Cambiando contraseña de usuario");

        try {
            // Validar que las contraseñas coincidan
            if (!changePasswordRequest.passwordsMatch()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Las contraseñas no coinciden");

                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            Long userId = extractUserIdFromRequest(request);

            // Cambiar contraseña
            boolean changed = userServicePort.changePassword(
                    userId,
                    changePasswordRequest.getCurrentPassword(),
                    changePasswordRequest.getNewPassword()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", changed);
            response.put("message", changed ? "Contraseña cambiada exitosamente" : "Error al cambiar contraseña");

            if (changed) {
                logger.info("Contraseña cambiada para usuario ID: {}", userId);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {
            logger.error("Error al cambiar contraseña: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Obtiene las estadísticas del usuario autenticado
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getUserStats(HttpServletRequest request) {
        logger.info("Obteniendo estadísticas de usuario");

        try {
            Long userId = extractUserIdFromRequest(request);

            // Obtener estadísticas
            IUserServicePort.UserStats userStats = userServicePort.getUserStats(userId);

            // Crear respuesta con estadísticas detalladas
            Map<String, Object> statsData = new HashMap<>();
            statsData.put("user_id", userStats.getUserId());
            statsData.put("total_cars", userStats.getTotalCars());
            statsData.put("registration_date", userStats.getRegistrationDate());
            statsData.put("last_update", userStats.getLastUpdate());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Estadísticas obtenidas exitosamente");
            response.put("data", statsData);

            logger.info("Estadísticas obtenidas para usuario ID: {}", userId);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error al obtener estadísticas: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Elimina la cuenta del usuario autenticado
     */
    @DeleteMapping("/profile")
    public ResponseEntity<Map<String, Object>> deleteUserAccount(HttpServletRequest request) {
        logger.info("Eliminando cuenta de usuario");

        try {
            Long userId = extractUserIdFromRequest(request);

            // Eliminar usuario (esto también eliminará todos sus autos)
            boolean deleted = userServicePort.deleteUser(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", deleted);
            response.put("message", deleted ? "Cuenta eliminada exitosamente" : "Error al eliminar cuenta");

            if (deleted) {
                logger.info("Cuenta eliminada para usuario ID: {}", userId);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {
            logger.error("Error al eliminar cuenta: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Busca usuarios (funcionalidad administrativa o de búsqueda)
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchUsers(@RequestParam String searchTerm) {
        logger.info("Buscando usuarios con término: {}", searchTerm);

        try {
            // Buscar usuarios
            List<User> users = userServicePort.searchUsers(searchTerm);

            // Convertir a DTOs
            List<UserResponse> userResponses = userResponseMapper.domainListToResponseList(users);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Búsqueda completada");
            response.put("data", userResponses);
            response.put("total", userResponses.size());

            logger.info("Búsqueda completada. Encontrados {} usuarios", userResponses.size());
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error en búsqueda de usuarios: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Verifica si un email está disponible
     */
    @GetMapping("/email-available")
    public ResponseEntity<Map<String, Object>> checkEmailAvailability(@RequestParam String email) {
        logger.info("Verificando disponibilidad de email: {}", email);

        try {
            boolean available = userServicePort.isEmailAvailable(email);

            Map<String, Object> response = new HashMap<>();
            response.put("email", email);
            response.put("available", available);
            response.put("message", available ? "Email disponible" : "Email ya está en uso");

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error al verificar email: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Método helper para extraer el ID del usuario desde el request
     */
    private Long extractUserIdFromRequest(HttpServletRequest request) {
        Object userIdAttribute = request.getAttribute("userId");

        if (userIdAttribute instanceof Long) {
            return (Long) userIdAttribute;
        } else if (userIdAttribute instanceof Integer) {
            return ((Integer) userIdAttribute).longValue();
        } else {
            throw new IllegalArgumentException("ID de usuario no encontrado en el request");
        }
    }
}