package com.anborja.tucarro.infrastructure.driving.http.controller;

import com.anborja.tucarro.domain.api.IAuthServicePort;
import com.anborja.tucarro.domain.model.User;
import com.anborja.tucarro.domain.spi.IJwtTokenPort;
import com.anborja.tucarro.infrastructure.documentation.ApiDocumentation;
import com.anborja.tucarro.infrastructure.driving.http.dto.request.LoginRequest;
import com.anborja.tucarro.infrastructure.driving.http.dto.request.RegisterRequest;
import com.anborja.tucarro.infrastructure.driving.http.dto.response.AuthResponse;
import com.anborja.tucarro.infrastructure.driving.http.mapper.IUserRequestMapper;
import com.anborja.tucarro.shared.constant.AppConstants;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(AppConstants.API_VERSION + AppConstants.AUTH_ENDPOINT)
@Tag(name = "🔐 Autenticación", description = "Gestión de registro, login y autenticación JWT")
public class AuthControllerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(AuthControllerAdapter.class);

    private final IAuthServicePort authServicePort;
    private final IUserRequestMapper userRequestMapper;
    private final IJwtTokenPort jwtTokenPort;

    public AuthControllerAdapter(IAuthServicePort authServicePort,
                                 IUserRequestMapper userRequestMapper,
                                 IJwtTokenPort jwtTokenPort) {
        this.authServicePort = authServicePort;
        this.userRequestMapper = userRequestMapper;
        this.jwtTokenPort = jwtTokenPort;
    }

    @GetMapping("/test-hash")
    public ResponseEntity<String> testHash() {
        PasswordEncoder encoder = new BCryptPasswordEncoder(12);
        String hash = encoder.encode("password");
        return ResponseEntity.ok("Hash: " + hash);
    }

    /**
     * Endpoint para registro de usuario
     */
    @PostMapping("/register")
    @ApiDocumentation.RegisterDocumentation
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        logger.info("Intento de registro para email: {}", registerRequest.getEmail());

        try {
            // Convertir DTO a modelo del dominio
            User user = userRequestMapper.registerRequestToDomain(registerRequest);

            // Registrar usuario
            User registeredUser = authServicePort.register(user);

            // Generar token para el usuario registrado
            String token = authServicePort.generateToken(registeredUser);

            // Crear respuesta de autenticación
            AuthResponse authResponse = new AuthResponse(
                    token,
                    jwtTokenPort.getExpirationTime() / 1000, // Convertir a segundos
                    registeredUser.getId(),
                    registeredUser.getEmail(),
                    registeredUser.getFullName()
            );

            // Respuesta exitosa
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", AppConstants.REGISTER_SUCCESS_MESSAGE);
            response.put("data", authResponse);

            logger.info("Usuario registrado exitosamente: {}", registeredUser.getEmail());
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            logger.error("Error durante el registro: {}", e.getMessage());
            throw e; // El GlobalExceptionHandler se encargará de esto
        }
    }

    /**
     * Endpoint para login de usuario
     */
    @PostMapping("/login")
    @ApiDocumentation.LoginDocumentation
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Intento de login para email: {}", loginRequest.getEmail());

        try {
            // Autenticar usuario
            String token = authServicePort.login(loginRequest.getEmail(), loginRequest.getPassword());

            // Extraer información del usuario del token
            String email = authServicePort.extractEmailFromToken(token);
            Long userId = authServicePort.extractUserIdFromToken(token);

            // Crear respuesta de autenticación
            AuthResponse authResponse = new AuthResponse(
                    token,
                    jwtTokenPort.getExpirationTime() / 1000, // Convertir a segundos
                    userId,
                    email,
                    email // Por simplicidad, usar email como nombre completo aquí
            );

            // Respuesta exitosa
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", AppConstants.LOGIN_SUCCESS_MESSAGE);
            response.put("data", authResponse);

            logger.info("Login exitoso para usuario: {}", email);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error durante el login: {}", e.getMessage());
            throw e; // El GlobalExceptionHandler se encargará de esto
        }
    }

    /**
     * Endpoint para logout de usuario
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);

            if (token != null && authServicePort.validateToken(token)) {
                // Invalidar token
                boolean invalidated = authServicePort.invalidateToken(token);

                Map<String, Object> response = new HashMap<>();
                response.put("success", invalidated);
                response.put("message", invalidated ? AppConstants.LOGOUT_SUCCESS_MESSAGE : "Error al cerrar sesión");

                logger.info("Logout exitoso");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Token inválido o no proporcionado");

                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            logger.error("Error durante el logout: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Endpoint para refrescar token
     */
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshToken(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);

            if (token != null && authServicePort.validateToken(token)) {
                // Refrescar token usando el JwtTokenPort
                String newToken = jwtTokenPort.refreshToken(token);

                // Extraer información del nuevo token
                String email = authServicePort.extractEmailFromToken(newToken);
                Long userId = authServicePort.extractUserIdFromToken(newToken);

                // Crear respuesta de autenticación
                AuthResponse authResponse = new AuthResponse(
                        newToken,
                        jwtTokenPort.getExpirationTime() / 1000,
                        userId,
                        email,
                        email
                );

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Token refrescado exitosamente");
                response.put("data", authResponse);

                logger.info("Token refrescado para usuario: {}", email);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Token inválido o expirado");

                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            logger.error("Error al refrescar token: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Endpoint para validar token
     */
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            boolean isValid = token != null && authServicePort.validateToken(token);

            Map<String, Object> response = new HashMap<>();
            response.put("valid", isValid);

            if (isValid) {
                String email = authServicePort.extractEmailFromToken(token);
                Long userId = authServicePort.extractUserIdFromToken(token);

                Map<String, Object> tokenInfo = new HashMap<>();
                tokenInfo.put("user_id", userId);
                tokenInfo.put("email", email);
                tokenInfo.put("expires_at", jwtTokenPort.extractExpiration(token));

                response.put("token_info", tokenInfo);
            }

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error al validar token: {}", e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("error", e.getMessage());

            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    /**
     * Endpoint de salud para verificar que el servicio de auth está funcionando
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Authentication Service");
        response.put("timestamp", System.currentTimeMillis());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Método helper para extraer token del header Authorization
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}