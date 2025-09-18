package com.micorregimiento.micorregimiento.Generics.services;

import com.micorregimiento.micorregimiento.Generics.interfaces.IPrivilegeService;
import com.micorregimiento.micorregimiento.Privileges.entitys.Privilege;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;

@Service
public class PrivilegeService implements IPrivilegeService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Privilege> getPrivilegesByUserId(Long userId) {
        // Privilegios directos del usuario
        String jpqlDirect = "SELECT p FROM Privilege p JOIN UserPrivilege up ON p.id = up.privilegioId WHERE up.usuarioId = :userId";
        TypedQuery<Privilege> directQuery = entityManager.createQuery(jpqlDirect, Privilege.class);
        directQuery.setParameter("userId", userId);
        List<Privilege> directPrivileges = directQuery.getResultList();

        // Privilegios a través de roles
        String jpqlThroughRoles = """
            SELECT DISTINCT p FROM Privilege p 
            JOIN RolePrivilege rp ON p.id = rp.privilegioId 
            JOIN UserRole ur ON rp.rolId = ur.rolId 
            WHERE ur.usuarioId = :userId
        """;
        TypedQuery<Privilege> roleQuery = entityManager.createQuery(jpqlThroughRoles, Privilege.class);
        roleQuery.setParameter("userId", userId);
        List<Privilege> rolePrivileges = roleQuery.getResultList();

        // Combinar ambas listas y eliminar duplicados
        directPrivileges.addAll(rolePrivileges);
        return directPrivileges.stream().distinct().collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<Privilege> getPrivilegesByUserIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }

        // Privilegios directos
        String jpqlDirect = "SELECT DISTINCT p FROM Privilege p JOIN UserPrivilege up ON p.id = up.privilegioId WHERE up.usuarioId IN :userIds";
        TypedQuery<Privilege> directQuery = entityManager.createQuery(jpqlDirect, Privilege.class);
        directQuery.setParameter("userIds", userIds);
        List<Privilege> directPrivileges = directQuery.getResultList();

        // Privilegios a través de roles
        String jpqlThroughRoles = """
            SELECT DISTINCT p FROM Privilege p 
            JOIN RolePrivilege rp ON p.id = rp.privilegioId 
            JOIN UserRole ur ON rp.rolId = ur.rolId 
            WHERE ur.usuarioId IN :userIds
        """;
        TypedQuery<Privilege> roleQuery = entityManager.createQuery(jpqlThroughRoles, Privilege.class);
        roleQuery.setParameter("userIds", userIds);
        List<Privilege> rolePrivileges = roleQuery.getResultList();

        // Combinar y eliminar duplicados
        directPrivileges.addAll(rolePrivileges);
        return directPrivileges.stream().distinct().collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<Privilege> getAllPrivileges() {
        String jpql = "SELECT p FROM Privilege p ORDER BY p.nombre";
        TypedQuery<Privilege> query = entityManager.createQuery(jpql, Privilege.class);
        return query.getResultList();
    }
}
