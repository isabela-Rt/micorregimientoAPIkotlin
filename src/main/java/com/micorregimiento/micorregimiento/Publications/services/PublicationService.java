package com.micorregimiento.micorregimiento.Publications.services;

import com.micorregimiento.micorregimiento.Config.WebhookConfig;
import com.micorregimiento.micorregimiento.Notifications.interfaces.IWebhookService;
import com.micorregimiento.micorregimiento.Publications.interfaces.IpublicationService;
import com.micorregimiento.micorregimiento.Publications.entitys.Epublications;
import com.micorregimiento.micorregimiento.Publications.entitys.Epublicationlocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class PublicationService implements IpublicationService {


    @Autowired
    private IWebhookService webhookService;

    @Autowired
    private WebhookConfig webhookConfig;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Epublications createPublicacion(Epublications publicacion, List<Long> barrioIds, List<Long> corregimientoIds) {
        // Ejecutar todas las validaciones antes de procesar
        validatePublicacionData(barrioIds, corregimientoIds);

        entityManager.persist(publicacion);
        entityManager.flush();

        // Crear ubicaciones para barrios
        if (barrioIds != null && !barrioIds.isEmpty()) {
            for (Long barrioId : barrioIds) {
                Long corregimientoId = getCorregimientoIdByBarrioId(barrioId);
                if (corregimientoId == null) {
                    throw new RuntimeException("El barrio " + barrioId + " no tiene un corregimiento válido asociado");
                }
                Epublicationlocation ubicacion = new Epublicationlocation();
                ubicacion.setPublicacionId(publicacion.getId());
                ubicacion.setBarrioId(barrioId);
                ubicacion.setCorregimientoId(corregimientoId);
                entityManager.persist(ubicacion);
            }
        }

        // Crear ubicaciones para corregimientos - USAR null en lugar de 0L
        if (corregimientoIds != null && !corregimientoIds.isEmpty()) {
            for (Long corregimientoId : corregimientoIds) {
                Epublicationlocation ubicacion = new Epublicationlocation();
                ubicacion.setPublicacionId(publicacion.getId());
                ubicacion.setBarrioId(null); // CAMBIADO: usar null en lugar de 0L
                ubicacion.setCorregimientoId(corregimientoId);
                entityManager.persist(ubicacion);
            }
        }

        // Después de crear la publicación, enviar al webhook
        if (webhookConfig.isWebhookEnabled()) {
            try {
                // Convertir la publicación a un formato adecuado para el webhook
                Map<String, Object> publicationData = Map.of(
                        "id", publicacion.getId(),
                        "titulo", publicacion.getTitulo(),
                        "contenido", publicacion.getContenido(),
                        "tipoPublicacionId", publicacion.getTipoPublicacionId(),
                        "autorId", publicacion.getAutorId(),
                        "fechaPublicacion", publicacion.getFechaPublicacion(),
                        "barrioIds", barrioIds != null ? barrioIds : Collections.emptyList(),
                        "corregimientoIds", corregimientoIds != null ? corregimientoIds : Collections.emptyList()
                );

                webhookService.sendToWebhook(
                        webhookConfig.getN8nWebhookUrl(),
                        publicationData,
                        "publication_created"
                );
            } catch (Exception e) {
                // No fallar la creación si el webhook falla, solo loggear
                System.err.println("Error sending to webhook: " + e.getMessage());
            }
        }

        return publicacion;
    }

    @Override
    @Transactional
    public Epublications updatePublicacion(Long id, Epublications publicacion, List<Long> barrioIds, List<Long> corregimientoIds) {
        Epublications existing = entityManager.find(Epublications.class, id);
        if (existing == null) {
            throw new RuntimeException("Publicación no encontrada con ID: " + id);
        }

        // Ejecutar todas las validaciones antes de procesar
        validatePublicacionData(barrioIds, corregimientoIds);

        existing.setTitulo(publicacion.getTitulo());
        existing.setContenido(publicacion.getContenido());
        existing.setTipoPublicacionId(publicacion.getTipoPublicacionId());

        // Eliminar ubicaciones existentes
        String deleteJpql = "DELETE FROM Epublicationlocation pu WHERE pu.publicacionId = :publicacionId";
        entityManager.createQuery(deleteJpql)
                .setParameter("publicacionId", id)
                .executeUpdate();

        // Crear nuevas ubicaciones para barrios
        if (barrioIds != null && !barrioIds.isEmpty()) {
            for (Long barrioId : barrioIds) {
                Long corregimientoId = getCorregimientoIdByBarrioId(barrioId);
                if (corregimientoId == null) {
                    throw new RuntimeException("El barrio " + barrioId + " no tiene un corregimiento válido asociado");
                }
                Epublicationlocation ubicacion = new Epublicationlocation();
                ubicacion.setPublicacionId(id);
                ubicacion.setBarrioId(barrioId);
                ubicacion.setCorregimientoId(corregimientoId);
                entityManager.persist(ubicacion);
            }
        }

        // Crear nuevas ubicaciones para corregimientos - USAR null
        if (corregimientoIds != null && !corregimientoIds.isEmpty()) {
            for (Long corregimientoId : corregimientoIds) {
                Epublicationlocation ubicacion = new Epublicationlocation();
                ubicacion.setPublicacionId(id);
                ubicacion.setBarrioId(null); // CAMBIADO: usar null en lugar de 0L
                ubicacion.setCorregimientoId(corregimientoId);
                entityManager.persist(ubicacion);
            }
        }

        return entityManager.merge(existing);
    }

    @Override
    @Transactional
    public boolean deactivatePublicacion(Long id) {
        Epublications publicacion = entityManager.find(Epublications.class, id);
        if (publicacion == null) {
            return false;
        }
        entityManager.remove(publicacion);
        return true;
    }

    @Override
    public Epublications getPublicacionById(Long id) {
        return entityManager.find(Epublications.class, id);
    }

    @Override
    public List<Epublications> getPublicacionesByUserId(Long userId) {
        String jpql = "SELECT p FROM Epublications p WHERE p.autorId = :userId ORDER BY p.fechaPublicacion DESC";
        TypedQuery<Epublications> query = entityManager.createQuery(jpql, Epublications.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    public List<Epublications> getPublicacionesByBarrioId(Long barrioId) {
        // Primero obtener el corregimiento del barrio
        Long corregimientoId = getCorregimientoIdByBarrioId(barrioId);
        if (corregimientoId == null) {
            throw new RuntimeException("El barrio " + barrioId + " no tiene un corregimiento válido asociado");
        }

        String jpql = """
            SELECT DISTINCT p FROM Epublications p 
            JOIN Epublicationlocation pu ON p.id = pu.publicacionId 
            WHERE (pu.barrioId = :barrioId) OR 
                  (pu.barrioId IS NULL AND pu.corregimientoId = :corregimientoId)
            ORDER BY p.fechaPublicacion DESC
            """;
        TypedQuery<Epublications> query = entityManager.createQuery(jpql, Epublications.class);
        query.setParameter("barrioId", barrioId);
        query.setParameter("corregimientoId", corregimientoId);
        return query.getResultList();
    }

    @Override
    public List<Epublications> getPublicacionesByCorregimientoId(Long corregimientoId) {
        String jpql = """
            SELECT DISTINCT p FROM Epublications p 
            JOIN Epublicationlocation pu ON p.id = pu.publicacionId 
            WHERE pu.corregimientoId = :corregimientoId 
            ORDER BY p.fechaPublicacion DESC
            """;
        TypedQuery<Epublications> query = entityManager.createQuery(jpql, Epublications.class);
        query.setParameter("corregimientoId", corregimientoId);
        return query.getResultList();
    }

    @Override
    public List<Epublicationlocation> getUbicacionesByPublicacionId(Long publicacionId) {
        String jpql = "SELECT pu FROM Epublicationlocation pu WHERE pu.publicacionId = :publicacionId";
        TypedQuery<Epublicationlocation> query = entityManager.createQuery(jpql, Epublicationlocation.class);
        query.setParameter("publicacionId", publicacionId);
        return query.getResultList();
    }

    // MÉTODO CENTRALIZADO PARA TODAS LAS VALIDACIONES
    private void validatePublicacionData(List<Long> barrioIds, List<Long> corregimientoIds) {
        // Validar que al menos una ubicación esté especificada
        if ((barrioIds == null || barrioIds.isEmpty()) &&
                (corregimientoIds == null || corregimientoIds.isEmpty())) {
            throw new RuntimeException("Debe especificar al menos un barrio o corregimiento");
        }

        // Validar que los barrios existan
        if (barrioIds != null && !barrioIds.isEmpty()) {
            List<Long> barriosInvalidos = validateBarriosExist(barrioIds);
            if (!barriosInvalidos.isEmpty()) {
                throw new RuntimeException("Los siguientes barrios no existen: " + barriosInvalidos);
            }
        }

        // Validar que los corregimientos existan
        if (corregimientoIds != null && !corregimientoIds.isEmpty()) {
            List<Long> corregimientosInvalidos = validateCorregimientosExist(corregimientoIds);
            if (!corregimientosInvalidos.isEmpty()) {
                throw new RuntimeException("Los siguientes corregimientos no existen: " + corregimientosInvalidos);
            }
        }

        // Validar coherencia entre barrios y corregimientos
        if (barrioIds != null && !barrioIds.isEmpty() &&
                corregimientoIds != null && !corregimientoIds.isEmpty()) {
            String incoherencia = validateBarrioCorregimientoCoherence(barrioIds, corregimientoIds);
            if (incoherencia != null) {
                throw new RuntimeException(incoherencia);
            }
        }
    }

    // Método para validar que los barrios existan
    private List<Long> validateBarriosExist(List<Long> barrioIds) {
        List<Long> barriosInvalidos = new ArrayList<>();

        for (Long barrioId : barrioIds) {
            String jpql = "SELECT COUNT(b) FROM Eneighborhoods b WHERE b.id = :barrioId";
            TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
            query.setParameter("barrioId", barrioId);
            Long count = query.getSingleResult();

            if (count == 0) {
                barriosInvalidos.add(barrioId);
            }
        }

        return barriosInvalidos;
    }

    // Método para validar que los corregimientos existan
    private List<Long> validateCorregimientosExist(List<Long> corregimientoIds) {
        List<Long> corregimientosInvalidos = new ArrayList<>();

        for (Long corregimientoId : corregimientoIds) {
            String jpql = "SELECT COUNT(c) FROM Ecorregimientos c WHERE c.id = :corregimientoId";
            TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
            query.setParameter("corregimientoId", corregimientoId);
            Long count = query.getSingleResult();

            if (count == 0) {
                corregimientosInvalidos.add(corregimientoId);
            }
        }

        return corregimientosInvalidos;
    }

    // Método para validar coherencia entre barrios y corregimientos
    private String validateBarrioCorregimientoCoherence(List<Long> barrioIds, List<Long> corregimientoIds) {
        for (Long barrioId : barrioIds) {
            // Obtener el corregimiento al que pertenece este barrio
            Long barrioCorregimientoId = getCorregimientoIdByBarrioId(barrioId);

            if (barrioCorregimientoId == null) {
                return String.format("El barrio %d no existe o no tiene corregimiento asociado", barrioId);
            }

            // Verificar si el corregimiento del barrio está en la lista de corregimientos especificados
            if (!corregimientoIds.contains(barrioCorregimientoId)) {
                return String.format(
                        "El barrio %d pertenece al corregimiento %d, pero se especificó para los corregimientos %s. " +
                                "Los barrios deben pertenecer a los corregimientos indicados.",
                        barrioId, barrioCorregimientoId, corregimientoIds
                );
            }
        }
        return null; // Todo es coherente
    }

    @Override
    public List<Epublications> getAllPublicaciones() {
        String jpql = "SELECT p FROM Epublications p ORDER BY p.fechaPublicacion DESC";
        TypedQuery<Epublications> query = entityManager.createQuery(jpql, Epublications.class);
        return query.getResultList();
    }

    private Long getCorregimientoIdByBarrioId(Long barrioId) {
        try {
            String jpql = "SELECT b.corregimientoId FROM Eneighborhoods b WHERE b.id = :barrioId";
            TypedQuery<Integer> query = entityManager.createQuery(jpql, Integer.class);
            query.setParameter("barrioId", barrioId);
            List<Integer> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0).longValue();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener corregimiento para barrio: " + barrioId, e);
        }
    }
}

