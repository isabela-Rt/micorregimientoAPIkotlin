package com.micorregimiento.micorregimiento.Auth.Login.controllers;

import com.micorregimiento.micorregimiento.Auth.Login.interfaces.ILoginService;
import com.micorregimiento.micorregimiento.Auth.Login.entitys.LoginRequest;
import com.micorregimiento.micorregimiento.Auth.Login.entitys.LoginResponse;
import com.micorregimiento.micorregimiento.Auth.Login.entitys.TokenValidationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoginController {

    private final ILoginService loginService;

    @Autowired
    public LoginController(ILoginService loginService) {
        this.loginService = loginService;
    }

    public LoginResponse handleLogin(LoginRequest request) {
        return loginService.authenticateUser(request);
    }

    public LoginResponse handleRefreshToken(String refreshToken) {
        return loginService.refreshToken(refreshToken);
    }

    public TokenValidationResponse handleTokenValidation(String token) {
        return loginService.validateToken(token);
    }

    public boolean handleLogout(String token) {
        return loginService.logout(token);
    }
}
