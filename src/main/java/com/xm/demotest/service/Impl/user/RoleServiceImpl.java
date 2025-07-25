package com.xm.demotest.service.Impl.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xm.demotest.common.exception.BusinessException;
import com.xm.demotest.mapper.RoleMapper;
import com.xm.demotest.mapper.RolePermissionMapper;
import com.xm.demotest.mapper.UserRoleMapper;
import com.xm.demotest.pojo.Role;
import com.xm.demotest.pojo.RolePermission;
import com.xm.demotest.service.auth.AuthService;
import com.xm.demotest.service.user.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleMapper roleMapper;
    
    @Autowired
    private RolePermissionMapper rolePermissionMapper;
    
    @Autowired
    private UserRoleMapper userRoleMapper;
    
    @Autowired
    private AuthService authService;

    @Override
    public void addRole(String roleName, String roleKey) {
        // 检查角色名是否已存在
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_name", roleName);
        if (roleMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("角色名已存在");
        }
        
        // 检查角色标识是否已存在
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_key", roleKey);
        if (roleMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("角色标识已存在");
        }
        
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new BusinessException("角色名不能为空");
        }
        if (roleKey == null || roleKey.trim().isEmpty()) {
            throw new BusinessException("角色标识不能为空");
        }
        
        Role newRole = new Role(null, roleName, roleKey);
        roleMapper.insert(newRole);
        log.info("创建角色成功: {}", newRole);
    }

    @Override
    @Transactional
    public void deleteRole(Integer roleId) {
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        
        // 删除角色权限关联
        rolePermissionMapper.deleteByRoleId(roleId);
        
        // 删除用户角色关联
        userRoleMapper.deleteByRoleId(roleId);
        
        // 删除角色
        roleMapper.deleteById(roleId);
        
        log.info("删除角色成功: {}", role);
    }

    @Override
    public void updateRole(Integer roleId, String roleName, String roleKey) {
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new BusinessException("角色名不能为空");
        }
        if (roleKey == null || roleKey.trim().isEmpty()) {
            throw new BusinessException("角色标识不能为空");
        }
        
        // 检查角色名是否与其他角色冲突
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_name", roleName).ne("id", roleId);
        if (roleMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("角色名已存在");
        }
        
        // 检查角色标识是否与其他角色冲突
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_key", roleKey).ne("id", roleId);
        if (roleMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("角色标识已存在");
        }
        
        Role updatedRole = new Role(roleId, roleName, roleKey);
        roleMapper.updateById(updatedRole);
        log.info("更新角色成功: {}", updatedRole);
    }

    @Override
    public Map<String, Object> getRoleInfo(Integer roleId) {
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", role.getId());
        result.put("roleName", role.getRoleName());
        result.put("roleKey", role.getRoleKey());
        
        return result;
    }

    @Override
    public Role getRoleById(Integer roleId) {
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        return role;
    }

    @Override
    public List<Role> getAllRoles() {
        return roleMapper.selectList(null);
    }

    @Override
    public Page<Role> getRolesByPage(Integer pageNum, Integer pageSize) {
        Page<Role> page = new Page<>(pageNum, pageSize);
        return roleMapper.selectPage(page, null);
    }

    @Override
    @Transactional
    public void assignPermissions(Integer roleId, List<Integer> permissionIds) {
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        
        // 删除原有的角色权限关联
        rolePermissionMapper.deleteByRoleId(roleId);
        
        // 插入新的角色权限关联
        if (permissionIds != null && !permissionIds.isEmpty()) {
            List<RolePermission> rolePermissions = permissionIds.stream()
                    .map(permissionId -> new RolePermission(roleId, permissionId))
                    .collect(Collectors.toList());
            rolePermissionMapper.batchInsert(rolePermissions);
        }
        
        // 清除相关用户的权限缓存
        clearUserPermissionCache(roleId);
        
        log.info("为角色 {} 分配权限: {}", roleId, permissionIds);
    }

    @Override
    public List<Integer> getRolePermissions(Integer roleId) {
        return rolePermissionMapper.selectPermissionIdsByRoleId(roleId);
    }

    @Override
    public List<Role> getRolesByUserId(Integer userId) {
        return roleMapper.selectRolesByUserId(userId);
    }
    
    /**
     * 清除拥有该角色的用户的权限缓存
     */
    private void clearUserPermissionCache(Integer roleId) {
        try {
            // 获取拥有该角色的用户ID列表
            List<Integer> userIds = userRoleMapper.selectList(
                new QueryWrapper<com.xm.demotest.pojo.UserRole>().eq("role_id", roleId)
            ).stream().map(com.xm.demotest.pojo.UserRole::getUserId).collect(Collectors.toList());
            
            // 清除这些用户的权限缓存
            for (Integer userId : userIds) {
                authService.clearUserPermissionsCache(userId);
            }
        } catch (Exception e) {
            log.error("清除用户权限缓存失败: {}", e.getMessage());
        }
    }
}
