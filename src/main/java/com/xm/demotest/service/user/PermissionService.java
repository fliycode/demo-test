package com.xm.demotest.service.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xm.demotest.pojo.Permission;

import java.util.List;
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
    
    /**
     * 根据ID获取权限
     */
    Permission getPermissionById(Integer permissionId);
    
    /**
     * 获取所有权限
     */
    List<Permission> getAllPermissions();
    
    /**
     * 分页查询权限
     */
    Page<Permission> getPermissionsByPage(Integer pageNum, Integer pageSize);
    
    /**
     * 获取权限树结构
     */
    List<Permission> getPermissionTree();
    
    /**
     * 根据用户ID获取权限列表
     */
    List<Permission> getPermissionsByUserId(Integer userId);
}
