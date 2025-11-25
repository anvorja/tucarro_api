package com.anborja.tucarro.infrastructure.configuration;

import com.anborja.tucarro.shared.constant.AppConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        logger.error("Acceso no autorizado: {}", authException.getMessage());

        // Configurar respuesta HTTP
        response.setContentType(AppConstants.CONTENT_TYPE_JSON);
        response.setCharacterEncoding(AppConstants.CHARSET_UTF8);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Crear cuerpo de respuesta de error
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        errorResponse.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        errorResponse.put("error", "Unauthorized");
        errorResponse.put("message", "Token de autenticación requerido o inválido");
        errorResponse.put("path", request.getRequestURI());
        errorResponse.put("method", request.getMethod());

        // Agregar detalles adicionales si están disponibles
        if (authException.getMessage() != null) {
            errorResponse.put("details", authException.getMessage());
        }

        // Escribir respuesta JSON
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        response.getWriter().flush();
    }
}