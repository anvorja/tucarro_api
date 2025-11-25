package com.anborja.tucarro.infrastructure.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {

    private List<String> allowedOrigins;
    private List<String> allowedMethods;
    private List<String> allowedHeaders;

    // Constructor vacío
    public CorsProperties() {
    }

    // Getters y Setters
    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public List<String> getAllowedMethods() {
        return allowedMethods;
    }

    public void setAllowedMethods(List<String> allowedMethods) {
        this.allowedMethods = allowedMethods;
    }

    public List<String> getAllowedHeaders() {
        return allowedHeaders;
    }

    public void setAllowedHeaders(List<String> allowedHeaders) {
        this.allowedHeaders = allowedHeaders;
    }

    // Método de conveniencia para obtener como array
    public String[] getAllowedOriginsAsArray() {
        return allowedOrigins != null ? allowedOrigins.toArray(new String[0]) : new String[0];
    }

    public String[] getAllowedMethodsAsArray() {
        return allowedMethods != null ? allowedMethods.toArray(new String[0]) : new String[0];
    }

    public String[] getAllowedHeadersAsArray() {
        return allowedHeaders != null ? allowedHeaders.toArray(new String[0]) : new String[0];
    }
}