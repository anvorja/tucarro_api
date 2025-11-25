# 🧪 Guía Completa de Testing - Endpoints de Búsqueda

## 📋 **PREPARACIÓN INICIAL**

### **1. Obtener Token JWT:**
```http
POST http://localhost:8080/api/v1/auth/login
Content-Type: application/json

{
  "email": "tu_email@ejemplo.com",
  "password": "tu_password"
}
```

**Respuesta esperada:**
```json
{
  "success": true,
  "message": "Inicio de sesión exitoso",
  "data": {
    "access_token": "eyJhbGciOiJIUzI1NiJ9...",
    "token_type": "Bearer",
    "expires_in": 3600,
    "user_info": {
      "user_id": 1,
      "email": "tu_email@ejemplo.com",
      "full_name": "Tu Nombre"
    }
  }
}
```

### **2. Configurar Headers para requests autenticados:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json
```

---

## 🏥 **1. HEALTH CHECK (Sin autenticación)**

### **Endpoint:** `GET /api/v1/cars/search/health`

```http
GET http://localhost:8080/api/v1/cars/search/health
```

**Respuesta esperada:**
```json
{
  "status": "UP",
  "service": "Car Search Service",
  "timestamp": 1703123456789,
  "endpoints_available": [
    "POST /search",
    "GET /quick",
    "GET /plate/{plate}",
    "GET /brand/{brand}",
    "GET /model/{model}",
    "GET /year/{year}",
    "GET /vintage",
    "GET /new",
    "GET /statistics"
  ]
}
```

---

## 🔍 **2. BÚSQUEDA RÁPIDA**

### **Endpoint:** `GET /api/v1/cars/search/quick`

#### **Opción A: Sin término (todos los autos)**
```http
GET http://localhost:8080/api/v1/cars/search/quick
Authorization: Bearer tu_token_aqui
```

#### **Opción B: Con término de búsqueda**
```http
GET http://localhost:8080/api/v1/cars/search/quick?term=toyota
Authorization: Bearer tu_token_aqui
```

**Respuesta esperada:**
```json
{
  "success": true,
  "message": "Búsqueda rápida completada",
  "data": [
    {
      "car_id": 1,
      "brand": "Toyota",
      "model": "Corolla",
      "year": 2020,
      "plate_number": "ABC123",
      "color": "Blanco",
      "photo_url": null,
      "full_description": "Toyota Corolla 2020",
      "is_vintage": false,
      "is_new": true,
      "age_years": 4,
      "owner_id": 1,
      "created_at": "2024-01-15 10:30:00",
      "updated_at": "2024-01-15 10:30:00"
    }
  ],
  "total_results": 1,
  "search_term": "toyota"
}
```

---

## 🎯 **3. BÚSQUEDA AVANZADA**

### **Endpoint:** `POST /api/v1/cars/search`

#### **Ejemplo A: Búsqueda básica**
```http
POST http://localhost:8080/api/v1/cars/search
Authorization: Bearer tu_token_aqui
Content-Type: application/json

{
  "searchTerm": "toyota"
}
```

#### **Ejemplo B: Búsqueda con filtros múltiples**
```http
POST http://localhost:8080/api/v1/cars/search
Authorization: Bearer tu_token_aqui
Content-Type: application/json

{
  "searchTerm": "toyota",
  "minYear": 2015,
  "maxYear": 2023,
  "color": "blanco",
  "sortBy": "year",
  "sortDirection": "desc"
}
```

#### **Ejemplo C: Filtros específicos**
```http
POST http://localhost:8080/api/v1/cars/search
Authorization: Bearer tu_token_aqui
Content-Type: application/json

