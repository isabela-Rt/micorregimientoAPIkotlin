package com.micorregimiento.micorregimiento.Generics.interfaces;

import com.micorregimiento.micorregimiento.Roles.entitys.Role;
import java.util.List;

public interface IRoleService {
    List<Role> getRolesByUserId(Long userId);
    List<Role> getRolesByUserIds(List<Long> userIds);
    List<Role> getAllRoles();
}