package com.micorregimiento.micorregimiento.Generics.interfaces;

import com.micorregimiento.micorregimiento.Privileges.entitys.Privilege;
import java.util.List;

public interface IPrivilegeService {
    List<Privilege> getPrivilegesByUserId(Long userId);
    List<Privilege> getPrivilegesByUserIds(List<Long> userIds);
    List<Privilege> getAllPrivileges();
}