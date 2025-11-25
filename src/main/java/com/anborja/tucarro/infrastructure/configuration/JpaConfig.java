package com.anborja.tucarro.infrastructure.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.anborja.tucarro.infrastructure.driven.jpa.sqlserver.repository")
@EnableTransactionManagement
public class JpaConfig {

    // Esta configuración habilita:
    // 1. Repositorios JPA en el paquete especificado
    // 2. Gestión de transacciones
    // 3. Auditoría JPA (si se configura)

    // La configuración adicional está en application.yml
}