{
  "brand": "Toyota",
  "model": "Corolla",
  "isNew": true,
  "hasPhoto": false,
  "sortBy": "createdAt",
  "sortDirection": "desc"
}
```

**Respuesta esperada:**
```json
{
  "success": true,
  "message": "Búsqueda completada exitosamente",
  "data": {
    "cars": [
      {
        "car_id": 1,
        "brand": "Toyota",
        "model": "Corolla",
        "year": 2020,
        "plate_number": "ABC123",
        "color": "Blanco",
        "full_description": "Toyota Corolla 2020",
        "is_vintage": false,
        "is_new": true,
        "age_years": 4,
        "owner_id": 1
      }
    ],
    "search_metadata": {
      "total_results": 1,
      "applied_filters": "Término: 'toyota', Años: 2015-2023, Color: blanco",
      "sort_by": "year",
      "sort_direction": "desc",
      "search_term": "toyota"
    },
    "search_statistics": {
      "total_count": 1,
      "vintage_count": 0,
      "new_count": 1,
      "with_photo_count": 0,
      "most_common_brand": "Toyota",
      "year_range": {
        "min_year": 2020,
        "max_year": 2020
      }
    }
  }
}
```

---

## 🔢 **4. BÚSQUEDA POR PLACA**

### **Endpoint:** `GET /api/v1/cars/search/plate/{plateNumber}`

```http
GET http://localhost:8080/api/v1/cars/search/plate/ABC123
Authorization: Bearer tu_token_aqui
```

**Respuesta esperada:**
```json
{
  "success": true,
  "message": "Auto encontrado",
  "data": {
    "car_id": 1,
    "brand": "Toyota",
    "model": "Corolla",
    "year": 2020,
    "plate_number": "ABC123",
    "color": "Blanco",
    "owner_id": 1
  }
}
```

---

## ✅ **5. VERIFICAR DISPONIBILIDAD DE PLACA**

### **Endpoint:** `GET /api/v1/cars/search/plate-available`

#### **Verificar placa nueva**
```http
GET http://localhost:8080/api/v1/cars/search/plate-available?plate=XYZ999
```

#### **Verificar para actualización (excluir usuario)**
```http
GET http://localhost:8080/api/v1/cars/search/plate-available?plate=ABC123&excludeUserId=1
```

**Respuesta esperada:**
```json
{
  "available": true,
  "plate_number": "XYZ999",
  "message": "Placa disponible"
}
```

---

## 🏷️ **6. FILTROS ESPECÍFICOS**

### **A. Por Marca**
```http
GET http://localhost:8080/api/v1/cars/search/brand/Toyota
Authorization: Bearer tu_token_aqui
```

### **B. Por Modelo**
```http
GET http://localhost:8080/api/v1/cars/search/model/Corolla
Authorization: Bearer tu_token_aqui
```

### **C. Por Año**
```http
GET http://localhost:8080/api/v1/cars/search/year/2020
Authorization: Bearer tu_token_aqui
```

**Respuesta esperada (todas similares):**
```json
{
  "success": true,
  "message": "Filtro por marca aplicado",
  "data": [
    {
      "car_id": 1,
      "brand": "Toyota",
      "model": "Corolla",
      "year": 2020,
      "plate_number": "ABC123"
    }
  ],
  "total_results": 1,
  "filter_brand": "Toyota"
}
```

---

## 🏆 **7. FILTROS ESPECIALES**

### **A. Autos Vintage (+25 años)**
```http
GET http://localhost:8080/api/v1/cars/search/vintage
Authorization: Bearer tu_token_aqui
```

### **B. Autos Nuevos (≤3 años)**
```http
GET http://localhost:8080/api/v1/cars/search/new
Authorization: Bearer tu_token_aqui
```

**Respuesta esperada:**
```json
{
  "success": true,
  "message": "Autos vintage obtenidos",
  "data": [
    {
      "car_id": 2,
      "brand": "Volkswagen",
      "model": "Beetle",
      "year": 1995,
      "is_vintage": true,
      "age_years": 29
    }
  ],
  "total_results": 1,
  "filter_type": "vintage"
}
```

---

## 📊 **8. ESTADÍSTICAS**

### **Endpoint:** `GET /api/v1/cars/search/statistics`

```http
GET http://localhost:8080/api/v1/cars/search/statistics
Authorization: Bearer tu_token_aqui
```

**Respuesta esperada:**
```json
{
  "success": true,
  "message": "Estadísticas obtenidas exitosamente",
  "data": {
    "total_cars": 3,
    "vintage_count": 1,
    "new_count": 2,
    "with_photo_count": 1,
    "min_year": 1995,
    "max_year": 2023,
    "average_year": 2012.67,
    "year_range": 28,
    "most_common_brands": [
      "Toyota",
      "Honda",
      "Volkswagen"
    ]
  }
}
```

---

## 🚨 **CASOS DE ERROR A PROBAR**

### **1. Sin autenticación:**
```http
GET http://localhost:8080/api/v1/cars/search/quick
# Sin header Authorization
```

**Respuesta esperada:** `401 Unauthorized`

### **2. Placa no encontrada:**
```http
GET http://localhost:8080/api/v1/cars/search/plate/NOEXISTE
Authorization: Bearer tu_token_aqui
```

**Respuesta esperada:** `404 Not Found`

### **3. Datos inválidos:**
```http
POST http://localhost:8080/api/v1/cars/search
Authorization: Bearer tu_token_aqui
Content-Type: application/json

{
  "minYear": 2030,
  "maxYear": 1990,
  "sortBy": "campo_invalido"
}
```

**Respuesta esperada:** `400 Bad Request`

---

## 📝 **ORDEN RECOMENDADO DE PRUEBAS**

1. ✅ **Health Check** (sin autenticación)
2. 🔐 **Login** para obtener token
3. 📊 **Statistics** (verificar que tienes datos)
4. 🔍 **Quick Search** (sin parámetros)
5. 🔍 **Quick Search** (con término)
6. 🎯 **Advanced Search** (búsqueda básica)
7. 🏷️ **Filtros específicos** (marca, modelo, año)
8. 🏆 **Filtros especiales** (vintage, nuevos)
9. 🔢 **Búsqueda por placa**
10. ✅ **Verificar disponibilidad de placa**
11. 🚨 **Casos de error**

---

## 🛠️ **TIPS PARA POSTMAN**

### **Configurar Variables de Entorno:**
```json
{
  "base_url": "http://localhost:8080/api/v1",
  "jwt_token": "eyJhbGciOiJIUzI1NiJ9...",
  "user_id": "1"
}
```

### **Usar en requests:**
- URL: `{{base_url}}/cars/search/quick`  
- Authorization: `Bearer {{jwt_token}}`

### **Script para extraer token automáticamente:**
```javascript
// En la pestaña "Tests" del request de login:
if (pm.response.code === 200) {
    const response = pm.response.json();
    pm.environment.set("jwt_token", response.data.access_token);
    pm.environment.set("user_id", response.data.user_info.user_id);
}
```