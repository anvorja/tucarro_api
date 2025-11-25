package com.anborja.tucarro.shared.constant;

public class AppConstants {

    // Información de la aplicación
    public static final String APP_NAME = "Tu Carro";
    public static final String APP_VERSION = "1.0.0";
    public static final String APP_DESCRIPTION = "Sistema de gestión de autos personales";

    // Endpoints API
    public static final String API_VERSION = "/v1";
    public static final String AUTH_ENDPOINT = "/auth";
    public static final String USERS_ENDPOINT = "/users";
    public static final String CARS_ENDPOINT = "/cars";
    public static final String SEARCH_ENDPOINT = "/search";

    // Endpoints específicos
    public static final String LOGIN_ENDPOINT = AUTH_ENDPOINT + "/login";
    public static final String REGISTER_ENDPOINT = AUTH_ENDPOINT + "/register";
    public static final String REFRESH_TOKEN_ENDPOINT = AUTH_ENDPOINT + "/refresh";
    public static final String LOGOUT_ENDPOINT = AUTH_ENDPOINT + "/logout";

    // Headers HTTP
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CHARSET_UTF8 = "UTF-8";
    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_REQUEST_ID = "X-Request-Id";

    // Códigos de respuesta HTTP personalizados
    public static final String SUCCESS_CODE = "SUCCESS";
    public static final String ERROR_CODE = "ERROR";
    public static final String VALIDATION_ERROR_CODE = "VALIDATION_ERROR";
    public static final String NOT_FOUND_CODE = "NOT_FOUND";
    public static final String UNAUTHORIZED_CODE = "UNAUTHORIZED";
    public static final String FORBIDDEN_CODE = "FORBIDDEN";

    // Mensajes de respuesta
    public static final String SUCCESS_MESSAGE = "Operación exitosa";
    public static final String CREATED_MESSAGE = "Recurso creado exitosamente";
    public static final String UPDATED_MESSAGE = "Recurso actualizado exitosamente";
    public static final String DELETED_MESSAGE = "Recurso eliminado exitosamente";
    public static final String LOGIN_SUCCESS_MESSAGE = "Inicio de sesión exitoso";
    public static final String LOGOUT_SUCCESS_MESSAGE = "Cierre de sesión exitoso";
    public static final String REGISTER_SUCCESS_MESSAGE = "Usuario registrado exitosamente";

    // Formatos de fecha y hora
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    // Configuración de paginación
    public static final String PAGE_PARAM = "page";
    public static final String SIZE_PARAM = "size";
    public static final String SORT_PARAM = "sort";
    public static final String DIRECTION_PARAM = "direction";
    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;
    public static final String DEFAULT_SORT_FIELD = "id";
    public static final String DEFAULT_SORT_DIRECTION = "ASC";

    // Parámetros de búsqueda y filtros
    public static final String SEARCH_PARAM = "search";
    public static final String BRAND_FILTER = "brand";
    public static final String MODEL_FILTER = "model";
    public static final String YEAR_FILTER = "year";
    public static final String COLOR_FILTER = "color";
    public static final String PLATE_FILTER = "plate";
    public static final String MIN_YEAR_FILTER = "minYear";
    public static final String MAX_YEAR_FILTER = "maxYear";

    // Configuración de archivos
    public static final String UPLOAD_DIR = "uploads";
    public static final String PHOTOS_DIR = "photos";
    public static final String TEMP_DIR = "temp";
    public static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    public static final String[] ALLOWED_IMAGE_TYPES = {"jpg", "jpeg", "png", "gif"};

    // Configuración de caché
    public static final String CACHE_USERS = "users";
    public static final String CACHE_CARS = "cars";
    public static final int CACHE_TTL_MINUTES = 60;

    // Logs y auditoría
    public static final String LOG_REQUEST_START = "REQUEST_START";
    public static final String LOG_REQUEST_END = "REQUEST_END";
    public static final String LOG_ERROR = "ERROR";
    public static final String LOG_INFO = "INFO";
    public static final String LOG_DEBUG = "DEBUG";

    // Configuración de seguridad
    public static final String[] PUBLIC_ENDPOINTS = {
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/health",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    };

    // Configuración de CORS
    public static final String[] ALLOWED_ORIGINS = {
            "http://localhost:3000",
            "http://localhost:3001",
            "https://localhost:3000",
            "http://localhost:5173",
            "https://localhost:5173",
            "https://tucarro.onrender.com",
    };

    public static final String[] ALLOWED_METHODS = {
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
    };

    public static final String[] ALLOWED_HEADERS = {
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
    };

    // Configuración de validación
    public static final String VALIDATION_GROUP_CREATE = "Create";
    public static final String VALIDATION_GROUP_UPDATE = "Update";

    // Patrones de validación adicionales
    public static final String PHONE_REGEX = "^[+]?[0-9]{10,15}$";
    public static final String ALPHA_NUMERIC_REGEX = "^[a-zA-Z0-9]+$";
    public static final String ALPHA_REGEX = "^[a-zA-Z]+$";
    public static final String NUMERIC_REGEX = "^[0-9]+$";

    // Constructor privado para evitar instanciación
    private AppConstants() {
        throw new IllegalStateException("Utility class");
    }
}