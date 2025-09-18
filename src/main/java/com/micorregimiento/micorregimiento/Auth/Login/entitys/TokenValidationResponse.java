package com.micorregimiento.micorregimiento.Auth.Login.entitys;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenValidationResponse {
    private Long userId;
    private String email;
    private List<String> roles;
    private List<String> privilegios;
    private List<Long> barrioIds;
    private boolean valid;
    private String mensaje;
}

