# 🚗 TuCarro API - Guía Completa de Endpoints

## 📋 Información General

**Base URL:** `http://localhost:8080/api`  
**Autenticación:** Bearer Token JWT (excepto endpoints públicos)  
**Content-Type:** `application/json`

---

## 🔐 Autenticación

### 1. Registro de Usuario
```http
POST /v1/auth/register
```

**Descripción:** Registra un nuevo usuario y retorna un token JWT.

**Body:**
```json
{
  "firstName": "Juan Carlos",
  "lastName": "Pérez García",
  "email": "juan.perez@example.com",
  "password": "miPassword123"
}
```

**Validaciones:**
- `firstName`: 2-50 caracteres, solo letras y espacios
- `lastName`: 2-50 caracteres, solo letras y espacios
- `email`: formato válido, máximo 100 caracteres, único
- `password`: 6-100 caracteres

**Respuesta 201:**
```json
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
      "full_name": "Juan Carlos Pérez García"
    },
    "timestamp": "2025-07-24 10:30:00"
  }
}
```

**Errores:**
- `400`: Email ya en uso o datos inválidos
- `400`: Errores de validación

---

### 2. Inicio de Sesión
```http
POST /v1/auth/login
```

**Descripción:** Autentica un usuario y retorna un token JWT.

**Body:**
```json
{
  "email": "juan.perez@example.com",
  "password": "miPassword123"
}
```

**Respuesta 200:**
```json
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
      "full_name": "Juan Carlos Pérez García"
    }
  }
}
```

**Errores:**
- `401`: Credenciales inválidas

---

### 3. Cerrar Sesión
```http
POST /v1/auth/logout
```
**Headers:** `Authorization: Bearer <token>`

**Descripción:** Invalida el token actual.

**Respuesta 200:**
```json
{
  "success": true,
  "message": "Cierre de sesión exitoso"
}
```

---

### 4. Refrescar Token
```http
POST /v1/auth/refresh
```
**Headers:** `Authorization: Bearer <token>`

**Descripción:** Genera un nuevo token con nueva fecha de expiración.

**Respuesta 200:**
```json
{
  "success": true,
  "message": "Token refrescado exitosamente",
  "data": {
    "access_token": "eyJhbGciOiJIUzI1NiJ9...",
    "token_type": "Bearer",
    "expires_in": 86400,
    "user_info": {
      "user_id": 1,
      "email": "juan.perez@example.com",
      "full_name": "Juan Carlos Pérez García"
    }
  }
}
```

---

### 5. Validar Token
```http
POST /v1/auth/validate
```
**Headers:** `Authorization: Bearer <token>`

**Descripción:** Verifica si el token es válido.

**Respuesta 200:**
```json
{
  "valid": true,
  "token_info": {
    "user_id": 1,
    "email": "juan.perez@example.com",
    "expires_at": "2025-07-25T10:30:00Z"
  }
}
```

---

### 6. Health Check Auth
```http
GET /v1/auth/health
```

**Descripción:** Verifica estado del servicio de autenticación.

**Respuesta 200:**
```json
{
  "status": "UP",
  "service": "Authentication Service",
  "timestamp": 1721900400000
}
```

---

## 🚗 Gestión de Autos

### 7. Crear Auto
```http
POST /v1/cars
```
**Headers:** `Authorization: Bearer <token>`

**Descripción:** Registra un nuevo auto para el usuario autenticado.

**Body:**
```json
{
  "brand": "Toyota",
  "model": "Corolla",
  "year": 2023,
  "plateNumber": "ABC123",
  "color": "Blanco",
  "photoUrl": "https://example.com/photo.jpg" // Opcional
}
```

**Validaciones:**
- `brand`: 2-30 caracteres, requerido
- `model`: 1-50 caracteres, requerido
- `year`: 1900-2025, requerido
- `plateNumber`: formato colombiano (ABC123 o ABC12D), único por usuario
- `color`: 3-20 caracteres, requerido
- `photoUrl`: máximo 500 caracteres, opcional

**Respuesta 201:**
```json
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
    "photo_url": "https://example.com/photo.jpg",
    "full_description": "Toyota Corolla 2023",
    "is_vintage": false,
    "is_new": true,
    "age_years": 1,
    "owner_id": 1,
    "created_at": "2025-07-24 10:30:00",
    "updated_at": "2025-07-24 10:30:00"
  }
}
```

**Errores:**
- `409`: Placa ya registrada
- `400`: Errores de validación

