package com.anborja.tucarro.infrastructure.driving.http.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/test") // Sin /api porque ya está en context-path
public class TestController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Test endpoint working");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hash")
    public ResponseEntity<Map<String, Object>> testHash() {
        PasswordEncoder encoder = new BCryptPasswordEncoder(12);
        String hash = encoder.encode("password123");

        Map<String, Object> response = new HashMap<>();
        response.put("originalPassword", "password123");
        response.put("hash", hash);
        response.put("matches", encoder.matches("password123", hash));

        return ResponseEntity.ok(response);
    }
}