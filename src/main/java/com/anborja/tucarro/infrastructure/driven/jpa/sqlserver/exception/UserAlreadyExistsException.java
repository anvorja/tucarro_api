package com.anborja.tucarro.infrastructure.driven.jpa.sqlserver.exception;

import com.anborja.tucarro.domain.util.DomainConstants;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException() {
        super(DomainConstants.USER_ALREADY_EXISTS_MESSAGE);
    }

    public UserAlreadyExistsException(String message) {
        super(message);
    }

    public UserAlreadyExistsException(String email, boolean byEmail) {
        super(DomainConstants.USER_ALREADY_EXISTS_MESSAGE + " con email: " + email);
    }

    public UserAlreadyExistsException(Long userId) {
        super(DomainConstants.USER_ALREADY_EXISTS_MESSAGE + " con ID: " + userId);
    }

    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserAlreadyExistsException(Throwable cause) {
        super(DomainConstants.USER_ALREADY_EXISTS_MESSAGE, cause);
    }
}