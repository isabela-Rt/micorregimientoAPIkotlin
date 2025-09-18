package com.micorregimiento.micorregimiento.Generics.interfaces;

import java.util.List;

public interface IPrivilegeValidationService {
    boolean canCreateUser(Long userId);
    boolean canDeleteUser(Long userId);
    boolean canEditUser(Long userId);
    boolean canViewAllUsers(Long userId);
    boolean canCreatePost(Long userId);
    boolean canDeletePost(Long userId);
    boolean canEditPost(Long userId);
    boolean hasSpecificPrivilege(Long userId, String privilegeName);
    boolean hasAnyPrivilege(Long userId, List<String> privilegeNames);
}

