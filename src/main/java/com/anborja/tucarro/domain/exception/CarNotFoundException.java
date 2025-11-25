package com.anborja.tucarro.domain.exception;

import com.anborja.tucarro.domain.util.DomainConstants;

public class CarNotFoundException extends RuntimeException {

    public CarNotFoundException() {
        super(DomainConstants.CAR_NOT_FOUND_MESSAGE);
    }

    public CarNotFoundException(String message) {
        super(message);
    }

    public CarNotFoundException(Long carId) {
        super(DomainConstants.CAR_NOT_FOUND_MESSAGE + " con ID: " + carId);
    }

    public CarNotFoundException(String plateNumber, boolean byPlate) {
        super(DomainConstants.CAR_NOT_FOUND_MESSAGE + " con placa: " + plateNumber);
    }

    public CarNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}