---

### 8. Listar Autos del Usuario
```http
GET /v1/cars?sortBy=year&sortOrder=desc
```
**Headers:** `Authorization: Bearer <token>`

**Query Parameters:**
- `sortBy`: `created|year|brand|model` (default: `created`)
- `sortOrder`: `asc|desc` (default: `desc`)

**Descripción:** Obtiene todos los autos del usuario autenticado.

**Respuesta 200:**
```json
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
      "is_new": true,
      "age_years": 1
    }
  ],
  "total": 1
}
```

---

### 9. Obtener Auto por ID
```http
GET /v1/cars/{carId}
```
**Headers:** `Authorization: Bearer <token>`

**Descripción:** Obtiene detalles completos de un auto específico.

**Respuesta 200:**
```json
{
  "success": true,
  "message": "Auto obtenido exitosamente",
  "data": {
    "car_id": 1,
    "brand": "Toyota",
    "model": "Corolla",
    "year": 2023,
    "plate_number": "ABC123",
    "color": "Blanco",
    "photo_url": "https://example.com/photo.jpg",
    "full_description": "Toyota Corolla 2023",
    "is_vintage": false,
    "is_new": true,
    "age_years": 1,
    "owner_id": 1,
    "created_at": "2025-07-24 10:30:00",
    "updated_at": "2025-07-24 10:30:00"
  }
}
```

**Errores:**
- `404`: Auto no encontrado
- `403`: Auto no pertenece al usuario

---

### 10. Actualizar Auto
```http
PUT /v1/cars/{carId}
```
**Headers:** `Authorization: Bearer <token>`

**Descripción:** Actualiza un auto existente.

**Body:** (todos los campos son opcionales)
```json
{
  "brand": "Toyota",
  "model": "Corolla Cross",
  "year": 2024,
  "plateNumber": "ABC123",
  "color": "Negro",
  "photoUrl": "https://example.com/new-photo.jpg"
}
```

**Respuesta 200:**
```json
{
  "success": true,
  "message": "Recurso actualizado exitosamente",
  "data": {
    "car_id": 1,
    "brand": "Toyota",
    "model": "Corolla Cross",
    "year": 2024,
    "plate_number": "ABC123",
    "color": "Negro",
    "photo_url": "https://example.com/new-photo.jpg",
    "full_description": "Toyota Corolla Cross 2024",
    "is_vintage": false,
    "is_new": true,
    "age_years": 0,
    "owner_id": 1,
    "created_at": "2025-07-24 10:30:00",
    "updated_at": "2025-07-24 11:45:00"
  }
}
```

---

### 11. Eliminar Auto
```http
DELETE /v1/cars/{carId}
```
**Headers:** `Authorization: Bearer <token>`

**Descripción:** Elimina un auto del usuario.

**Respuesta 200:**
```json
{
  "success": true,
  "message": "Recurso eliminado exitosamente"
}
```

---

### 12. Búsqueda General de Autos
```http
GET /v1/cars/search?q=toyota
```
**Headers:** `Authorization: Bearer <token>`

**Query Parameters:**
- `q`: término de búsqueda (busca en marca, modelo, color)

**Descripción:** Busca autos del usuario por término general.

**Respuesta 200:**
```json
{
  "success": true,
  "message": "Búsqueda completada",
  "data": [
    {
      "car_id": 1,
      "brand": "Toyota",
      "model": "Corolla",
      "year": 2023,
      "plate_number": "ABC123"
    }
  ],
  "total": 1,
  "search_term": "toyota"
}
```

---

### 13. Filtrar por Marca
```http
GET /v1/cars/filter/brand?brand=Toyota
```
**Headers:** `Authorization: Bearer <token>`

**Respuesta 200:**
```json
{
  "success": true,
  "message": "Filtrado por marca completado",
  "data": [...],
  "total": 2,
  "filter": {"type": "brand", "value": "Toyota"}
}
```

---

### 14. Filtrar por Modelo
```http
GET /v1/cars/filter/model?model=Corolla
```
**Headers:** `Authorization: Bearer <token>`

---

### 15. Filtrar por Año
```http
GET /v1/cars/filter/year?year=2023
```
**Headers:** `Authorization: Bearer <token>`

---

### 16. Filtrar por Rango de Años
```http
GET /v1/cars/filter/year-range?minYear=2020&maxYear=2024
```
**Headers:** `Authorization: Bearer <token>`

