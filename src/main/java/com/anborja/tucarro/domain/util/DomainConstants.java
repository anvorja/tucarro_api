package com.anborja.tucarro.domain.util;

public class DomainConstants {

    // Constantes de validación para Usuario
    public static final int USER_FIRST_NAME_MIN_LENGTH = 2;
    public static final int USER_FIRST_NAME_MAX_LENGTH = 50;
    public static final int USER_LAST_NAME_MIN_LENGTH = 2;
    public static final int USER_LAST_NAME_MAX_LENGTH = 50;
    public static final int USER_EMAIL_MAX_LENGTH = 100;
    public static final int USER_PASSWORD_MIN_LENGTH = 6;
    public static final int USER_PASSWORD_MAX_LENGTH = 100;

    // Constantes de validación para Auto
    public static final int CAR_BRAND_MIN_LENGTH = 2;
    public static final int CAR_BRAND_MAX_LENGTH = 30;
    public static final int CAR_MODEL_MIN_LENGTH = 1;
    public static final int CAR_MODEL_MAX_LENGTH = 50;
    public static final int CAR_COLOR_MIN_LENGTH = 3;
    public static final int CAR_COLOR_MAX_LENGTH = 20;
    public static final int CAR_PLATE_MIN_LENGTH = 6;
    public static final int CAR_PLATE_MAX_LENGTH = 10;

    // Constantes de año para validación - CAMBIO AQUÍ
    public static final int CAR_MIN_YEAR = 1900;
    public static final int CAR_MAX_YEAR = 2025; // FIJO, actualizar manualmente cada año

    // Expresiones regulares
    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";
    public static final String PLATE_REGEX_COLOMBIA = "^[A-Z]{3}[0-9]{3}$|^[A-Z]{3}[0-9]{2}[A-Z]$";
    public static final String NAME_REGEX = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$";

    // Mensajes de error
    public static final String USER_NOT_FOUND_MESSAGE = "Usuario no encontrado";
    public static final String CAR_NOT_FOUND_MESSAGE = "Auto no encontrado";
    public static final String INVALID_CREDENTIALS_MESSAGE = "Credenciales inválidas";
    public static final String USER_ALREADY_EXISTS_MESSAGE = "El usuario ya existe";
    public static final String CAR_ALREADY_EXISTS_MESSAGE = "Ya existe un auto con esa placa";
    public static final String UNAUTHORIZED_ACCESS_MESSAGE = "Acceso no autorizado";

    // Mensajes de validación - Usuario
    public static final String USER_FIRST_NAME_REQUIRED = "El nombre es obligatorio";
    public static final String USER_FIRST_NAME_LENGTH = "El nombre debe tener entre " + USER_FIRST_NAME_MIN_LENGTH + " y " + USER_FIRST_NAME_MAX_LENGTH + " caracteres";
    public static final String USER_FIRST_NAME_FORMAT = "El nombre solo puede contener letras y espacios";
    public static final String USER_LAST_NAME_REQUIRED = "El apellido es obligatorio";
    public static final String USER_LAST_NAME_LENGTH = "El apellido debe tener entre " + USER_LAST_NAME_MIN_LENGTH + " y " + USER_LAST_NAME_MAX_LENGTH + " caracteres";
    public static final String USER_LAST_NAME_FORMAT = "El apellido solo puede contener letras y espacios";
    public static final String USER_EMAIL_REQUIRED = "El email es obligatorio";
    public static final String USER_EMAIL_FORMAT = "El formato del email no es válido";
    public static final String USER_EMAIL_LENGTH = "El email no puede exceder " + USER_EMAIL_MAX_LENGTH + " caracteres";
    public static final String USER_PASSWORD_REQUIRED = "La contraseña es obligatoria";
    public static final String USER_PASSWORD_LENGTH = "La contraseña debe tener entre " + USER_PASSWORD_MIN_LENGTH + " y " + USER_PASSWORD_MAX_LENGTH + " caracteres";

    // Mensajes de validación - Auto
    public static final String CAR_BRAND_REQUIRED = "La marca es obligatoria";
    public static final String CAR_BRAND_LENGTH = "La marca debe tener entre " + CAR_BRAND_MIN_LENGTH + " y " + CAR_BRAND_MAX_LENGTH + " caracteres";
    public static final String CAR_MODEL_REQUIRED = "El modelo es obligatorio";
    public static final String CAR_MODEL_LENGTH = "El modelo debe tener entre " + CAR_MODEL_MIN_LENGTH + " y " + CAR_MODEL_MAX_LENGTH + " caracteres";
    public static final String CAR_YEAR_REQUIRED = "El año es obligatorio";
    public static final String CAR_YEAR_RANGE = "El año debe estar entre " + CAR_MIN_YEAR + " y " + CAR_MAX_YEAR;
    public static final String CAR_YEAR_FUTURE = "El año no puede ser futuro";
    public static final String CAR_PLATE_REQUIRED = "La placa es obligatoria";
    public static final String CAR_PLATE_LENGTH = "La placa debe tener entre " + CAR_PLATE_MIN_LENGTH + " y " + CAR_PLATE_MAX_LENGTH + " caracteres";
    public static final String CAR_PLATE_FORMAT = "El formato de la placa no es válido (ej: ABC123 o ABC12D)";
    public static final String CAR_COLOR_REQUIRED = "El color es obligatorio";
    public static final String CAR_COLOR_LENGTH = "El color debe tener entre " + CAR_COLOR_MIN_LENGTH + " y " + CAR_COLOR_MAX_LENGTH + " caracteres";
    public static final String CAR_USER_REQUIRED = "El auto debe estar asociado a un usuario";

    // Roles y permisos
    public static final String ROLE_USER = "USER";
    public static final String ROLE_ADMIN = "ADMIN";

    // JWT
    public static final String JWT_TOKEN_PREFIX = "Bearer ";
    public static final String JWT_HEADER_NAME = "Authorization";

    // Paginación
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;

    // Constructor privado para evitar instanciación
    private DomainConstants() {
        throw new IllegalStateException("Utility class");
    }
}