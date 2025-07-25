package com.xm.demotest.service.Impl.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xm.demotest.mapper.RoleMapper;
import com.xm.demotest.pojo.Role;
import com.xm.demotest.service.user.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleMapper roleMapper;


    @Override
    public void addRole(String roleName, String roleKey) {
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_name", roleName);
        List<Role> role = roleMapper.selectList(queryWrapper);
        if (!role.isEmpty()) {
            throw new RuntimeException("角色名已存在");
        }
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new RuntimeException("角色名不能为空");
        }
        Role newRole = new Role(null, roleName, roleKey);
        roleMapper.insert(newRole);
    }

    @Override
    public void deleteRole(Integer roleId) {
        if (roleId == null) {
            throw new RuntimeException("角色ID不能为空");
        }
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        roleMapper.deleteById(roleId);
    }

    @Override
    public void updateRole(Integer roleId, String roleName, String roleKey) {
        if (roleId == null) {
            throw new RuntimeException("角色ID不能为空");
        }
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new RuntimeException("角色名不能为空");
        }
        role.setRoleName(roleName);
        role.setRoleKey(roleKey);
        roleMapper.updateById(role);
    }

    @Override
    public Map<String, Object> getRoleInfo(Integer roleId) {
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        return Map.of(
                "id", role.getId(),
                "roleName", role.getRoleName(),
                "roleKey", role.getRoleKey()
        );
    }
}