---

### 17. Filtrar por Color
```http
GET /v1/cars/filter/color?color=Blanco
```
**Headers:** `Authorization: Bearer <token>`

---

### 18. Obtener Autos Vintage
```http
GET /v1/cars/vintage
```
**Headers:** `Authorization: Bearer <token>`

**Descripción:** Obtiene autos de más de 25 años.

---

### 19. Obtener Autos Nuevos
```http
GET /v1/cars/new
```
**Headers:** `Authorization: Bearer <token>`

**Descripción:** Obtiene autos de 3 años o menos.

---

### 20. Estadísticas de Autos
```http
GET /v1/cars/stats
```
**Headers:** `Authorization: Bearer <token>`

**Descripción:** Obtiene estadísticas de los autos del usuario.

**Respuesta 200:**
```json
{
  "success": true,
  "message": "Estadísticas obtenidas exitosamente",
  "data": {
    "user_id": 1,
    "total_cars": 5,
    "vintage_car_count": 1,
    "new_car_count": 2,
    "most_common_brand": "Toyota",
    "newest_car_year": 2024,
    "oldest_car_year": 1995
  }
}
```

---

### 21. Verificar Disponibilidad de Placa
```http
GET /v1/cars/plate-available?plateNumber=XYZ999
```

**Descripción:** Verifica si una placa está disponible (endpoint público).

**Respuesta 200:**
```json
{
  "plate_number": "XYZ999",
  "available": true,
  "message": "Placa disponible"
}
```

---

### 22. Buscar por Placa
```http
GET /v1/cars/plate/{plateNumber}
```
**Headers:** `Authorization: Bearer <token>`

**Descripción:** Busca un auto específico por placa del usuario.

---

## 🔍 Búsqueda Avanzada de Autos

### 23. Búsqueda Avanzada
```http
POST /v1/cars/search
```
**Headers:** `Authorization: Bearer <token>`

**Descripción:** Búsqueda con múltiples criterios.

**Body:**
```json
{
  "searchTerm": "toyota",
  "brand": "Toyota",
  "model": "Corolla",
  "year": 2023,
  "minYear": 2020,
  "maxYear": 2024,
  "color": "Blanco",
  "plateNumber": "ABC123",
  "sortBy": "year",
  "sortDirection": "desc",
  "isVintage": false,
  "isNew": true,
  "hasPhoto": true
}
```

**Respuesta 200:**
```json
{
  "success": true,
  "message": "Búsqueda completada exitosamente",
  "data": {
    "cars": [...],
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
```

---

### 24. Búsqueda Rápida
```http
GET /v1/cars/search/quick?term=toyota
```
**Headers:** `Authorization: Bearer <token>`

---

### 25. Buscar por Placa (Búsqueda)
```http
GET /v1/cars/search/plate/{plateNumber}
```
**Headers:** `Authorization: Bearer <token>`

---

### 26. Verificar Disponibilidad de Placa (Búsqueda)
```http
GET /v1/cars/search/plate-available?plate=ABC123&excludeUserId=1
```

---

### 27. Filtrar por Marca (Búsqueda)
```http
GET /v1/cars/search/brand/{brand}
```
**Headers:** `Authorization: Bearer <token>`

---

### 28. Filtrar por Modelo (Búsqueda)
```http
GET /v1/cars/search/model/{model}
```
**Headers:** `Authorization: Bearer <token>`

---

### 29. Filtrar por Año (Búsqueda)
```http
GET /v1/cars/search/year/{year}
```
**Headers:** `Authorization: Bearer <token>`

---

### 30. Autos Vintage (Búsqueda)
```http
GET /v1/cars/search/vintage
```
**Headers:** `Authorization: Bearer <token>`

---

### 31. Autos Nuevos (Búsqueda)
```http
GET /v1/cars/search/new
```
**Headers:** `Authorization: Bearer <token>`

---

### 32. Estadísticas de Búsqueda
```http
GET /v1/cars/search/statistics
```
**Headers:** `Authorization: Bearer <token>`

**Respuesta 200:**
```json
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
```

---

### 33. Health Check Búsqueda
```http
GET /v1/cars/search/health
```

---

## 👤 Gestión de Usuario

### 34. Obtener Perfil
```http
GET /v1/users/profile
```
**Headers:** `Authorization: Bearer <token>`

**Descripción:** Obtiene el perfil del usuario autenticado.

