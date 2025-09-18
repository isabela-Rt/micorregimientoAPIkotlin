package com.micorregimiento.micorregimiento.Publications.interfaces;

import com.micorregimiento.micorregimiento.Publications.entitys.Epublications;
import com.micorregimiento.micorregimiento.Publications.entitys.Epublicationlocation;
import java.util.List;

public interface IpublicationService {
    Epublications createPublicacion(Epublications publicacion, List<Long> barrioIds, List<Long> corregimientoIds);
    Epublications updatePublicacion(Long id, Epublications publicacion, List<Long> barrioIds, List<Long> corregimientoIds);
    boolean deactivatePublicacion(Long id);
    Epublications getPublicacionById(Long id);
    List<Epublications> getPublicacionesByUserId(Long userId);
    List<Epublications> getPublicacionesByBarrioId(Long barrioId);
    List<Epublications> getPublicacionesByCorregimientoId(Long corregimientoId);
    List<Epublicationlocation> getUbicacionesByPublicacionId(Long publicacionId); // CORREGIDO: debe retornar EPublicationLocation
    List<Epublications> getAllPublicaciones();
}
