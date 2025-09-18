package com.micorregimiento.micorregimiento.Generics.interfaces;

import java.util.List;

public interface IPermissionValidationService {
    boolean userHasRole(Long userId, String roleName);
    boolean userHasPrivilege(Long userId, String privilegeName);
    boolean userHasAnyRole(Long userId, List<String> roleNames);
    boolean userHasAnyPrivilege(Long userId, List<String> privilegeNames);
    boolean userHasAllRoles(Long userId, List<String> roleNames);
    boolean userHasAllPrivileges(Long userId, List<String> privilegeNames);
    boolean usersHaveRole(List<Long> userIds, String roleName);
    boolean usersHavePrivilege(List<Long> userIds, String privilegeName);
}