package com.xm.demotest.service.Impl.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xm.demotest.mapper.PermissionMapper;
import com.xm.demotest.pojo.Permission;
import com.xm.demotest.service.user.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    private PermissionMapper permissionMapper;


    @Override
    public void addPermission(Integer parentId, String permissionName, String permissionStr) {
        QueryWrapper<permissionMapper> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("permission_name", permissionName);
        if (permissionMapper.selectCount(queryWrapper) > 0) {
            throw new RuntimeException("权限名称已存在");
        }
        if (permissionName == null || permissionName.trim().isEmpty()) {
            throw new RuntimeException("权限名称不能为空");
        }
        if (permissionStr == null || permissionStr.trim().isEmpty()) {
            throw new RuntimeException("权限字符串不能为空");
        }
        permissionMapper.insert(new Permission(null, parentId, permissionName, permissionStr));


    }

    @Override
    public void deletePermission(Integer permissionId) {
        if (permissionId == null) {
            throw new RuntimeException("权限ID不能为空");
        }
        Permission permission = permissionMapper.selectById(permissionId);
        if (permission == null) {
            throw new RuntimeException("权限不存在");
        }
        permissionMapper.deleteById(permissionId);
    }

    @Override
    public void updatePermission(Integer permissionId, String permissionName, String permissionStr) {
        if (permissionId == null) {
            throw new RuntimeException("权限ID不能为空");
        }
        Permission permission = permissionMapper.selectById(permissionId);
        if (permission == null) {
            throw new RuntimeException("权限不存在");
        }
        if (permissionName == null || permissionName.trim().isEmpty()) {
            throw new RuntimeException("权限名称不能为空");
        }
        if (permissionStr == null || permissionStr.trim().isEmpty()) {
            throw new RuntimeException("权限字符串不能为空");
        }
        permission.setPermissionName(permissionName);
        permission.setPermissionStr(permissionStr);
        permissionMapper.updateById(permission);
    }

    @Override
    public Map<String, Object> getPermissionInfo(Integer permissionId) {
        Permission permission = permissionMapper.selectById(permissionId);
        if (permission == null) {
            throw new RuntimeException("权限不存在");
        }
        return Map.of(
                "id", permission.getId(),
                "parentId", permission.getParentId(),
                "permissionName", permission.getPermissionName(),
                "permissionStr", permission.getPermissionStr()
        );
    }
}
