package com.anborja.tucarro.domain.exception;

import com.anborja.tucarro.domain.util.DomainConstants;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super(DomainConstants.USER_NOT_FOUND_MESSAGE);
    }

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(Long userId) {
        super(DomainConstants.USER_NOT_FOUND_MESSAGE + " con ID: " + userId);
    }

    public UserNotFoundException(String email, boolean byEmail) {
        super(DomainConstants.USER_NOT_FOUND_MESSAGE + " con email: " + email);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}