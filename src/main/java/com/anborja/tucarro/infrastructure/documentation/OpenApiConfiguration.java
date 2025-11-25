package com.anborja.tucarro.infrastructure.documentation;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("🚗 TuCarro - API de Gestión de Autos")
                        .description("""
                                ## API REST para gestión personal de automóviles
                                
                                **TuCarro** es una aplicación que permite a los usuarios registrar y gestionar 
                                información detallada de sus vehículos personales de forma segura y organizada.
                                
                                ### 🚀 Funcionalidades principales:
                                
                                #### 🔐 **Autenticación y Seguridad**
                                - 📝 **Registro de usuarios** con validación completa
                                - 🔑 **Login con JWT** para acceso seguro
                                - 🛡️ **Protección de endpoints** con tokens Bearer
                                - 👤 **Gestión de perfil** personal
                                
                                #### 🚗 **Gestión de Vehículos**
                                - ➕ **Registrar autos** con validación de placa colombiana
                                - 📋 **Listar vehículos** personales
                                - ✏️ **Editar información** de autos existentes
                                - 🗑️ **Eliminar registros** cuando sea necesario
                                
                                #### 🔍 **Búsqueda y Filtros Avanzados**
                                - 🔎 **Búsqueda rápida** por término general
                                - 🎯 **Búsqueda avanzada** con múltiples criterios
                                - 🏷️ **Filtros específicos** por marca, modelo, año, color
                                - 📅 **Filtros por rango de años** (mínimo y máximo)
                                - 🏺 **Categorías especiales** (vintage, nuevos, con foto)
                                - 🔢 **Búsqueda por placa** específica
                                
                                #### 📊 **Estadísticas y Reportes**
                                - 📈 **Estadísticas personales** de la colección
                                - 🏆 **Marcas más comunes** del usuario
                                - 📊 **Análisis por años** (promedio, rango, distribución)
                                - 🔢 **Conteos por categoría** (vintage, nuevos, con foto)
                                
                                ### 🚙 **Formatos de Placa Soportados (Colombia)**
                                - **Formato tradicional**: `ABC123` (3 letras + 3 números)
                                - **Formato nuevo**: `ABC12D` (3 letras + 2 números + 1 letra)
                                
                                ### 📝 **Validaciones del Sistema**
                                - ✅ **Placa única** por usuario (no duplicados)
                                - ✅ **Formato de placa** colombiano válido
                                - ✅ **Años válidos** (1900 - año actual)
                                - ✅ **Email único** en el sistema
                                - ✅ **Campos obligatorios** validados
                                
                                ### 🔧 **Arquitectura**
                                - 🏗️ **Arquitectura Hexagonal** (Puertos y Adaptadores)
                                - 🛡️ **Spring Security** con JWT
                                - 🗃️ **SQL Server** con JPA/Hibernate
                                - ✅ **Validaciones** con Bean Validation
                                - 🚫 **Manejo de errores** centralizado
                                
                                ---
                                
                                ### 📱 **Uso de la API**
                                
                                1. **Registrarse** o hacer **login** para obtener un token JWT
                                2. **Incluir el token** en el header `Authorization: Bearer <token>`
                                3. **Gestionar autos** usando los endpoints protegidos
                                4. **Explorar búsquedas** y **estadísticas** personales
                                
                                ### 🔑 **Autenticación**
                                Todos los endpoints (excepto registro, login y algunos de verificación) 
                                requieren autenticación con token JWT en el header Authorization.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo TuCarro")
                                .email("soporte@tucarro.com")
                                .url("https://github.com/anborja/tucarro-api"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080/api")
                                .description("🛠️ Servidor de desarrollo local"),
                        new Server()
                                .url("https://api.tucarro.com")
                                .description("🌐 Servidor de producción"),
                        new Server()
                                .url("https://staging-api.tucarro.com")
                                .description("🧪 Servidor de staging")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Token JWT obtenido del endpoint de login")))
                .tags(List.of(
                        new Tag()
                                .name("🔐 Autenticación")
                                .description("Endpoints para registro, login y gestión de sesiones"),
                        new Tag()
                                .name("🚗 Gestión de Autos")
                                .description("Operaciones CRUD para autos del usuario autenticado"),
                        new Tag()
                                .name("🔍 Búsqueda de Autos")
                                .description("Búsquedas y filtros avanzados de autos personales"),
                        new Tag()
                                .name("👤 Gestión de Usuario")
                                .description("Operaciones del perfil y configuración del usuario"),
                        new Tag()
                                .name("📊 Estadísticas")
                                .description("Reportes y estadísticas de la colección de autos del usuario"),
                        new Tag()
                                .name("🏥 Sistema")
                                .description("Endpoints de estado y verificación del sistema")
                ));
    }
}