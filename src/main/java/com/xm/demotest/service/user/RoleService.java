package com.xm.demotest.service.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xm.demotest.pojo.Role;

import java.util.List;
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
    
    /**
     * 根据ID获取角色
     */
    Role getRoleById(Integer roleId);
    
    /**
     * 获取所有角色
     */
    List<Role> getAllRoles();
    
    /**
     * 分页查询角色
     */
    Page<Role> getRolesByPage(Integer pageNum, Integer pageSize);
    
    /**
     * 为角色分配权限
     */
    void assignPermissions(Integer roleId, List<Integer> permissionIds);
    
    /**
     * 获取角色的权限ID列表
     */
    List<Integer> getRolePermissions(Integer roleId);
    
    /**
     * 根据用户ID获取角色列表
     */
    List<Role> getRolesByUserId(Integer userId);
}
