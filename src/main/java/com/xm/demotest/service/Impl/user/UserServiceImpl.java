package com.xm.demotest.service.Impl.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xm.demotest.mapper.UserMapper;
import com.xm.demotest.mapper.UserRoleMapper;
import com.xm.demotest.pojo.User;
import com.xm.demotest.service.auth.AuthService;
import com.xm.demotest.service.user.UserService;
import com.xm.demotest.utils.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private AuthService authService;

    @Override
    public void addUser(String username, String password, String phone, String name, String status) {

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        List<User> user = userMapper.selectList(queryWrapper);
        if(!user.isEmpty()) {
            throw new RuntimeException("用户名已存在");
        }

        if(username == null || username.trim().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        if(password == null || password.length() < 6) {
            throw new RuntimeException("密码长度不能少于6位");
        }
        // 密码加密
        String encodedPassword = PasswordUtil.encode(password);

        User newUser = new User(null,username, encodedPassword, phone, name, status);
        userMapper.insert(newUser);
    }

    @Override
    public void deleteUser(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);
        if(user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 删除角色关联
        userRoleMapper.deleteByUserId(user.getId());
        // 清楚用户权限缓存
        authService.clearUserPermissionsCache(user.getId());

        // 删除用户
        userMapper.delete(queryWrapper);
    }

    @Override
    public void updateUser(String username, String password, String phone, String name, String status) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);
        if(user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查手机号是否唯一
        if(phone != null && !phone.trim().isEmpty()) {
            QueryWrapper<User> phoneQueryWrapper = new QueryWrapper<>();
            phoneQueryWrapper.eq("phone", phone).ne("id", user.getId());
            if (userMapper.selectCount(phoneQueryWrapper) > 0) {
                throw new RuntimeException("手机号已被其他用户使用");
            }
        }
        // 检查姓名是否为空
        if(name == null || name.trim().isEmpty()) {
            throw new RuntimeException("姓名不能为空");
        }
        // 密码处理
        if(password != null && !password.trim().isEmpty()) {
            if(password.length() < 6) {
                throw new RuntimeException("密码长度不能少于6位");
            }
            // 密码加密
            String encodedPassword = PasswordUtil.encode(password);
            user.setPassword(encodedPassword);
        }
        user.setPhone(phone);
        user.setName(name);
        user.setStatus(status);
        userMapper.updateById(user);

        // 清除用户权限缓存
        authService.clearUserPermissionsCache(user.getId());


    }

    /**
     * 获取用户信息 ,此处应该是根据用户名获取指定用户信息，但是没有写好
     * @param username
     * @return
     */
    @Override
    public Map<String, Object> getUserInfo(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);
        if(user == null) {
            throw new RuntimeException("用户不存在");
        }
        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("phone", user.getPhone());
        result.put("name", user.getName());
        result.put("status", user.getStatus());

        return result;
    }

    @Override
    public List<User> getAllUsers() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> users = userMapper.selectList(queryWrapper);
        if(users.isEmpty()) {
            throw new RuntimeException("没有用户信息");
        }
        return users;
    }

    @Override
    public void assignRoles(Integer userId, List<Integer> roleIds) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", userId);
        User user = userMapper.selectOne(queryWrapper);
        if(user == null) {
            throw new RuntimeException("用户不存在");
        }
        if(roleIds == null || roleIds.isEmpty()) {
            throw new RuntimeException("角色列表不能为空");
        }

    }

    @Override
    public List<Integer> getUserRoles(Integer userId) {
        return List.of();
    }

    @Override
    public void updateUserStatus(Integer userId, String status) {

    }


}
