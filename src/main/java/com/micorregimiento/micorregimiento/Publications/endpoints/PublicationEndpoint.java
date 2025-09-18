package com.micorregimiento.micorregimiento.Publications.endpoints;

import com.micorregimiento.micorregimiento.Publications.interfaces.IpublicationController;
import com.micorregimiento.micorregimiento.Publications.entitys.response.*;
import com.micorregimiento.micorregimiento.Auth.Login.entitys.TokenValidationResponse;
import com.micorregimiento.micorregimiento.Generics.interfaces.IJwtService;
import com.micorregimiento.micorregimiento.Publications.interfaces.IpublicationController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/publicaciones")
// REMOVER @CrossOrigin - Ya está manejado globalmente
public class PublicationEndpoint {

    private final IpublicationController publicacionController;
    private final IJwtService jwtService;

    @Autowired
    public PublicationEndpoint(IpublicationController publicacionController, IJwtService jwtService) {
        this.publicacionController = publicacionController;
        this.jwtService = jwtService;
    }

    @PostMapping("/create")
    public ResponseEntity<PublicacionResponse> createPublicacion(
            @RequestBody CreatePublicacionRequest request,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = extractUserIdFromToken(authHeader);
        if (userId == null) {
            return ResponseEntity.badRequest().body(
                    PublicacionResponse.builder()
                            .exitoso(false)
                            .mensaje("Token inválido")
                            .build()
            );
        }

        PublicacionResponse response = publicacionController.createPublicacion(request, userId);
        return response.isExitoso() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<PublicacionResponse> updatePublicacion(
            @PathVariable Long id,
            @RequestBody UpdatePublicacionRequest request,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = extractUserIdFromToken(authHeader);
        if (userId == null) {
            return ResponseEntity.badRequest().body(
                    PublicacionResponse.builder()
                            .exitoso(false)
                            .mensaje("Token inválido")
                            .build()
            );
        }

        PublicacionResponse response = publicacionController.updatePublicacion(id, request, userId);
        return response.isExitoso() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<PublicacionResponse> deactivatePublicacion(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = extractUserIdFromToken(authHeader);
        if (userId == null) {
            return ResponseEntity.badRequest().body(
                    PublicacionResponse.builder()
                            .exitoso(false)
                            .mensaje("Token inválido")
                            .build()
            );
        }

        PublicacionResponse response = publicacionController.deactivatePublicacion(id, userId);
        return response.isExitoso() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicacionResponse> getPublicacion(@PathVariable Long id) {
        PublicacionResponse response = publicacionController.getPublicacion(id);
        return response.isExitoso() ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }

    @GetMapping("/my-publicaciones")
    public ResponseEntity<List<PublicacionResponse>> getMyPublicaciones(
            @RequestHeader("Authorization") String authHeader) {

        Long userId = extractUserIdFromToken(authHeader);
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }

        List<PublicacionResponse> response = publicacionController.getMyPublicaciones(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/barrio/{barrioId}")
    public ResponseEntity<List<PublicacionResponse>> getPublicacionesByBarrio(@PathVariable Long barrioId) {
        List<PublicacionResponse> response = publicacionController.getPublicacionesByBarrio(barrioId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PublicacionResponse>> getAllPublicaciones() {
        List<PublicacionResponse> response = publicacionController.getAllPublicaciones();
        return ResponseEntity.ok(response);
    }

    private Long extractUserIdFromToken(String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            TokenValidationResponse validation = jwtService.validateToken(token);
            return validation.isValid() ? validation.getUserId() : null;
        } catch (Exception e) {
            return null;
        }
    }
}




