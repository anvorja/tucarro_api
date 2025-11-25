package com.anborja.tucarro.infrastructure.configuration;

import com.anborja.tucarro.domain.api.IAuthServicePort;
import com.anborja.tucarro.domain.api.ICarSearchServicePort;
import com.anborja.tucarro.domain.api.ICarServicePort;
import com.anborja.tucarro.domain.api.IUserServicePort;
import com.anborja.tucarro.domain.api.usecase.AuthUseCase;
import com.anborja.tucarro.domain.api.usecase.CarSearchUseCase;
import com.anborja.tucarro.domain.api.usecase.CarUseCase;
import com.anborja.tucarro.domain.api.usecase.UserUseCase;
import com.anborja.tucarro.domain.spi.ICarRepositoryPort;
import com.anborja.tucarro.domain.spi.IJwtTokenPort;
import com.anborja.tucarro.domain.spi.IPasswordEncoderPort;
import com.anborja.tucarro.domain.spi.IUserRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BeanConfiguration {

    /**
     * Configuración del codificador de contraseñas
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // return new Argon2PasswordEncoder();         // ← otra opción
        return new BCryptPasswordEncoder(12); // Strength de 12 para mejor seguridad
    }

    /**
     * Bean para el caso de uso de autenticación
     */
    @Bean
    public IAuthServicePort authServicePort(IUserRepositoryPort userRepositoryPort,
                                            IPasswordEncoderPort passwordEncoderPort,
                                            IJwtTokenPort jwtTokenPort) {
        return new AuthUseCase(userRepositoryPort, passwordEncoderPort, jwtTokenPort);
    }

    /**
     * Bean para el caso de uso de usuario
     */
    @Bean
    public IUserServicePort userServicePort(IUserRepositoryPort userRepositoryPort,
                                            IPasswordEncoderPort passwordEncoderPort,
                                            ICarRepositoryPort carRepositoryPort) {
        return new UserUseCase(userRepositoryPort, passwordEncoderPort, carRepositoryPort);
    }

    /**
     * Bean para el caso de uso de auto
     */
    @Bean
    public ICarServicePort carServicePort(ICarRepositoryPort carRepositoryPort,
                                          IUserRepositoryPort userRepositoryPort) {
        return new CarUseCase(carRepositoryPort, userRepositoryPort);
    }

    /**
     * Bean para el caso de uso de búsqueda de autos
     */
    @Bean
    public ICarSearchServicePort carSearchServicePort(ICarRepositoryPort carRepositoryPort,
                                                      IUserRepositoryPort userRepositoryPort) {
        return new CarSearchUseCase(carRepositoryPort, userRepositoryPort);
    }
}