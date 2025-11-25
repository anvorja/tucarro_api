package com.anborja.tucarro.shared.validation;

import com.anborja.tucarro.domain.util.DomainConstants;
import java.time.LocalDate;

public class YearValidator {

    /**
     * Valida si un año es válido para un automóvil
     *
     * @param year el año a validar
     * @return true si el año es válido, false en caso contrario
     */
    public static boolean isValidYear(Integer year) {
        if (year == null) {
            return false;
        }

        int currentYear = LocalDate.now().getYear();
        return year >= DomainConstants.CAR_MIN_YEAR && year <= currentYear;
    }

    /**
     * Valida si un año no es futuro
     *
     * @param year el año a validar
     * @return true si el año no es futuro, false en caso contrario
     */
    public static boolean isNotFuture(Integer year) {
        if (year == null) {
            return false;
        }

        int currentYear = LocalDate.now().getYear();
        return year <= currentYear;
    }

    /**
     * Valida si un año está en el rango mínimo permitido
     *
     * @param year el año a validar
     * @return true si el año está en el rango permitido, false en caso contrario
     */
    public static boolean isInValidRange(Integer year) {
        if (year == null) {
            return false;
        }

        return year >= DomainConstants.CAR_MIN_YEAR;
    }

    /**
     * Valida un año y lanza excepciones específicas si no es válido
     *
     * @param year el año a validar
     * @throws IllegalArgumentException si el año no es válido
     */
    public static void validateYear(Integer year) {
        if (year == null) {
            throw new IllegalArgumentException(DomainConstants.CAR_YEAR_REQUIRED);
        }

        if (!isInValidRange(year)) {
            throw new IllegalArgumentException(DomainConstants.CAR_YEAR_RANGE);
        }

        if (!isNotFuture(year)) {
            throw new IllegalArgumentException(DomainConstants.CAR_YEAR_FUTURE);
        }
    }

    /**
     * Determina si un auto es considerado clásico/vintage
     *
     * @param year el año del auto
     * @return true si es un auto clásico (más de 25 años), false en caso contrario
     */
    public static boolean isVintage(Integer year) {
        if (year == null) {
            return false;
        }

        int currentYear = LocalDate.now().getYear();
        return (currentYear - year) >= 25;
    }

    /**
     * Determina si un auto es considerado nuevo
     *
     * @param year el año del auto
     * @return true si es un auto nuevo (menos de 3 años), false en caso contrario
     */
    public static boolean isNew(Integer year) {
        if (year == null) {
            return false;
        }

        int currentYear = LocalDate.now().getYear();
        return (currentYear - year) <= 3;
    }

    /**
     * Calcula la edad del auto en años
     *
     * @param year el año del auto
     * @return la edad del auto en años, o -1 si el año es inválido
     */
    public static int calculateAge(Integer year) {
        if (year == null || !isValidYear(year)) {
            return -1;
        }

        int currentYear = LocalDate.now().getYear();
        return currentYear - year;
    }

    /**
     * Obtiene el año actual
     *
     * @return el año actual
     */
    public static int getCurrentYear() {
        return LocalDate.now().getYear();
    }

    // Constructor privado para evitar instanciación
    private YearValidator() {
        throw new IllegalStateException("Utility class");
    }
}