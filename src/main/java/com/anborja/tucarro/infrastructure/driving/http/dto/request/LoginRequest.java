package com.anborja.tucarro.infrastructure.driving.http.dto.request;

import com.anborja.tucarro.domain.util.DomainConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {

    @NotBlank(message = DomainConstants.USER_EMAIL_REQUIRED)
    @Email(message = DomainConstants.USER_EMAIL_FORMAT)
    @Size(max = DomainConstants.USER_EMAIL_MAX_LENGTH, message = DomainConstants.USER_EMAIL_LENGTH)
    private String email;

    @NotBlank(message = DomainConstants.USER_PASSWORD_REQUIRED)
    @Size(min = DomainConstants.USER_PASSWORD_MIN_LENGTH,
            max = DomainConstants.USER_PASSWORD_MAX_LENGTH,
            message = DomainConstants.USER_PASSWORD_LENGTH)
    private String password;

    // Constructor vacío
    public LoginRequest() {
    }

    // Constructor completo
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters y Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "email='" + email + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }
}