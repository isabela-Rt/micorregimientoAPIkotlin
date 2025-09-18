package com.micorregimiento.micorregimiento.Auth.Register.endpoints;

import com.micorregimiento.micorregimiento.Auth.Register.controllers.RegisterController;
import com.micorregimiento.micorregimiento.Auth.Register.entitys.PublicRegisterRequest;
import com.micorregimiento.micorregimiento.Auth.Register.entitys.AdminRegisterRequest;
import com.micorregimiento.micorregimiento.Auth.Register.entitys.RegisterResponse;
import com.micorregimiento.micorregimiento.Generics.interfaces.IRoleService;
import com.micorregimiento.micorregimiento.Generics.services.SecurityContextService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/register")
@Validated
public class RegisterEndpoint {


    private final RegisterController registerController;
    private final IRoleService roleService;
    private final SecurityContextService securityContextService;

    @Autowired
    public RegisterEndpoint(RegisterController registerController,
                                   IRoleService roleService,
                                   SecurityContextService securityContextService) {
        this.registerController = registerController;
        this.roleService = roleService;
        this.securityContextService = securityContextService;
    }

    @PostMapping("/public")
    public ResponseEntity<Map<String, Object>> registerPublicUser(@Valid @RequestBody PublicRegisterRequest request) {
        try {
            RegisterResponse response = registerController.handlePublicRegister(request);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("success", response.isExitoso());
            responseBody.put("message", response.getMensaje());

            if (response.isExitoso()) {
                responseBody.put("data", response);
                return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
            }

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error interno del servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/admin")
    public ResponseEntity<Map<String, Object>> registerUserByAdmin(
            @Valid @RequestBody AdminRegisterRequest request) {
        try {
            // Usar SecurityContextService para obtener el userId actual
            Long adminUserId = securityContextService.getCurrentUserId();

            if (adminUserId == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Usuario no autenticado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }

            RegisterResponse response = registerController.handleAdminRegister(request, adminUserId);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("success", response.isExitoso());
            responseBody.put("message", response.getMensaje());

            if (response.isExitoso()) {
                responseBody.put("data", response);
                return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
            }

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error interno del servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/roles")
    public ResponseEntity<Map<String, Object>> getAllRoles() {
        try {
            // Utilizar el servicio genérico para obtener todos los roles
            var roles = roleService.getAllRoles();

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("success", true);
            responseBody.put("message", "Roles obtenidos exitosamente");
            responseBody.put("data", roles);

            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al obtener los roles: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Método helper para extraer el userId de la autenticación
    // Este método debe ser implementado según tu sistema de JWT/autenticación
    private Long extractUserIdFromAuthentication(Authentication authentication) {
        // Placeholder - implementar según tu sistema de autenticación
        // Por ejemplo, si usas JWT, deberías extraer el userId del token
        // return jwtService.getUserIdFromToken(authentication.getCredentials().toString());

        // Por ahora retorno null para que sepas que esto debe ser implementado
        return null; // TODO: Implementar extracción de userId del JWT/token
    }
}
