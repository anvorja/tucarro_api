package com.anborja.tucarro.infrastructure.driving.http.dto.request;

import com.anborja.tucarro.domain.util.DomainConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UpdateUserRequest {

    @Size(min = DomainConstants.USER_FIRST_NAME_MIN_LENGTH,
            max = DomainConstants.USER_FIRST_NAME_MAX_LENGTH,
            message = DomainConstants.USER_FIRST_NAME_LENGTH)
    @Pattern(regexp = DomainConstants.NAME_REGEX, message = DomainConstants.USER_FIRST_NAME_FORMAT)
    private String firstName;

    @Size(min = DomainConstants.USER_LAST_NAME_MIN_LENGTH,
            max = DomainConstants.USER_LAST_NAME_MAX_LENGTH,
            message = DomainConstants.USER_LAST_NAME_LENGTH)
    @Pattern(regexp = DomainConstants.NAME_REGEX, message = DomainConstants.USER_LAST_NAME_FORMAT)
    private String lastName;

    @Email(message = DomainConstants.USER_EMAIL_FORMAT)
    @Size(max = DomainConstants.USER_EMAIL_MAX_LENGTH, message = DomainConstants.USER_EMAIL_LENGTH)
    private String email;

    // Constructor vacío
    public UpdateUserRequest() {
    }

    // Constructor completo
    public UpdateUserRequest(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
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

    @Override
    public String toString() {
        return "UpdateUserRequest{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}