package com.micorregimiento.micorregimiento.Auth.Login.endpoints;

import com.micorregimiento.micorregimiento.Auth.Login.controllers.LoginController;
import com.micorregimiento.micorregimiento.Auth.Login.entitys.LoginRequest;
import com.micorregimiento.micorregimiento.Auth.Login.entitys.LoginResponse;
import com.micorregimiento.micorregimiento.Auth.Login.entitys.TokenValidationResponse;
import com.micorregimiento.micorregimiento.Generics.interfaces.IJwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/login")
@Validated
public class LoginEndpoint {

    private final LoginController loginController;
    private final IJwtService jwtService;

    @Autowired
    public LoginEndpoint(LoginController loginController, IJwtService jwtService) {
        this.loginController = loginController;
        this.jwtService = jwtService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = loginController.handleLogin(request);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("success", response.isExitoso());
            responseBody.put("message", response.getMensaje());

            if (response.isExitoso()) {
                responseBody.put("data", response);
                return ResponseEntity.ok(responseBody);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
            }

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error interno del servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestBody Map<String, String> request) {
        try {
            String refreshToken = request.get("refreshToken");
            if (refreshToken == null || refreshToken.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Refresh token es requerido");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            LoginResponse response = loginController.handleRefreshToken(refreshToken);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("success", response.isExitoso());
            responseBody.put("message", response.getMensaje());

            if (response.isExitoso()) {
                responseBody.put("data", response);
                return ResponseEntity.ok(responseBody);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
            }

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error interno del servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Token de autorización requerido");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }

            String token = authHeader.substring(7);
            TokenValidationResponse validation = loginController.handleTokenValidation(token);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("success", validation.isValid());
            responseBody.put("message", validation.getMensaje());

            if (validation.isValid()) {
                responseBody.put("data", validation);
                return ResponseEntity.ok(responseBody);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
            }

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error interno del servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Token de autorización requerido");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }

            String token = authHeader.substring(7);
            boolean logoutSuccess = loginController.handleLogout(token);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("success", logoutSuccess);
            responseBody.put("message", logoutSuccess ? "Logout exitoso" : "Error durante el logout");

            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error interno del servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