**Respuesta 200:**
```json
{
  "success": true,
  "message": "Perfil obtenido exitosamente",
  "data": {
    "user_id": 1,
    "first_name": "Juan Carlos",
    "last_name": "Pérez García",
    "email": "juan.perez@example.com",
    "full_name": "Juan Carlos Pérez García",
    "total_cars": 3,
    "created_at": "2025-01-15 10:30:00",
    "updated_at": "2025-07-24 10:30:00"
  }
}
```

---

### 35. Actualizar Perfil
```http
PUT /v1/users/profile
```
**Headers:** `Authorization: Bearer <token>`

**Body:**
```json
{
  "firstName": "Juan Carlos",
  "lastName": "Pérez García",
  "email": "nuevo.email@example.com"
}
```

---

### 36. Cambiar Contraseña
```http
POST /v1/users/change-password
```
**Headers:** `Authorization: Bearer <token>`

**Body:**
```json
{
  "currentPassword": "miPasswordActual",
  "newPassword": "miNuevoPassword123",
  "confirmPassword": "miNuevoPassword123"
}
```

**Respuesta 200:**
```json
{
  "success": true,
  "message": "Contraseña cambiada exitosamente"
}
```

---

### 37. Estadísticas del Usuario
```http
GET /v1/users/stats
```
**Headers:** `Authorization: Bearer <token>`

**Respuesta 200:**
```json
{
  "success": true,
  "message": "Estadísticas obtenidas exitosamente",
  "data": {
    "user_id": 1,
    "total_cars": 3,
    "registration_date": "2025-01-15 10:30:00",
    "last_update": "2025-07-24 10:30:00"
  }
}
```

---

### 38. Eliminar Cuenta
```http
DELETE /v1/users/profile
```
**Headers:** `Authorization: Bearer <token>`

**Descripción:** Elimina la cuenta del usuario y todos sus autos.

**Respuesta 200:**
```json
{
  "success": true,
  "message": "Cuenta eliminada exitosamente"
}
```

---

### 39. Buscar Usuarios
```http
GET /v1/users/search?searchTerm=Juan
```
**Headers:** `Authorization: Bearer <token>`

---

### 40. Verificar Disponibilidad de Email
```http
GET /v1/users/email-available?email=test@example.com
```

**Respuesta 200:**
```json
{
  "email": "test@example.com",
  "available": true,
  "message": "Email disponible"
}
```

---

## 🔧 Endpoints de Prueba

### 41. Health Check General
```http
GET /health
```

---

### 42. Test Hash
```http
GET /v1/test/hash
```

**Descripción:** Prueba la funcionalidad de hash de contraseñas.

---

### 43. Test Health
```http
GET /v1/test/health
```

---

## 📝 Formatos de Respuesta

### Respuesta Exitosa Estándar
```json
{
  "success": true,
  "message": "Descripción del resultado",
  "data": { /* datos específicos */ }
}
```

### Respuesta de Error Estándar
```json
{
  "timestamp": "2025-07-24 10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Descripción del error",
  "path": "/api/v1/cars",
  "method": "POST",
  "details": "Información adicional del error"
}
```

### Error de Validación
```json
{
  "timestamp": "2025-07-24 10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Los datos proporcionados no son válidos",
  "validation_errors": [
    {
      "field": "email",
      "rejected_value": "invalid-email",
      "message": "El formato del email no es válido"
    }
  ]
}
```

---

## 🔐 Autenticación

Para usar endpoints protegidos, incluir el header:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsImVtYWlsIjoidGVzdEBleGFtcGxlLmNvbSIsInN1YiI6InRlc3RAZXhhbXBsZS5jb20iLCJpYXQiOjE3MjE5MDA0MDAsImV4cCI6MTcyMTk4NjgwMH0.example
```

**Token expira en:** 24 horas (86400 segundos)

---

## 📊 Swagger UI

**URL:** `http://localhost:8080/api/swagger-ui.html`

La documentación interactiva está disponible en Swagger UI donde puedes:
- Ver todos los endpoints
- Probar las APIs directamente
- Ver ejemplos de request/response
- Generar tokens y probar autenticación

---

## 🛡️ Validaciones de Placa

**Formatos válidos para Colombia:**
- Formato tradicional: `ABC123` (3 letras + 3 números)
- Formato nuevo: `ABC12D` (3 letras + 2 números + 1 letra)

**Ejemplos válidos:** `ABC123`, `DEF456`, `GHI78J`, `KLM90N`