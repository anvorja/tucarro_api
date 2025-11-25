package com.anborja.tucarro.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsProperties corsProperties;

    public SecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          JwtAuthenticationFilter jwtAuthenticationFilter,
                          CorsProperties corsProperties) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.corsProperties = corsProperties;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitar CSRF ya que usamos JWT
                .csrf(AbstractHttpConfigurer::disable)

                // Configurar CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Configurar gestión de sesiones como stateless
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Configurar punto de entrada para autenticación
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))

                // Configurar autorización de endpoints
                .authorizeHttpRequests(auth -> auth
                        // SWAGGER ENDPOINTS - AGREGAR ESTAS LÍNEAS
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                        .requestMatchers("/swagger-config").permitAll()
                        .requestMatchers("/api-docs/**").permitAll()

                        // Endpoints públicos de autenticación
                        .requestMatchers(HttpMethod.GET, "/v1/cars/search/health").permitAll()
                        .requestMatchers(HttpMethod.POST, "/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/v1/auth/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/auth/health").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/auth/test-hash").permitAll()

                        // Endpoints de prueba
                        .requestMatchers(HttpMethod.GET, "/v1/test/**").permitAll()

                        // Health check general
                        .requestMatchers(HttpMethod.GET, "/health").permitAll()

                        // Endpoints de autenticación que requieren token
                        .requestMatchers("/v1/auth/logout").authenticated()
                        .requestMatchers("/v1/auth/refresh").authenticated()
                        .requestMatchers("/v1/auth/validate").authenticated()

                        // Endpoints de usuario
                        .requestMatchers(HttpMethod.GET, "/v1/users/profile").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/v1/users/profile").authenticated()
                        .requestMatchers(HttpMethod.POST, "/v1/users/change-password").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/v1/users/profile").authenticated()
                        .requestMatchers(HttpMethod.GET, "/v1/users/stats").authenticated()

                        // Endpoints de autos
                        .requestMatchers(HttpMethod.GET, "/v1/cars").authenticated()
                        .requestMatchers(HttpMethod.POST, "/v1/cars").authenticated()
                        .requestMatchers(HttpMethod.GET, "/v1/cars/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/v1/cars/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/v1/cars/**").authenticated()

                        // Endpoints de búsqueda
                        .requestMatchers(HttpMethod.GET, "/v1/search/**").authenticated()

                        // Cualquier otra petición requiere autenticación
                        .anyRequest().authenticated()
                )

                // Agregar filtro JWT antes del filtro de autenticación por usuario y contraseña
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(corsProperties.getAllowedOrigins());
        configuration.setAllowedMethods(corsProperties.getAllowedMethods());
        configuration.setAllowedHeaders(corsProperties.getAllowedHeaders());

        // Permitir credenciales
        configuration.setAllowCredentials(true);

        // Headers expuestos
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

        // Tiempo de vida del preflight request
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}