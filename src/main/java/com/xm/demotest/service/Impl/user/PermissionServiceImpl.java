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
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
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

    }

    @Override
    public void updatePermission(Integer permissionId, String permissionName, String permissionStr) {

    }

    @Override
    public Map<String, Object> getPermissionInfo(Integer permissionId) {
        return Map.of();
    }
}
