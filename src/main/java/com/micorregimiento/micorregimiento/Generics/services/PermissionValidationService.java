package com.micorregimiento.micorregimiento.Generics.services;

import com.micorregimiento.micorregimiento.Generics.interfaces.IPermissionValidationService;
import com.micorregimiento.micorregimiento.Generics.interfaces.IRoleService;
import com.micorregimiento.micorregimiento.Generics.interfaces.IPrivilegeService;
import com.micorregimiento.micorregimiento.Roles.entitys.Role;
import com.micorregimiento.micorregimiento.Privileges.entitys.Privilege;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PermissionValidationService implements IPermissionValidationService {

    private final IRoleService roleService;
    private final IPrivilegeService privilegeService;

    @Autowired
    public PermissionValidationService(IRoleService roleService, IPrivilegeService privilegeService) {
        this.roleService = roleService;
        this.privilegeService = privilegeService;
    }

    @Override
    public boolean userHasRole(Long userId, String roleName) {
        List<Role> userRoles = roleService.getRolesByUserId(userId);
        return userRoles.stream()
                .anyMatch(role -> role.getNombre().equalsIgnoreCase(roleName));
    }

    @Override
    public boolean userHasPrivilege(Long userId, String privilegeName) {
        List<Privilege> userPrivileges = privilegeService.getPrivilegesByUserId(userId);
        return userPrivileges.stream()
                .anyMatch(privilege -> privilege.getNombre().equalsIgnoreCase(privilegeName));
    }

    @Override
    public boolean userHasAnyRole(Long userId, List<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            return false;
        }

        List<Role> userRoles = roleService.getRolesByUserId(userId);
        Set<String> userRoleNames = userRoles.stream()
                .map(role -> role.getNombre().toLowerCase())
                .collect(Collectors.toSet());

        return roleNames.stream()
                .anyMatch(roleName -> userRoleNames.contains(roleName.toLowerCase()));
    }

    @Override
    public boolean userHasAnyPrivilege(Long userId, List<String> privilegeNames) {
        if (privilegeNames == null || privilegeNames.isEmpty()) {
            return false;
        }

        List<Privilege> userPrivileges = privilegeService.getPrivilegesByUserId(userId);
        Set<String> userPrivilegeNames = userPrivileges.stream()
                .map(privilege -> privilege.getNombre().toLowerCase())
                .collect(Collectors.toSet());

        return privilegeNames.stream()
                .anyMatch(privilegeName -> userPrivilegeNames.contains(privilegeName.toLowerCase()));
    }

    @Override
    public boolean userHasAllRoles(Long userId, List<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            return true;
        }

        List<Role> userRoles = roleService.getRolesByUserId(userId);
        Set<String> userRoleNames = userRoles.stream()
                .map(role -> role.getNombre().toLowerCase())
                .collect(Collectors.toSet());

        return roleNames.stream()
                .allMatch(roleName -> userRoleNames.contains(roleName.toLowerCase()));
    }

    @Override
    public boolean userHasAllPrivileges(Long userId, List<String> privilegeNames) {
        if (privilegeNames == null || privilegeNames.isEmpty()) {
            return true;
        }

        List<Privilege> userPrivileges = privilegeService.getPrivilegesByUserId(userId);
        Set<String> userPrivilegeNames = userPrivileges.stream()
                .map(privilege -> privilege.getNombre().toLowerCase())
                .collect(Collectors.toSet());

        return privilegeNames.stream()
                .allMatch(privilegeName -> userPrivilegeNames.contains(privilegeName.toLowerCase()));
    }

    @Override
    public boolean usersHaveRole(List<Long> userIds, String roleName) {
        if (userIds == null || userIds.isEmpty()) {
            return false;
        }

        List<Role> roles = roleService.getRolesByUserIds(userIds);
        return roles.stream()
                .anyMatch(role -> role.getNombre().equalsIgnoreCase(roleName));
    }

    @Override
    public boolean usersHavePrivilege(List<Long> userIds, String privilegeName) {
        if (userIds == null || userIds.isEmpty()) {
            return false;
        }

        List<Privilege> privileges = privilegeService.getPrivilegesByUserIds(userIds);
        return privileges.stream()
                .anyMatch(privilege -> privilege.getNombre().equalsIgnoreCase(privilegeName));
    }
}
