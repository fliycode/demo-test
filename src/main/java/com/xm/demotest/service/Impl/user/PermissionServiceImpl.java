package com.xm.demotest.service.Impl.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xm.demotest.common.exception.BusinessException;
import com.xm.demotest.mapper.PermissionMapper;
import com.xm.demotest.mapper.RolePermissionMapper;
import com.xm.demotest.pojo.Permission;
import com.xm.demotest.service.user.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    private PermissionMapper permissionMapper;
    
    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public void addPermission(Integer parentId, String permissionName, String permissionStr) {
        // 检查权限名称是否已存在
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("permission_name", permissionName);
        if (permissionMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("权限名称已存在");
        }
        
        // 检查权限字符串是否已存在
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("permission_str", permissionStr);
        if (permissionMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("权限字符串已存在");
        }
        
        if (permissionName == null || permissionName.trim().isEmpty()) {
            throw new BusinessException("权限名称不能为空");
        }
        if (permissionStr == null || permissionStr.trim().isEmpty()) {
            throw new BusinessException("权限字符串不能为空");
        }
        
        // 如果有父权限，检查父权限是否存在
        if (parentId != null && permissionMapper.selectById(parentId) == null) {
            throw new BusinessException("父权限不存在");
        }
        
        Permission permission = new Permission();
        permission.setParentId(parentId);
        permission.setPermissionName(permissionName);
        permission.setPermissionStr(permissionStr);
        
        permissionMapper.insert(permission);
        log.info("创建权限成功: {}", permission);
    }

    @Override
    @Transactional
    public void deletePermission(Integer permissionId) {
        Permission permission = permissionMapper.selectById(permissionId);
        if (permission == null) {
            throw new BusinessException("权限不存在");
        }
        
        // 检查是否有子权限
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", permissionId);
        if (permissionMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("存在子权限，无法删除");
        }
        
        // 删除角色权限关联
        rolePermissionMapper.deleteByPermissionId(permissionId);
        
        // 删除权限
        permissionMapper.deleteById(permissionId);
        
        log.info("删除权限成功: {}", permission);
    }

    @Override
    public void updatePermission(Integer permissionId, String permissionName, String permissionStr) {
        Permission permission = permissionMapper.selectById(permissionId);
        if (permission == null) {
            throw new BusinessException("权限不存在");
        }
        
        if (permissionName == null || permissionName.trim().isEmpty()) {
            throw new BusinessException("权限名称不能为空");
        }
        if (permissionStr == null || permissionStr.trim().isEmpty()) {
            throw new BusinessException("权限字符串不能为空");
        }
        
        // 检查权限名称是否与其他权限冲突
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("permission_name", permissionName).ne("id", permissionId);
        if (permissionMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("权限名称已存在");
        }
        
        // 检查权限字符串是否与其他权限冲突
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("permission_str", permissionStr).ne("id", permissionId);
        if (permissionMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("权限字符串已存在");
        }
        
        permission.setPermissionName(permissionName);
        permission.setPermissionStr(permissionStr);
        
        permissionMapper.updateById(permission);
        log.info("更新权限成功: {}", permission);
    }

    @Override
    public Map<String, Object> getPermissionInfo(Integer permissionId) {
        Permission permission = permissionMapper.selectById(permissionId);
        if (permission == null) {
            throw new BusinessException("权限不存在");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", permission.getId());
        result.put("parentId", permission.getParentId());
        result.put("permissionName", permission.getPermissionName());
        result.put("permissionStr", permission.getPermissionStr());
        
        return result;
    }

    @Override
    public Permission getPermissionById(Integer permissionId) {
        Permission permission = permissionMapper.selectById(permissionId);
        if (permission == null) {
            throw new BusinessException("权限不存在");
        }
        return permission;
    }

    @Override
    public List<Permission> getAllPermissions() {
        return permissionMapper.selectList(null);
    }

    @Override
    public Page<Permission> getPermissionsByPage(Integer pageNum, Integer pageSize) {
        Page<Permission> page = new Page<>(pageNum, pageSize);
        return permissionMapper.selectPage(page, null);
    }

    @Override
    public List<Permission> getPermissionTree() {
        // 获取所有权限
        List<Permission> allPermissions = permissionMapper.selectList(null);
        
        // 构建树结构
        return buildPermissionTree(allPermissions, null);
    }

    @Override
    public List<Permission> getPermissionsByUserId(Integer userId) {
        return permissionMapper.selectPermissionDetailsByUserId(userId);
    }
    
    /**
     * 递归构建权限树
     */
    private List<Permission> buildPermissionTree(List<Permission> allPermissions, Integer parentId) {
        List<Permission> children = new ArrayList<>();
        
        for (Permission permission : allPermissions) {
            if ((parentId == null && permission.getParentId() == null) ||
                (parentId != null && parentId.equals(permission.getParentId()))) {
                
                // 递归获取子权限
                List<Permission> subPermissions = buildPermissionTree(allPermissions, permission.getId());
                // 这里可以为Permission添加children字段来存储子权限，暂时不添加
                
                children.add(permission);
            }
        }
        
        return children;
    }
}
