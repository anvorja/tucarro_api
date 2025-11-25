package com.anborja.tucarro.infrastructure.configuration;

import com.anborja.tucarro.domain.spi.IJwtTokenPort;
import com.anborja.tucarro.domain.util.DomainConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final IJwtTokenPort jwtTokenPort;

    public JwtAuthenticationFilter(IJwtTokenPort jwtTokenPort) {
        this.jwtTokenPort = jwtTokenPort;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Log para debug
        logger.debug("JWT Filter - Processing request: {} {}", request.getMethod(), request.getRequestURI());

        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtTokenPort.validateToken(jwt)) {
                String email = jwtTokenPort.extractEmail(jwt);
                Long userId = jwtTokenPort.extractUserId(jwt);

                // Crear el objeto de autenticación
                List<SimpleGrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_" + DomainConstants.ROLE_USER)
                );

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(email, null, authorities);

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Agregar el userId como atributo personalizado
                request.setAttribute("userId", userId);
                request.setAttribute("userEmail", email);

                // Establecer la autenticación en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authentication);

                logger.debug("Usuario autenticado: {} con ID: {}", email, userId);
            } else {
                logger.debug("Token JWT no válido o no presente para: {}", request.getRequestURI());
            }
        } catch (Exception ex) {
            logger.error("No se pudo establecer la autenticación del usuario: {}", ex.getMessage());
            // No lanzamos la excepción para permitir que el filtro continúe
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token JWT del header Authorization
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(DomainConstants.JWT_HEADER_NAME);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(DomainConstants.JWT_TOKEN_PREFIX)) {
            return bearerToken.substring(DomainConstants.JWT_TOKEN_PREFIX.length());
        }

        return null;
    }

    /**
     * Determina si este filtro debe ejecutarse para la petición dada
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        logger.debug("Evaluando shouldNotFilter para: {} {}", method, path);

        // No filtrar peticiones OPTIONS (CORS preflight)
        if ("OPTIONS".equals(method)) {
            logger.debug("Skipping JWT filter for OPTIONS request");
            return true;
        }

        // SWAGGER ENDPOINTS - ACTUALIZAR ESTA SECCIÓN
        if (path.equals("/api/swagger-ui.html") ||
                path.startsWith("/api/swagger-ui/") ||
                path.startsWith("/api/v3/api-docs") ||
                path.startsWith("/api/swagger-resources") ||
                path.startsWith("/api/webjars/") ||
                path.equals("/api/swagger-config") ||
                path.startsWith("/api/api-docs")) {
            logger.debug("Swagger endpoint detectado, saltando filtro JWT para: {}", path);
            return true;
        }

        // Endpoints públicos de autenticación (SIN /api porque está en context-path)
        if (path.equals("/api/v1/auth/login") ||
                path.equals("/api/v1/auth/register") ||
                path.equals("/api/v1/auth/health") ||
                path.equals("/api/v1/auth/test-hash") ||
                path.equals("/api/health")) {
            logger.debug("Endpoint público detectado, saltando filtro JWT para: {}", path);
            return true;
        }

        // Endpoints de prueba
        if (path.startsWith("/api/v1/test/")) {
            logger.debug("Test endpoint detectado, saltando filtro JWT para: {}", path);
            return true;
        }

        logger.debug("Aplicando filtro JWT para: {}", path);
        return false;
    }
}