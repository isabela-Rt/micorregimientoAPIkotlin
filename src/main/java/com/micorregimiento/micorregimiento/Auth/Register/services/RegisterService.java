package com.micorregimiento.micorregimiento.Auth.Register.services;

import com.micorregimiento.micorregimiento.Auth.Register.interfaces.IRegisterService;
import com.micorregimiento.micorregimiento.Auth.Register.entitys.PublicRegisterRequest;
import com.micorregimiento.micorregimiento.Auth.Register.entitys.AdminRegisterRequest;
import com.micorregimiento.micorregimiento.Auth.Register.entitys.RegisterResponse;
import com.micorregimiento.micorregimiento.Generics.interfaces.IRoleService;
import com.micorregimiento.micorregimiento.Generics.interfaces.IPrivilegeService;
import com.micorregimiento.micorregimiento.Generics.interfaces.IPermissionValidationService;
import com.micorregimiento.micorregimiento.Users.entitys.user;
import com.micorregimiento.micorregimiento.Users.entitys.UserRole;
import com.micorregimiento.micorregimiento.Users.entitys.UserPrivilege;
import com.micorregimiento.micorregimiento.Users.entitys.UserNeighborhood;
import com.micorregimiento.micorregimiento.Roles.entitys.Role;
import com.micorregimiento.micorregimiento.Privileges.entitys.Privilege;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegisterService implements IRegisterService {

    @PersistenceContext
    private EntityManager entityManager;

    private final PasswordEncoder passwordEncoder;
    private final IRoleService roleService;
    private final IPrivilegeService privilegeService;
    private final IPermissionValidationService permissionValidationService;

    @Autowired
    public RegisterService(PasswordEncoder passwordEncoder,
                           IRoleService roleService,
                           IPrivilegeService privilegeService,
                           IPermissionValidationService permissionValidationService) {
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.privilegeService = privilegeService;
        this.permissionValidationService = permissionValidationService;
    }

    @Override
    @Transactional
    public RegisterResponse registerPublicUser(PublicRegisterRequest request) {
        try {
            // Verificar si el email ya existe
            if (emailExists(request.getEmail())) {
                return RegisterResponse.builder()
                        .exitoso(false)
                        .mensaje("El email ya está registrado")
                        .build();
            }

            // Crear usuario
            user newUser = new user();
            newUser.setNombre(request.getNombre());
            newUser.setApellido(request.getApellido());
            newUser.setEmail(request.getEmail());
            newUser.setTelefono(request.getTelefono());
            newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));

            entityManager.persist(newUser);
            entityManager.flush(); // Para obtener el ID

            // Asignar rol "usuario" por defecto
            Role userRole = getUserRole();
            if (userRole != null) {
                assignRoleToUser(newUser.getId(), userRole.getId());
            }

            // Asignar barrios si se proporcionaron
            if (request.getBarrioIds() != null && !request.getBarrioIds().isEmpty()) {
                assignNeighborhoodsToUser(newUser.getId(), request.getBarrioIds());
            }

            return buildSuccessResponse(newUser);

        } catch (Exception e) {
            return RegisterResponse.builder()
                    .exitoso(false)
                    .mensaje("Error al registrar usuario: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional
    public RegisterResponse registerUserByAdmin(AdminRegisterRequest request, Long adminUserId) {
        try {
            // Verificar que el usuario administrativo tenga permisos de admin
            if (!permissionValidationService.userHasRole(adminUserId, "admin")) {
                return RegisterResponse.builder()
                        .exitoso(false)
                        .mensaje("No tienes permisos de administrador para realizar esta operación")
                        .build();
            }

            // Verificar si el email ya existe
            if (emailExists(request.getEmail())) {
                return RegisterResponse.builder()
                        .exitoso(false)
                        .mensaje("El email ya está registrado")
                        .build();
            }

            // Crear usuario
            user newUser = new user();
            newUser.setNombre(request.getNombre());
            newUser.setApellido(request.getApellido());
            newUser.setEmail(request.getEmail());
            newUser.setTelefono(request.getTelefono());
            newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));

            entityManager.persist(newUser);
            entityManager.flush(); // Para obtener el ID

            // Asignar roles especificados por el admin
            if (request.getRolIds() != null && !request.getRolIds().isEmpty()) {
                for (Long rolId : request.getRolIds()) {
                    assignRoleToUser(newUser.getId(), rolId);
                }
            }

            // Asignar privilegios adicionales si se proporcionaron
            if (request.getPrivilegioIds() != null && !request.getPrivilegioIds().isEmpty()) {
                for (Long privilegioId : request.getPrivilegioIds()) {
                    assignPrivilegeToUser(newUser.getId(), privilegioId);
                }
            }

            // Asignar barrios si se proporcionaron
            if (request.getBarrioIds() != null && !request.getBarrioIds().isEmpty()) {
                assignNeighborhoodsToUser(newUser.getId(), request.getBarrioIds());
            }

            return buildSuccessResponse(newUser);

        } catch (Exception e) {
            return RegisterResponse.builder()
                    .exitoso(false)
                    .mensaje("Error al registrar usuario: " + e.getMessage())
                    .build();
        }
    }

    private boolean emailExists(String email) {
        String jpql = "SELECT COUNT(u) FROM user u WHERE u.email = :email";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("email", email);
        return query.getSingleResult() > 0;
    }

    private Role getUserRole() {
        String jpql = "SELECT r FROM Role r WHERE r.nombre = :roleName";
        TypedQuery<Role> query = entityManager.createQuery(jpql, Role.class);
        query.setParameter("roleName", "usuario");
        List<Role> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    private void assignRoleToUser(Long userId, Long roleId) {
        UserRole userRole = new UserRole();
        userRole.setUsuarioId(userId);
        userRole.setRolId(roleId);
        entityManager.persist(userRole);
    }

    private void assignPrivilegeToUser(Long userId, Long privilegeId) {
        UserPrivilege userPrivilege = new UserPrivilege();
        userPrivilege.setUsuarioId(userId);
        userPrivilege.setPrivilegioId(privilegeId);
        entityManager.persist(userPrivilege);
    }

    private void assignNeighborhoodsToUser(Long userId, List<Long> neighborhoodIds) {
        for (Long neighborhoodId : neighborhoodIds) {
            UserNeighborhood userNeighborhood = new UserNeighborhood();
            userNeighborhood.setUsuarioId(userId);
            userNeighborhood.setBarrioId(neighborhoodId);
            entityManager.persist(userNeighborhood);
        }
    }

    private RegisterResponse buildSuccessResponse(user user) {
        // Obtener roles del usuario utilizando el servicio genérico
        List<Role> userRoles = roleService.getRolesByUserId(user.getId());
        List<String> roleNames = userRoles.stream()
                .map(Role::getNombre)
                .collect(Collectors.toList());

        // Obtener privilegios del usuario utilizando el servicio genérico
        List<Privilege> userPrivileges = privilegeService.getPrivilegesByUserId(user.getId());
        List<String> privilegeNames = userPrivileges.stream()
                .map(Privilege::getNombre)
                .collect(Collectors.toList());

        // Obtener barrios del usuario
        List<Long> neighborhoodIds = getUserNeighborhoods(user.getId());

        return RegisterResponse.builder()
                .userId(user.getId())
                .nombre(user.getNombre())
                .apellido(user.getApellido())
                .email(user.getEmail())
                .telefono(user.getTelefono())
                .fechaCreacion(user.getFechaCreacion())
                .roles(roleNames)
                .privilegios(privilegeNames)
                .barrioIds(neighborhoodIds)
                .mensaje("Usuario registrado exitosamente")
                .exitoso(true)
                .build();
    }

    private List<Long> getUserNeighborhoods(Long userId) {
        String jpql = "SELECT un.barrioId FROM UserNeighborhood un WHERE un.usuarioId = :userId";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
}
