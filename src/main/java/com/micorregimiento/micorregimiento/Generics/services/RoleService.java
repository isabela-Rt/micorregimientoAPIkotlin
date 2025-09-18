package com.micorregimiento.micorregimiento.Generics.services;

import com.micorregimiento.micorregimiento.Generics.interfaces.IRoleService;
import com.micorregimiento.micorregimiento.Roles.entitys.Role;
import com.micorregimiento.micorregimiento.Roles.entitys.RolePrivilege;
import com.micorregimiento.micorregimiento.Users.entitys.UserRole;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService implements IRoleService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Role> getRolesByUserId(Long userId) {
        String jpql = "SELECT r FROM Role r JOIN UserRole ur ON r.id = ur.rolId WHERE ur.usuarioId = :userId";
        TypedQuery<Role> query = entityManager.createQuery(jpql, Role.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    public List<Role> getRolesByUserIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }

        String jpql = "SELECT DISTINCT r FROM Role r JOIN UserRole ur ON r.id = ur.rolId WHERE ur.usuarioId IN :userIds";
        TypedQuery<Role> query = entityManager.createQuery(jpql, Role.class);
        query.setParameter("userIds", userIds);
        return query.getResultList();
    }

    @Override
    public List<Role> getAllRoles() {
        String jpql = "SELECT r FROM Role r ORDER BY r.nombre";
        TypedQuery<Role> query = entityManager.createQuery(jpql, Role.class);
        return query.getResultList();
    }
}
