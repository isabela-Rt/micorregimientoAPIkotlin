package com.micorregimiento.micorregimiento.Auth.Login.entitys;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private Long userId;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String accessToken;
    private String refreshToken;
    private LocalDateTime tokenExpiration;
    private List<String> roles;
    private List<String> privilegios;
    private List<Long> barrioIds;
    private String mensaje;
    private boolean exitoso;
    private long expiresIn; // segundos hasta la expiración
}
