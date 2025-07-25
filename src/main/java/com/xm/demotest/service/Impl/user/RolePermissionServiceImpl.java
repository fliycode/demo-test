package com.xm.demotest.service.Impl.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xm.demotest.mapper.RolePermissionMapper;
import com.xm.demotest.pojo.RolePermission;
import com.xm.demotest.service.user.RolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RolePermissionServiceImpl implements RolePermissionService {
    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public void assignPermissionToRole(Integer roleId, Integer permissionId) {
        rolePermissionMapper.insert(new RolePermission(roleId, permissionId));
    }

    @Override
    public void removePermissionFromRole(Integer roleId, Integer permissionId) {
        QueryWrapper<RolePermission> wrapper = new QueryWrapper<>();
        wrapper.eq("role_id", roleId).eq("permission_id", permissionId);
        rolePermissionMapper.delete(wrapper);
    }

    @Override
    public List<Integer> getPermissionIdsByRoleId(Integer roleId) {
        QueryWrapper<RolePermission> wrapper = new QueryWrapper<>();
        wrapper.eq("role_id", roleId);
        return rolePermissionMapper.selectList(wrapper)
                .stream().map(RolePermission::getPermissionId).collect(Collectors.toList());
    }
}