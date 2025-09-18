package com.micorregimiento.micorregimiento.Auth.Register.interfaces;

import com.micorregimiento.micorregimiento.Auth.Register.entitys.PublicRegisterRequest;
import com.micorregimiento.micorregimiento.Auth.Register.entitys.AdminRegisterRequest;
import com.micorregimiento.micorregimiento.Auth.Register.entitys.RegisterResponse;

public interface IRegisterService {
    RegisterResponse registerPublicUser(PublicRegisterRequest request);
    RegisterResponse registerUserByAdmin(AdminRegisterRequest request, Long adminUserId);
}
