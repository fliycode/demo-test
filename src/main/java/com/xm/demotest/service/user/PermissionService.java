package com.xm.demotest.service.user;

import java.util.Map;

public interface PermissionService {
    // 增加权限
    void addPermission(Integer parentId, String permissionName, String permissionStr);

    // 删除权限
    void deletePermission(Integer permissionId);

    // 更新权限
    void updatePermission(Integer permissionId, String permissionName, String permissionStr);

    // 获取权限信息
    Map<String, Object> getPermissionInfo(Integer permissionId);
}
