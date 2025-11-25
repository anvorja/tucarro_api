package com.anborja.tucarro.domain.exception;

import com.anborja.tucarro.domain.util.DomainConstants;

public class CarAlreadyExistsException extends RuntimeException {

    public CarAlreadyExistsException() {
        super(DomainConstants.CAR_ALREADY_EXISTS_MESSAGE);
    }

    public CarAlreadyExistsException(String message) {
        super(message);
    }

    public CarAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    // Métodos factory para crear excepciones específicas
    public static CarAlreadyExistsException withPlateNumber(String plateNumber) {
        return new CarAlreadyExistsException(
                DomainConstants.CAR_ALREADY_EXISTS_MESSAGE + " con placa: " + plateNumber
        );
    }

    public static CarAlreadyExistsException withPlateNumberAndUserId(String plateNumber, Long userId) {
        return new CarAlreadyExistsException(
                DomainConstants.CAR_ALREADY_EXISTS_MESSAGE + " con placa: " + plateNumber + " para usuario ID: " + userId
        );
    }
}