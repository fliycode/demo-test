package com.xm.demotest.service.Impl.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xm.demotest.mapper.UserRoleMapper;
import com.xm.demotest.pojo.UserRole;
import com.xm.demotest.service.user.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserRoleServiceImpl implements UserRoleService {
    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public void assignRoleToUser(Integer userId, Integer roleId) {
        userRoleMapper.insert(new UserRole(userId, roleId));
    }

    @Override
    public void removeRoleFromUser(Integer userId, Integer roleId) {
        QueryWrapper<UserRole> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("role_id", roleId);
        userRoleMapper.delete(wrapper);
    }

    @Override
    public List<Integer> getRoleIdsByUserId(Integer userId) {
        QueryWrapper<UserRole> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        return userRoleMapper.selectList(wrapper)
                .stream().map(UserRole::getRoleId).collect(Collectors.toList());
    }
}