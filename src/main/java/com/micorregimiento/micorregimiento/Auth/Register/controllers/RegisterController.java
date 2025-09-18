package com.micorregimiento.micorregimiento.Auth.Register.controllers;

import com.micorregimiento.micorregimiento.Auth.Register.interfaces.IRegisterService;
import com.micorregimiento.micorregimiento.Auth.Register.entitys.PublicRegisterRequest;
import com.micorregimiento.micorregimiento.Auth.Register.entitys.AdminRegisterRequest;
import com.micorregimiento.micorregimiento.Auth.Register.entitys.RegisterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RegisterController {

    private final IRegisterService registerService;

    @Autowired
    public RegisterController(IRegisterService registerService) {
        this.registerService = registerService;
    }

    public RegisterResponse handlePublicRegister(PublicRegisterRequest request) {
        return registerService.registerPublicUser(request);
    }

    public RegisterResponse handleAdminRegister(AdminRegisterRequest request, Long adminUserId) {
        return registerService.registerUserByAdmin(request, adminUserId);
    }
}
