# Estructura de Carpetas - Arquitectura Hexagonal Refinada

```
src/main/java/com/anborja/tucarro/
│
├── TucarroApplication.java
│
├── domain/                          # 🟡 NÚCLEO DEL DOMINIO (sin dependencias externas)
│   ├── model/                       # Entidades de dominio
│   │   ├── User.java
│   │   └── Car.java
│   │
│   ├── api/                         # Puertos de entrada (API - Application Programming Interface)
│   │   ├── IAuthServicePort.java
│   │   ├── IUserServicePort.java
│   │   ├── ICarServicePort.java
│   │   └── ICarSearchServicePort.java  # Opcional: para funcionalidades de búsqueda
│   │   │
│   │   └── usecase/                 # Casos de uso (implementan los puertos API)
│   │       ├── AuthUseCase.java
│   │       ├── UserUseCase.java
│   │       ├── CarUseCase.java
│   │       └── CarSearchUseCase.java   # Opcional: para búsquedas avanzadas
│   │
│   ├── spi/                         # Puertos de salida (SPI - Service Provider Interface)
│   │   ├── IUserRepositoryPort.java
│   │   ├── ICarRepositoryPort.java
│   │   └── IPasswordEncoderPort.java
│   │
│   ├── exception/                   # Excepciones de dominio
│   │   ├── UserNotFoundException.java
│   │   ├── CarNotFoundException.java
│   │   └── InvalidCredentialsException.java
│   │
│   └── util/                        # Utilidades de dominio
│       └── DomainConstants.java
│
├── infrastructure/                  # 🔴 ADAPTADORES
│   ├── driving/                     # Adaptadores primarios (entrada)
│   │   └── http/                    # REST API
│   │       ├── controller/
│   │       │   ├── AuthControllerAdapter.java
│   │       │   ├── UserRestControllerAdapter.java
│   │       │   └── CarRestControllerAdapter.java
│   │       │
│   │       ├── dto/
│   │       │   ├── request/
│   │       │   │   ├── LoginRequest.java
│   │       │   │   ├── RegisterRequest.java
│   │       │   │   ├── AddUserRequest.java
│   │       │   │   ├── UpdateUserRequest.java
│   │       │   │   ├── AddCarRequest.java
│   │       │   │   ├── CreateCarRequest.java
│   │       │   │   └── UpdateCarRequest.java
│   │       │   │
│   │       │   └── response/
│   │       │       ├── AuthResponse.java
│   │       │       ├── UserResponse.java
│   │       │       └── CarResponse.java
│   │       │
│   │       └── mapper/              # Mappers HTTP (con MapStruct)
│   │           ├── IUserRequestMapper.java
│   │           ├── IUserResponseMapper.java
│   │           ├── ICarRequestMapper.java
│   │           └── ICarResponseMapper.java
│   │
│   ├── driven/                      # Adaptadores secundarios (salida)
│   │   ├── jpa/
│   │   │   └── sqlserver/
│   │   │       ├── adapter/
│   │   │       │   ├── UserRepositoryAdapter.java
│   │   │       │   └── CarRepositoryAdapter.java
│   │   │       │
│   │   │       ├── entity/
│   │   │       │   ├── UserEntity.java
│   │   │       │   └── CarEntity.java
│   │   │       │
│   │   │       ├── repository/
│   │   │       │   ├── IUserRepository.java
│   │   │       │   └── ICarRepository.java
│   │   │       │
│   │   │       ├── mapper/          # Mappers JPA (con MapStruct)
│   │   │       │   ├── IUserEntityMapper.java
│   │   │       │   └── ICarEntityMapper.java
│   │   │       │
│   │   │       └── exception/       # Excepciones específicas de JPA
│   │   │           ├── ElementNotFoundException.java
│   │   │           ├── NoDataFoundException.java
│   │   │           ├── UserAlreadyExistsException.java
│   │   │           └── CarAlreadyExistsException.java
│   │   │
│   │   └── security/                # Adaptador de seguridad
│   │       ├── PasswordEncoderAdapter.java
│   │       └── JwtTokenProvider.java
│   │
│   ├── configuration/               # Configuraciones Spring
│   │   ├── SecurityConfig.java
│   │   ├── JpaConfig.java
│   │   ├── BeanConfiguration.java   # Inyección de dependencias
│   │   └── CorsConfig.java
│   │
│   └── exception/                   # Manejo global de excepciones
│       ├── GlobalExceptionHandler.java
│       └── ErrorResponse.java
│
└── shared/                          # 🟢 UTILIDADES COMPARTIDAS
    ├── validation/                  # Validaciones personalizadas
    │   ├── PlateValidator.java
    │   └── YearValidator.java
    │
    └── constant/                    # Constantes globales
        └── AppConstants.java
```

## 🎯 **Ubicación de los elementos restantes:**

### **✅ Seguridad:**

```
infrastructure/driven/security/
├── PasswordEncoderAdapter.java    # Implementa IPasswordEncoderPort
└── JwtTokenProvider.java          # Utilidad para JWT
```

### **✅ Configuraciones:**

```
infrastructure/configuration/
├── SecurityConfig.java            # Configuración Spring Security
├── JpaConfig.java                # Configuración JPA/Hibernate  
├── BeanConfiguration.java        # @Bean para inyectar UseCases
└── CorsConfig.java               # Configuración CORS
```

### **✅ Manejo de excepciones:**

```
infrastructure/exception/
├── GlobalExceptionHandler.java   # @ControllerAdvice
└── ErrorResponse.java           # DTO para respuestas de error
```

### **✅ Validaciones y constantes:**

```
shared/
├── validation/                   # Validaciones que usan multiple capas
└── constant/                    # Constantes globales
```

## 🚀 **Flujo de dependencias:**

```
HTTP Request → Controller → UseCase → Domain Service → Repository Port → Repository Adapter → Database
     ↑              ↑           ↑            ↑               ↑                ↑
  driving/http  domain/api  domain/api  domain/spi    driven/jpa      driven/jpa
```