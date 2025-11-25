package com.anborja.tucarro.domain.exception;

import com.anborja.tucarro.domain.util.DomainConstants;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super(DomainConstants.INVALID_CREDENTIALS_MESSAGE);
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }

    public InvalidCredentialsException(String email, String reason) {
        super(DomainConstants.INVALID_CREDENTIALS_MESSAGE + " para el usuario: " + email + ". " + reason);
    }

    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}