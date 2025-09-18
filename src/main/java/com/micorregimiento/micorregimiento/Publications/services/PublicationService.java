package com.micorregimiento.micorregimiento.Publications.services;

import com.micorregimiento.micorregimiento.Publications.interfaces.IpublicationService;
import com.micorregimiento.micorregimiento.Publications.entitys.Epublications;
import com.micorregimiento.micorregimiento.Publications.entitys.Epublicationlocation;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;

@Service
public class PublicationService implements IpublicationService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Epublications createPublicacion(Epublications publicacion, List<Long> barrioIds, List<Long> corregimientoIds) {
        entityManager.persist(publicacion);
        entityManager.flush();

        // Crear ubicaciones
        if (barrioIds != null && !barrioIds.isEmpty()) {
            for (Long barrioId : barrioIds) {
                Long corregimientoId = getCorregimientoIdByBarrioId(barrioId);
                Epublicationlocation ubicacion = new Epublicationlocation();
                ubicacion.setPublicacionId(publicacion.getId());
                ubicacion.setBarrioId(barrioId);
                ubicacion.setCorregimientoId(corregimientoId);
                entityManager.persist(ubicacion);
            }
        }

        if (corregimientoIds != null && !corregimientoIds.isEmpty()) {
            for (Long corregimientoId : corregimientoIds) {
                Epublicationlocation ubicacion = new Epublicationlocation();
                ubicacion.setPublicacionId(publicacion.getId());
                ubicacion.setBarrioId(0L); // 0 indica que es para todo el corregimiento
                ubicacion.setCorregimientoId(corregimientoId);
                entityManager.persist(ubicacion);
            }
        }

        return publicacion;
    }

    @Override
    @Transactional
    public Epublications updatePublicacion(Long id, Epublications publicacion, List<Long> barrioIds, List<Long> corregimientoIds) {
        Epublications existing = entityManager.find(Epublications.class, id);
        if (existing == null) {
            return null;
        }

        existing.setTitulo(publicacion.getTitulo());
        existing.setContenido(publicacion.getContenido());
        existing.setTipoPublicacionId(publicacion.getTipoPublicacionId());

        // Eliminar ubicaciones existentes
        String deleteJpql = "DELETE FROM Epublicationlocation pu WHERE pu.publicacionId = :publicacionId";
        entityManager.createQuery(deleteJpql)
                .setParameter("publicacionId", id)
                .executeUpdate();

        // Crear nuevas ubicaciones
        if (barrioIds != null && !barrioIds.isEmpty()) {
            for (Long barrioId : barrioIds) {
                Long corregimientoId = getCorregimientoIdByBarrioId(barrioId);
                Epublicationlocation ubicacion = new Epublicationlocation();
                ubicacion.setPublicacionId(id);
                ubicacion.setBarrioId(barrioId);
                ubicacion.setCorregimientoId(corregimientoId);
                entityManager.persist(ubicacion);
            }
        }

        if (corregimientoIds != null && !corregimientoIds.isEmpty()) {
            for (Long corregimientoId : corregimientoIds) {
                Epublicationlocation ubicacion = new Epublicationlocation();
                ubicacion.setPublicacionId(id);
                ubicacion.setBarrioId(0L);
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
        String jpql = """
            SELECT DISTINCT p FROM Epublications p 
            JOIN Epublicationlocation pu ON p.id = pu.publicacionId 
            WHERE pu.barrioId = :barrioId OR (pu.barrioId = 0 AND pu.corregimientoId = 
                (SELECT b.corregimientoId FROM Eneighborhoods b WHERE b.id = :barrioId))
            ORDER BY p.fechaPublicacion DESC
            """;
        TypedQuery<Epublications> query = entityManager.createQuery(jpql, Epublications.class);
        query.setParameter("barrioId", barrioId);
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

    private Long getCorregimientoIdByBarrioId(Long barrioId) {
        String jpql = "SELECT b.corregimientoId FROM Eneighborhoods b WHERE b.id = :barrioId";
        TypedQuery<Integer> query = entityManager.createQuery(jpql, Integer.class);
        query.setParameter("barrioId", barrioId);
        List<Integer> results = query.getResultList();
        return results.isEmpty() ? 0L : results.get(0).longValue();
    }
}

