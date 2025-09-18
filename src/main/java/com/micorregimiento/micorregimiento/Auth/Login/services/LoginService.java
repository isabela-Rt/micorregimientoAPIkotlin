package com.micorregimiento.micorregimiento.Auth.Login.services;

import com.micorregimiento.micorregimiento.Auth.Login.interfaces.ILoginService;
import com.micorregimiento.micorregimiento.Auth.Login.entitys.LoginRequest;
import com.micorregimiento.micorregimiento.Auth.Login.entitys.LoginResponse;
import com.micorregimiento.micorregimiento.Auth.Login.entitys.TokenValidationResponse;
import com.micorregimiento.micorregimiento.Generics.interfaces.IRoleService;
import com.micorregimiento.micorregimiento.Generics.interfaces.IPrivilegeService;
import com.micorregimiento.micorregimiento.Generics.interfaces.IJwtService;
import com.micorregimiento.micorregimiento.Users.entitys.user;
import com.micorregimiento.micorregimiento.Roles.entitys.Role;
import com.micorregimiento.micorregimiento.Privileges.entitys.Privilege;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoginService implements ILoginService {

    @PersistenceContext
    private EntityManager entityManager;

    private final PasswordEncoder passwordEncoder;
    private final IRoleService roleService;
    private final IPrivilegeService privilegeService;
    private final IJwtService jwtService;

    @Autowired
    public LoginService(PasswordEncoder passwordEncoder,
                        IRoleService roleService,
                        IPrivilegeService privilegeService,
                        IJwtService jwtService) {
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.privilegeService = privilegeService;
        this.jwtService = jwtService;
    }

    @Override
    public LoginResponse authenticateUser(LoginRequest request) {
        try {
            // Buscar usuario por email
            user foundUser = findUserByEmail(request.getEmail());
            if (foundUser == null) {
                return LoginResponse.builder()
                        .exitoso(false)
                        .mensaje("Credenciales inválidas")
                        .build();
            }

            // Verificar contraseña
            if (!passwordEncoder.matches(request.getPassword(), foundUser.getPasswordHash())) {
                return LoginResponse.builder()
                        .exitoso(false)
                        .mensaje("Credenciales inválidas")
                        .build();
            }

            // Obtener roles utilizando el servicio genérico
            List<Role> userRoles = roleService.getRolesByUserId(foundUser.getId());
            List<String> roleNames = userRoles.stream()
                    .map(Role::getNombre)
                    .collect(Collectors.toList());

            // Obtener privilegios utilizando el servicio genérico
            List<Privilege> userPrivileges = privilegeService.getPrivilegesByUserId(foundUser.getId());
            List<String> privilegeNames = userPrivileges.stream()
                    .map(Privilege::getNombre)
                    .collect(Collectors.toList());

            // Si el usuario es admin, agregar todos los privilegios
            if (roleNames.contains("admin")) {
                privilegeNames = addAllAdminPrivileges(privilegeNames);
            }

            // Obtener barrios del usuario
            List<Long> neighborhoodIds = getUserNeighborhoods(foundUser.getId());

            // Generar tokens
            String accessToken = jwtService.generateAccessToken(
                    foundUser.getId(),
                    foundUser.getEmail(),
                    roleNames,
                    privilegeNames,
                    neighborhoodIds
            );

            String refreshToken = jwtService.generateRefreshToken(
                    foundUser.getId(),
                    foundUser.getEmail()
            );

            // Calcular tiempo de expiración
            LocalDateTime tokenExpiration = LocalDateTime.now().plusHours(1); // 1 hora por defecto
            long expiresIn = 3600; // 1 hora en segundos

            return LoginResponse.builder()
                    .userId(foundUser.getId())
                    .nombre(foundUser.getNombre())
                    .apellido(foundUser.getApellido())
                    .email(foundUser.getEmail())
                    .telefono(foundUser.getTelefono())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenExpiration(tokenExpiration)
                    .roles(roleNames)
                    .privilegios(privilegeNames)
                    .barrioIds(neighborhoodIds)
                    .mensaje("Login exitoso")
                    .exitoso(true)
                    .expiresIn(expiresIn)
                    .build();

        } catch (Exception e) {
            return LoginResponse.builder()
                    .exitoso(false)
                    .mensaje("Error interno durante la autenticación: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        try {
            TokenValidationResponse validation = jwtService.validateToken(refreshToken);
            if (!validation.isValid()) {
                return LoginResponse.builder()
                        .exitoso(false)
                        .mensaje("Refresh token inválido")
                        .build();
            }

            // Buscar usuario
            user foundUser = findUserByEmail(validation.getEmail());
            if (foundUser == null) {
                return LoginResponse.builder()
                        .exitoso(false)
                        .mensaje("Usuario no encontrado")
                        .build();
            }

            // Obtener datos actualizados del usuario
            List<Role> userRoles = roleService.getRolesByUserId(foundUser.getId());
            List<String> roleNames = userRoles.stream()
                    .map(Role::getNombre)
                    .collect(Collectors.toList());

            List<Privilege> userPrivileges = privilegeService.getPrivilegesByUserId(foundUser.getId());
            List<String> privilegeNames = userPrivileges.stream()
                    .map(Privilege::getNombre)
                    .collect(Collectors.toList());

            if (roleNames.contains("admin")) {
                privilegeNames = addAllAdminPrivileges(privilegeNames);
            }

            List<Long> neighborhoodIds = getUserNeighborhoods(foundUser.getId());

            // Generar nuevo access token
            String newAccessToken = jwtService.generateAccessToken(
                    foundUser.getId(),
                    foundUser.getEmail(),
                    roleNames,
                    privilegeNames,
                    neighborhoodIds
            );

            LocalDateTime tokenExpiration = LocalDateTime.now().plusHours(1);

            return LoginResponse.builder()
                    .userId(foundUser.getId())
                    .nombre(foundUser.getNombre())
                    .apellido(foundUser.getApellido())
                    .email(foundUser.getEmail())
                    .telefono(foundUser.getTelefono())
                    .accessToken(newAccessToken)
                    .refreshToken(refreshToken) // El mismo refresh token
                    .tokenExpiration(tokenExpiration)
                    .roles(roleNames)
                    .privilegios(privilegeNames)
                    .barrioIds(neighborhoodIds)
                    .mensaje("Token renovado exitosamente")
                    .exitoso(true)
                    .expiresIn(3600)
                    .build();

        } catch (Exception e) {
            return LoginResponse.builder()
                    .exitoso(false)
                    .mensaje("Error al renovar token: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public TokenValidationResponse validateToken(String token) {
        return jwtService.validateToken(token);
    }

    @Override
    public boolean logout(String token) {
        try {
            jwtService.blacklistToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private user findUserByEmail(String email) {
        String jpql = "SELECT u FROM user u WHERE u.email = :email";
        TypedQuery<user> query = entityManager.createQuery(jpql, user.class);
        query.setParameter("email", email);
        List<user> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    private List<Long> getUserNeighborhoods(Long userId) {
        String jpql = "SELECT un.barrioId FROM UserNeighborhood un WHERE un.usuarioId = :userId";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    private List<String> addAllAdminPrivileges(List<String> existingPrivileges) {
        List<String> allPrivileges = List.of(
                "CREATE_USER", "DELETE_USER", "EDIT_USER", "VIEW_ALL_USERS",
                "CREATE_POST", "DELETE_POST", "EDIT_POST"
        );

        List<String> result = new java.util.ArrayList<>(existingPrivileges);
        for (String privilege : allPrivileges) {
            if (!result.contains(privilege)) {
                result.add(privilege);
            }
        }
        return result;
    }
}
