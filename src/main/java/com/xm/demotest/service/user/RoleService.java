package com.xm.demotest.service.user;

import java.util.Map;

public interface RoleService {
    // 增加角色
    void addRole(String roleName, String roleKey);

    // 删除角色
    void deleteRole(Integer roleId);

    // 更新角色信息
    void updateRole(Integer roleId, String roleName, String roleKey);

    // 获取角色信息
    Map<String, Object> getRoleInfo(Integer roleId);
}
