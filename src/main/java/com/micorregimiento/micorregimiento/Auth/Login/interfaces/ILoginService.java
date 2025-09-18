package com.micorregimiento.micorregimiento.Auth.Login.interfaces;

import com.micorregimiento.micorregimiento.Auth.Login.entitys.LoginRequest;
import com.micorregimiento.micorregimiento.Auth.Login.entitys.LoginResponse;
import com.micorregimiento.micorregimiento.Auth.Login.entitys.TokenValidationResponse;

public interface ILoginService {
    LoginResponse authenticateUser(LoginRequest request);
    LoginResponse refreshToken(String refreshToken);
    TokenValidationResponse validateToken(String token);
    boolean logout(String token);
}