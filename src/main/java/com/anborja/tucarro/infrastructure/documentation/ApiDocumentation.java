package com.anborja.tucarro.infrastructure.documentation;

import com.anborja.tucarro.infrastructure.driving.http.dto.response.CarResponse;
import com.anborja.tucarro.infrastructure.driving.http.dto.response.UserResponse;
import com.anborja.tucarro.infrastructure.driving.http.dto.response.AuthResponse;
import com.anborja.tucarro.infrastructure.driving.http.dto.response.CarSearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class ApiDocumentation {

    // ========== ANOTACIONES PARA AUTENTICACIÓN ==========

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "🔐 Registro de usuario",
            description = """
                    Registra un nuevo usuario en el sistema y retorna un token JWT para autenticación.
                    El sistema validará que el email no esté en uso y que todos los campos cumplan
                    con los requisitos de validación.
                    """,
            tags = {"🔐 Autenticación"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "✅ Usuario registrado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(
                                    name = "Registro exitoso",
                                    value = """
                                            {
                                              "success": true,
                                              "message": "Usuario registrado exitosamente",
                                              "data": {
                                                "access_token": "eyJhbGciOiJIUzI1NiJ9...",
                                                "token_type": "Bearer",
                                                "expires_in": 86400,
                                                "user_info": {
                                                  "user_id": 1,
                                                  "email": "juan.perez@example.com",
                                                  "full_name": "Juan Pérez"
                                                },
                                                "timestamp": "2025-07-24 10:30:00"
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "🚫 Error en la validación de datos",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Email ya en uso",
                                            value = """
                                                    {
                                                      "timestamp": "2025-07-24 10:30:00",
                                                      "status": 400,
                                                      "error": "Bad Request",
                                                      "message": "El usuario ya existe",
                                                      "path": "/api/v1/auth/register"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Datos inválidos",
                                            value = """
                                                    {
                                                      "timestamp": "2025-07-24 10:30:00",
                                                      "status": 400,
                                                      "error": "Validation Failed",
                                                      "message": "Los datos proporcionados no son válidos",
                                                      "validation_errors": [
                                                        {
                                                          "field": "email",
                                                          "message": "El formato del email no es válido"
                                                        }
                                                      ]
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    public @interface RegisterDocumentation {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "🔑 Inicio de sesión",
            description = "Autentica un usuario con email y contraseña, retornando un token JWT válido para acceder a endpoints protegidos.",
            tags = {"🔐 Autenticación"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "✅ Login exitoso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(
                                    name = "Login exitoso",
                                    value = """
                                            {
                                              "success": true,
                                              "message": "Inicio de sesión exitoso",
                                              "data": {
                                                "access_token": "eyJhbGciOiJIUzI1NiJ9...",
                                                "token_type": "Bearer",
                                                "expires_in": 86400,
                                                "user_info": {
                                                  "user_id": 1,
                                                  "email": "juan.perez@example.com",
                                                  "full_name": "Juan Pérez"
                                                }
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "🚫 Credenciales inválidas",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Credenciales incorrectas",
                                    value = """
                                            {
                                              "timestamp": "2025-07-24 10:30:00",
                                              "status": 401,
                                              "error": "Invalid Credentials",
                                              "message": "Credenciales inválidas",
                                              "path": "/api/v1/auth/login"
                                            }
                                            """
                            )
                    )
            )
    })
    public @interface LoginDocumentation {}

    // ========== ANOTACIONES PARA GESTIÓN DE AUTOS ==========

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "🚗 Crear nuevo auto",
            description = """
                    Registra un nuevo auto para el usuario autenticado. El sistema validará que:
                    - La placa tenga formato colombiano válido (ABC123 o ABC12D)
                    - La placa no esté registrada por otro usuario
                    - Todos los campos cumplan con las validaciones
                    """,
            tags = {"🚗 Gestión de Autos"},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "✅ Auto creado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CarResponse.class),
                            examples = @ExampleObject(
                                    name = "Auto creado",
                                    value = """
                                            {
                                              "success": true,
                                              "message": "Recurso creado exitosamente",
                                              "data": {
                                                "car_id": 1,
                                                "brand": "Toyota",
                                                "model": "Corolla",
                                                "year": 2023,
                                                "plate_number": "ABC123",
                                                "color": "Blanco",
                                                "photo_url": null,
                                                "full_description": "Toyota Corolla 2023",
                                                "is_vintage": false,
                                                "is_new": true,
                                                "age_years": 1,
                                                "owner_id": 1,
                                                "created_at": "2025-07-24 10:30:00"
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "🚫 Placa ya registrada",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Placa en uso",
                                    value = """
                                            {
                                              "timestamp": "2025-07-24 10:30:00",
                                              "status": 409,
                                              "error": "Car Already Exists",
                                              "message": "Ya existe un auto con esa placa: ABC123"
                                            }
                                            """
                            )
                    )
            )
    })
    public @interface CreateCarDocumentation {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "📋 Listar autos del usuario",
            description = "Obtiene todos los autos registrados por el usuario autenticado, con opción de ordenamiento.",
            tags = {"🚗 Gestión de Autos"},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "✅ Lista de autos obtenida",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CarResponse.class),
                            examples = @ExampleObject(
                                    name = "Lista de autos",
                                    value = """
                                            {
                                              "success": true,
                                              "message": "Autos obtenidos exitosamente",
                                              "data": [
                                                {
                                                  "car_id": 1,
                                                  "brand": "Toyota",
                                                  "model": "Corolla",
                                                  "year": 2023,
                                                  "plate_number": "ABC123",
                                                  "color": "Blanco",
                                                  "is_vintage": false,
                                                  "is_new": true
                                                }
                                              ],
                                              "total": 1
                                            }
                                            """
                            )
                    )
            )
    })
    public @interface GetUserCarsDocumentation {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "🔍 Obtener auto por ID",
            description = "Obtiene los detalles completos de un auto específico del usuario autenticado.",
            tags = {"🚗 Gestión de Autos"},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "✅ Auto encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CarResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "🚫 Auto no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Auto no encontrado",
                                    value = """
                                            {
                                              "timestamp": "2025-07-24 10:30:00",
                                              "status": 404,
                                              "error": "Car Not Found",
                                              "message": "Auto no encontrado con ID: 999"
                                            }
                                            """
                            )
                    )
            )
    })
    public @interface GetCarByIdDocumentation {}

    // ========== ANOTACIONES PARA BÚSQUEDA DE AUTOS ==========

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "🔍 Búsqueda rápida de autos",
            description = "Realiza una búsqueda simple en marca, modelo y color de los autos del usuario.",
            tags = {"🔍 Búsqueda de Autos"},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "✅ Búsqueda completada",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Resultados de búsqueda",
                                    value = """
                                            {
                                              "success": true,
                                              "message": "Búsqueda rápida completada",
                                              "data": [
                                                {
                                                  "car_id": 1,
                                                  "brand": "Toyota",
                                                  "model": "Corolla",
                                                  "year": 2023,
                                                  "plate_number": "ABC123"
                                                }
                                              ],
                                              "total_results": 1,
                                              "search_term": "toyota"
                                            }
                                            """
                            )
                    )
            )
    })
    public @interface QuickSearchDocumentation {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "🎯 Búsqueda avanzada de autos",
            description = """
                    Realiza búsquedas complejas con múltiples criterios:
                    - Término general de búsqueda
                    - Filtros por marca, modelo, año, color
                    - Rangos de años (mínimo y máximo)
                    - Filtros especiales (vintage, nuevos, con/sin foto)
                    - Opciones de ordenamiento
                    """,
            tags = {"🔍 Búsqueda de Autos"},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "✅ Búsqueda avanzada completada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CarSearchResponse.class),
                            examples = @ExampleObject(
                                    name = "Búsqueda con estadísticas",
                                    value = """
                                            {
                                              "success": true,
                                              "message": "Búsqueda completada exitosamente",
                                              "data": {
                                                "cars": [
                                                  {
                                                    "car_id": 1,
                                                    "brand": "Toyota",
                                                    "model": "Corolla",
                                                    "year": 2023,
                                                    "is_vintage": false,
                                                    "is_new": true
                                                  }
                                                ],
                                                "search_metadata": {
                                                  "total_results": 1,
                                                  "applied_filters": "Marca: Toyota, Año: 2023",
                                                  "sort_by": "year",
                                                  "sort_direction": "desc"
                                                },
                                                "search_statistics": {
                                                  "total_count": 1,
                                                  "vintage_count": 0,
                                                  "new_count": 1,
                                                  "most_common_brand": "Toyota"
                                                }
                                              }
                                            }
                                            """
                            )
                    )
            )
    })
    public @interface AdvancedSearchDocumentation {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "📊 Estadísticas de autos del usuario",
            description = "Obtiene estadísticas detalladas de la colección de autos del usuario, incluyendo conteos por categoría, marcas más comunes y rangos de años.",
            tags = {"📊 Estadísticas"},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "✅ Estadísticas obtenidas",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Estadísticas completas",
                                    value = """
                                            {
                                              "success": true,
                                              "message": "Estadísticas obtenidas exitosamente",
                                              "data": {
                                                "total_cars": 5,
                                                "vintage_count": 1,
                                                "new_count": 2,
                                                "with_photo_count": 3,
                                                "min_year": 1995,
                                                "max_year": 2024,
                                                "average_year": 2015.2,
                                                "year_range": 29,
                                                "most_common_brands": ["Toyota", "Honda", "Ford"]
                                              }
                                            }
                                            """
                            )
                    )
            )
    })
    public @interface CarStatisticsDocumentation {}

    // ========== ANOTACIONES PARA GESTIÓN DE USUARIOS ==========

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "👤 Obtener perfil del usuario",
            description = "Obtiene la información completa del perfil del usuario autenticado, incluyendo estadísticas básicas.",
            tags = {"👤 Gestión de Usuario"},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "✅ Perfil obtenido",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class),
                            examples = @ExampleObject(
                                    name = "Perfil completo",
                                    value = """
                                            {
                                              "success": true,
                                              "message": "Perfil obtenido exitosamente",
                                              "data": {
                                                "user_id": 1,
                                                "first_name": "Juan",
                                                "last_name": "Pérez",
                                                "email": "juan.perez@example.com",
                                                "full_name": "Juan Pérez",
                                                "total_cars": 3,
                                                "created_at": "2025-01-15 10:30:00"
                                              }
                                            }
                                            """
                            )
                    )
            )
    })
    public @interface GetUserProfileDocumentation {}

    // ========== ANOTACIONES PARA FILTROS ESPECÍFICOS ==========

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "🏷️ Filtrar autos por marca",
            description = "Obtiene todos los autos del usuario que coincidan con la marca especificada. La búsqueda no es sensible a mayúsculas.",
            tags = {"🔍 Búsqueda de Autos"},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public @interface FilterByBrandDocumentation {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "🚙 Filtrar autos por modelo",
            description = "Obtiene todos los autos del usuario que coincidan con el modelo especificado.",
            tags = {"🔍 Búsqueda de Autos"},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public @interface FilterByModelDocumentation {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "📅 Filtrar autos por año",
            description = "Obtiene todos los autos del usuario del año especificado.",
            tags = {"🔍 Búsqueda de Autos"},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public @interface FilterByYearDocumentation {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "🏺 Obtener autos vintage",
            description = "Obtiene todos los autos vintage del usuario (más de 25 años de antigüedad).",
            tags = {"🔍 Búsqueda de Autos"},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public @interface GetVintageCarsDocumentation {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "✨ Obtener autos nuevos",
            description = "Obtiene todos los autos nuevos del usuario (3 años o menos de antigüedad).",
            tags = {"🔍 Búsqueda de Autos"},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public @interface GetNewCarsDocumentation {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "✅ Verificar disponibilidad de placa",
            description = "Verifica si una placa específica está disponible para registro o si ya está en uso.",
            tags = {"🔍 Búsqueda de Autos"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "✅ Verificación completada",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Placa disponible",
                                    value = """
                                            {
                                              "available": true,
                                              "plate_number": "XYZ999",
                                              "message": "Placa disponible"
                                            }
                                            """
                            )
                    )
            )
    })
    public @interface CheckPlateAvailabilityDocumentation {}

    // ========== ANOTACIÓN PARA BÚSQUEDA PAGINADA ==========

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "📄 Búsqueda paginada de autos",
            description = """
                    Búsqueda avanzada de autos con soporte para paginación, ordenamiento y filtros múltiples.
                    Ideal para interfaces de usuario que necesitan mostrar grandes cantidades de datos
                    de forma eficiente con navegación por páginas.
                    
                    ### 🔍 **Parámetros de búsqueda disponibles:**
                    - **searchTerm**: Búsqueda general en marca, modelo, placa y color
                    - **brand**: Filtro específico por marca
                    - **model**: Filtro específico por modelo
                    - **year**: Filtro por año exacto
                    - **color**: Filtro por color exacto
                    - **minYear**: Año mínimo (rango)
                    - **maxYear**: Año máximo (rango)
                    
                    ### 📄 **Parámetros de paginación:**
                    - **page**: Número de página (inicia en 0)
                    - **size**: Elementos por página (máximo 100)
                    - **sortBy**: Campo para ordenar (brand, model, year, createdAt, etc.)
                    - **sortDirection**: Dirección del ordenamiento (asc/desc)
                    
                    ### ⚡ **Optimizaciones incluidas:**
                    - Consultas optimizadas a base de datos
                    - Índices para mejor rendimiento
                    - Límites de tamaño para prevenir sobrecarga
                    - Ordenamiento eficiente
                    """,
            tags = {"🔍 Búsqueda de Autos"},
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "✅ Búsqueda paginada completada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Respuesta paginada exitosa",
                                    value = """
                                            {
                                              "success": true,
                                              "message": "Se encontraron 45 autos",
                                              "data": {
                                                "content": [
                                                  {
                                                    "car_id": 1,
                                                    "brand": "Toyota",
                                                    "model": "Corolla",
                                                    "year": 2020,
                                                    "color": "Blanco",
                                                    "plate_number": "ABC123",
                                                    "created_at": "2024-01-15T10:30:00"
                                                  },
                                                  {
                                                    "car_id": 2,
                                                    "brand": "Honda",
                                                    "model": "Civic",
                                                    "year": 2019,
                                                    "color": "Negro",
                                                    "plate_number": "XYZ789",
                                                    "created_at": "2024-01-14T14:20:00"
                                                  }
                                                ],
                                                "pageInfo": {
                                                  "page": 0,
                                                  "size": 20,
                                                  "totalPages": 3,
                                                  "totalElements": 45,
                                                  "first": true,
                                                  "last": false,
                                                  "hasNext": true,
                                                  "hasPrevious": false,
                                                  "sort": {
                                                    "sorted": true,
                                                    "sortBy": "year",
                                                    "direction": "DESC"
                                                  }
                                                }
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "❌ Parámetros de paginación inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Error de parámetros",
                                    value = """
                                            {
                                              "success": false,
                                              "message": "Parámetros de paginación inválidos",
                                              "error": "El tamaño de página no puede ser mayor a 100"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "🔒 No autorizado - Token JWT requerido",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "No autorizado",
                                    value = """
                                            {
                                              "success": false,
                                              "message": "Token de acceso requerido",
                                              "error": "Authorization header missing or invalid"
                                            }
                                            """
                            )
                    )
            )
    })
    public @interface PaginatedSearchDocumentation {}
}