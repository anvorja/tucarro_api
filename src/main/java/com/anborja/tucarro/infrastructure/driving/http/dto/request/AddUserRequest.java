package com.anborja.tucarro.infrastructure.driving.http.dto.request;

import com.anborja.tucarro.domain.util.DomainConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO para agregar un nuevo usuario
 * Nota: Este es un alias de RegisterRequest para mantener consistencia en el naming
 */
public class AddUserRequest {

    @NotBlank(message = DomainConstants.USER_FIRST_NAME_REQUIRED)
    @Size(min = DomainConstants.USER_FIRST_NAME_MIN_LENGTH,
            max = DomainConstants.USER_FIRST_NAME_MAX_LENGTH,
            message = DomainConstants.USER_FIRST_NAME_LENGTH)
    @Pattern(regexp = DomainConstants.NAME_REGEX, message = DomainConstants.USER_FIRST_NAME_FORMAT)
    private String firstName;

    @NotBlank(message = DomainConstants.USER_LAST_NAME_REQUIRED)
    @Size(min = DomainConstants.USER_LAST_NAME_MIN_LENGTH,
            max = DomainConstants.USER_LAST_NAME_MAX_LENGTH,
            message = DomainConstants.USER_LAST_NAME_LENGTH)
    @Pattern(regexp = DomainConstants.NAME_REGEX, message = DomainConstants.USER_LAST_NAME_FORMAT)
    private String lastName;

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
    public AddUserRequest() {
    }

    // Constructor completo
    public AddUserRequest(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    // Getters y Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

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
        return "AddUserRequest{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }
}