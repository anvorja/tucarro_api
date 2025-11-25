package com.anborja.tucarro.shared.validation;

import com.anborja.tucarro.domain.util.DomainConstants;
import java.util.regex.Pattern;

public class PlateValidator {

    private static final Pattern PLATE_PATTERN = Pattern.compile(DomainConstants.PLATE_REGEX_COLOMBIA);

    /**
     * Valida si una placa tiene el formato correcto según las normas colombianas
     * Formatos válidos: ABC123 o ABC12D
     *
     * @param plateNumber la placa a validar
     * @return true si la placa es válida, false en caso contrario
     */
    public static boolean isValidPlate(String plateNumber) {
        if (plateNumber == null || plateNumber.trim().isEmpty()) {
            return false;
        }

        String cleanPlate = plateNumber.trim().toUpperCase();
        return PLATE_PATTERN.matcher(cleanPlate).matches();
    }

    /**
     * Normaliza una placa eliminando espacios y convirtiendo a mayúsculas
     *
     * @param plateNumber la placa a normalizar
     * @return la placa normalizada o null si la entrada es inválida
     */
    public static String normalizePlate(String plateNumber) {
        if (plateNumber == null) {
            return null;
        }

        String normalized = plateNumber.trim().toUpperCase().replaceAll("\\s+", "");
        return normalized.isEmpty() ? null : normalized;
    }

    /**
     * Valida y normaliza una placa
     *
     * @param plateNumber la placa a validar y normalizar
     * @return la placa normalizada si es válida
     * @throws IllegalArgumentException si la placa no es válida
     */
    public static String validateAndNormalize(String plateNumber) {
        String normalized = normalizePlate(plateNumber);

        if (normalized == null) {
            throw new IllegalArgumentException(DomainConstants.CAR_PLATE_REQUIRED);
        }

        if (normalized.length() < DomainConstants.CAR_PLATE_MIN_LENGTH ||
                normalized.length() > DomainConstants.CAR_PLATE_MAX_LENGTH) {
            throw new IllegalArgumentException(DomainConstants.CAR_PLATE_LENGTH);
        }

        if (!isValidPlate(normalized)) {
            throw new IllegalArgumentException(DomainConstants.CAR_PLATE_FORMAT);
        }

        return normalized;
    }

    /**
     * Verifica si una placa pertenece al formato antiguo (ABC123)
     *
     * @param plateNumber la placa a verificar
     * @return true si es formato antiguo, false si es nuevo (ABC12D)
     */
    public static boolean isOldFormat(String plateNumber) {
        if (!isValidPlate(plateNumber)) {
            return false;
        }

        String normalized = normalizePlate(plateNumber);
        return normalized.matches("^[A-Z]{3}[0-9]{3}$");
    }

    /**
     * Verifica si una placa pertenece al formato nuevo (ABC12D)
     *
     * @param plateNumber la placa a verificar
     * @return true si es formato nuevo, false si es antiguo (ABC123)
     */
    public static boolean isNewFormat(String plateNumber) {
        if (!isValidPlate(plateNumber)) {
            return false;
        }

        String normalized = normalizePlate(plateNumber);
        return normalized.matches("^[A-Z]{3}[0-9]{2}[A-Z]$");
    }

    // Constructor privado para evitar instanciación
    private PlateValidator() {
        throw new IllegalStateException("Utility class");
    }
}