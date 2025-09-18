package com.micorregimiento.micorregimiento.Publications.controllers;

import com.micorregimiento.micorregimiento.Publications.entitys.response.CreatePublicacionRequest;
import com.micorregimiento.micorregimiento.Publications.entitys.response.PublicacionResponse;
import com.micorregimiento.micorregimiento.Publications.entitys.response.UpdatePublicacionRequest;
import com.micorregimiento.micorregimiento.Publications.interfaces.IpublicationController;
import com.micorregimiento.micorregimiento.Publications.interfaces.IpublicationService;
import com.micorregimiento.micorregimiento.Publications.entitys.Epublications;
import com.micorregimiento.micorregimiento.Publications.entitys.Epublicationlocation;
import com.micorregimiento.micorregimiento.Publications.entitys.*;
import com.micorregimiento.micorregimiento.Generics.interfaces.IPrivilegeValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
public class PublicacionController implements IpublicationController {

    private final IpublicationService publicacionService;
    private final IPrivilegeValidationService privilegeValidationService;

    @Autowired
    public PublicacionController(IpublicationService publicacionService,
                                 IPrivilegeValidationService privilegeValidationService) {
        this.publicacionService = publicacionService;
        this.privilegeValidationService = privilegeValidationService;
    }

    @Override
    public PublicacionResponse createPublicacion(CreatePublicacionRequest request, Long userId) {
        try {
            // Validar permisos
            if (!privilegeValidationService.canCreatePost(userId)) {
                return PublicacionResponse.builder()
                        .exitoso(false)
                        .mensaje("No tienes permisos para crear publicaciones")
                        .build();
            }

            Epublications publicacion = new Epublications();
            publicacion.setTitulo(request.getTitulo());
            publicacion.setContenido(request.getContenido());
            publicacion.setTipoPublicacionId(request.getTipoPublicacionId());
            publicacion.setAutorId(userId);

            Epublications created = publicacionService.createPublicacion(
                    publicacion, request.getBarrioIds(), request.getCorregimientoIds());

            return buildPublicacionResponse(created, true, "Publicación creada exitosamente");

        } catch (Exception e) {
            return PublicacionResponse.builder()
                    .exitoso(false)
                    .mensaje("Error al crear publicación: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public PublicacionResponse updatePublicacion(Long id, UpdatePublicacionRequest request, Long userId) {
        try {
            // Validar permisos
            if (!privilegeValidationService.canEditPost(userId)) {
                return PublicacionResponse.builder()
                        .exitoso(false)
                        .mensaje("No tienes permisos para editar publicaciones")
                        .build();
            }

            // Verificar que la publicación existe
            Epublications existing = publicacionService.getPublicacionById(id);
            if (existing == null) {
                return PublicacionResponse.builder()
                        .exitoso(false)
                        .mensaje("Publicación no encontrada")
                        .build();
            }

            // Verificar que es el autor o es admin
            boolean isAdmin = privilegeValidationService.hasSpecificPrivilege(userId, "admin");
            if (!isAdmin && !existing.getAutorId().equals(userId)) {
                return PublicacionResponse.builder()
                        .exitoso(false)
                        .mensaje("Solo puedes editar tus propias publicaciones")
                        .build();
            }

            Epublications publicacion = new Epublications();
            publicacion.setTitulo(request.getTitulo());
            publicacion.setContenido(request.getContenido());
            publicacion.setTipoPublicacionId(request.getTipoPublicacionId());

            Epublications updated = publicacionService.updatePublicacion(
                    id, publicacion, request.getBarrioIds(), request.getCorregimientoIds());

            return buildPublicacionResponse(updated, true, "Publicación actualizada exitosamente");

        } catch (Exception e) {
            return PublicacionResponse.builder()
                    .exitoso(false)
                    .mensaje("Error al actualizar publicación: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public PublicacionResponse deactivatePublicacion(Long id, Long userId) {
        try {
            // Validar permisos
            if (!privilegeValidationService.canDeletePost(userId)) {
                return PublicacionResponse.builder()
                        .exitoso(false)
                        .mensaje("No tienes permisos para eliminar publicaciones")
                        .build();
            }

            // Verificar que la publicación existe
            Epublications existing = publicacionService.getPublicacionById(id);
            if (existing == null) {
                return PublicacionResponse.builder()
                        .exitoso(false)
                        .mensaje("Publicación no encontrada")
                        .build();
            }

            // Verificar que es el autor o es admin
            boolean isAdmin = privilegeValidationService.hasSpecificPrivilege(userId, "admin");
            if (!isAdmin && !existing.getAutorId().equals(userId)) {
                return PublicacionResponse.builder()
                        .exitoso(false)
                        .mensaje("Solo puedes eliminar tus propias publicaciones")
                        .build();
            }

            boolean deactivated = publicacionService.deactivatePublicacion(id);
            if (deactivated) {
                return PublicacionResponse.builder()
                        .exitoso(true)
                        .mensaje("Publicación eliminada exitosamente")
                        .build();
            } else {
                return PublicacionResponse.builder()
                        .exitoso(false)
                        .mensaje("Error al eliminar publicación")
                        .build();
            }

        } catch (Exception e) {
            return PublicacionResponse.builder()
                    .exitoso(false)
                    .mensaje("Error al eliminar publicación: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public PublicacionResponse getPublicacion(Long id) {
        try {
            Epublications publicacion = publicacionService.getPublicacionById(id);
            if (publicacion == null) {
                return PublicacionResponse.builder()
                        .exitoso(false)
                        .mensaje("Publicación no encontrada")
                        .build();
            }

            return buildPublicacionResponse(publicacion, true, "Publicación encontrada");

        } catch (Exception e) {
            return PublicacionResponse.builder()
                    .exitoso(false)
                    .mensaje("Error al obtener publicación: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public List<PublicacionResponse> getMyPublicaciones(Long userId) {
        try {
            List<Epublications> publicaciones = publicacionService.getPublicacionesByUserId(userId);
            return publicaciones.stream()
                    .map(p -> buildPublicacionResponse(p, true, ""))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            return List.of(PublicacionResponse.builder()
                    .exitoso(false)
                    .mensaje("Error al obtener publicaciones: " + e.getMessage())
                    .build());
        }
    }

    @Override
    public List<PublicacionResponse> getPublicacionesByBarrio(Long barrioId) {
        try {
            List<Epublications> publicaciones = publicacionService.getPublicacionesByBarrioId(barrioId);
            return publicaciones.stream()
                    .map(p -> buildPublicacionResponse(p, true, ""))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            return List.of(PublicacionResponse.builder()
                    .exitoso(false)
                    .mensaje("Error al obtener publicaciones: " + e.getMessage())
                    .build());
        }
    }

    private PublicacionResponse buildPublicacionResponse(Epublications publicacion, boolean exitoso, String mensaje) {
        List<Epublicationlocation> ubicaciones = publicacionService.getUbicacionesByPublicacionId(publicacion.getId());

        List<Long> barrioIds = ubicaciones.stream()
                .map(Epublicationlocation::getBarrioId)
                .filter(b -> b != null) // solo barrios válidos
                .distinct()
                .collect(Collectors.toList());

        List<Long> corregimientoIds = ubicaciones.stream()
                .filter(u -> u.getBarrioId() == null) // corregimientos puros
                .map(Epublicationlocation::getCorregimientoId)
                .filter(c -> c != null) // seguridad extra
                .distinct()
                .collect(Collectors.toList());

        return PublicacionResponse.builder()
                .id(publicacion.getId())
                .titulo(publicacion.getTitulo())
                .contenido(publicacion.getContenido())
                .tipoPublicacionId(publicacion.getTipoPublicacionId())
                .fechaPublicacion(publicacion.getFechaPublicacion())
                .autorId(publicacion.getAutorId())
                .barrioIds(barrioIds)
                .corregimientoIds(corregimientoIds)
                .exitoso(exitoso)
                .mensaje(mensaje)
                .build();
    }

    @Override
    public List<PublicacionResponse> getAllPublicaciones() {
        try {
            List<Epublications> publicaciones = publicacionService.getAllPublicaciones();
            return publicaciones.stream()
                    .map(p -> buildPublicacionResponse(p, true, ""))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            return List.of(PublicacionResponse.builder()
                    .exitoso(false)
                    .mensaje("Error al obtener todas las publicaciones: " + e.getMessage())
                    .build());
        }
    }
}

