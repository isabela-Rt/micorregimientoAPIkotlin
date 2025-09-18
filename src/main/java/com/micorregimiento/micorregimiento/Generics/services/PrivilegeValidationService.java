package com.micorregimiento.micorregimiento.Generics.services;

import com.micorregimiento.micorregimiento.Generics.interfaces.IPrivilegeValidationService;
import com.micorregimiento.micorregimiento.Generics.interfaces.IPermissionValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrivilegeValidationService implements IPrivilegeValidationService {

    private final IPermissionValidationService permissionValidationService;

    @Autowired
    public PrivilegeValidationService(IPermissionValidationService permissionValidationService) {
        this.permissionValidationService = permissionValidationService;
    }

    @Override
    public boolean canCreateUser(Long userId) {
        return permissionValidationService.userHasRole(userId, "admin") ||
                permissionValidationService.userHasPrivilege(userId, "CREATE_USER");
    }

    @Override
    public boolean canDeleteUser(Long userId) {
        return permissionValidationService.userHasRole(userId, "admin") ||
                permissionValidationService.userHasPrivilege(userId, "DELETE_USER");
    }

    @Override
    public boolean canEditUser(Long userId) {
        return permissionValidationService.userHasRole(userId, "admin") ||
                permissionValidationService.userHasPrivilege(userId, "EDIT_USER");
    }

    @Override
    public boolean canViewAllUsers(Long userId) {
        return permissionValidationService.userHasRole(userId, "admin") ||
                permissionValidationService.userHasPrivilege(userId, "VIEW_ALL_USERS");
    }

    @Override
    public boolean canCreatePost(Long userId) {
        return permissionValidationService.userHasRole(userId, "admin") ||
                permissionValidationService.userHasPrivilege(userId, "CREATE_POST");
    }

    @Override
    public boolean canDeletePost(Long userId) {
        return permissionValidationService.userHasRole(userId, "admin") ||
                permissionValidationService.userHasPrivilege(userId, "DELETE_POST");
    }

    @Override
    public boolean canEditPost(Long userId) {
        return permissionValidationService.userHasRole(userId, "admin") ||
                permissionValidationService.userHasPrivilege(userId, "EDIT_POST");
    }

    @Override
    public boolean hasSpecificPrivilege(Long userId, String privilegeName) {
        return permissionValidationService.userHasRole(userId, "admin") ||
                permissionValidationService.userHasPrivilege(userId, privilegeName);
    }

    @Override
    public boolean hasAnyPrivilege(Long userId, List<String> privilegeNames) {
        return permissionValidationService.userHasRole(userId, "admin") ||
                permissionValidationService.userHasAnyPrivilege(userId, privilegeNames);
    }
}
