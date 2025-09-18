package com.micorregimiento.micorregimiento.Publications.interfaces;

import com.micorregimiento.micorregimiento.Publications.entitys.response.CreatePublicacionRequest;
import com.micorregimiento.micorregimiento.Publications.entitys.response.UpdatePublicacionRequest;
import com.micorregimiento.micorregimiento.Publications.entitys.response.PublicacionResponse;
import java.util.List;

public interface IpublicationController {
    PublicacionResponse createPublicacion(CreatePublicacionRequest request, Long userId);
    PublicacionResponse updatePublicacion(Long id, UpdatePublicacionRequest request, Long userId);
    PublicacionResponse deactivatePublicacion(Long id, Long userId);
    PublicacionResponse getPublicacion(Long id);
    List<PublicacionResponse> getMyPublicaciones(Long userId);
    List<PublicacionResponse> getPublicacionesByBarrio(Long barrioId);
    List<PublicacionResponse> getAllPublicaciones();
}
