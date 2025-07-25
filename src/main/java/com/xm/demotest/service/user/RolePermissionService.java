package com.xm.demotest.service.user;

import java.util.List;

public interface RolePermissionService {
    void assignPermissionToRole(Integer roleId, Integer permissionId);
    void removePermissionFromRole(Integer roleId, Integer permissionId);
    List<Integer> getPermissionIdsByRoleId(Integer roleId);
}