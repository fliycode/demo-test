package com.xm.demotest.service.Impl.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xm.demotest.mapper.UserMapper;
import com.xm.demotest.mapper.UserRoleMapper;
import com.xm.demotest.pojo.User;
import com.xm.demotest.pojo.UserRole;
import com.xm.demotest.service.auth.AuthService;
import com.xm.demotest.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void addUser(String username, String password, String phone, String name, String status) {
        if(username == null || username.trim().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        if(password == null || password.length() < 6) {
            throw new RuntimeException("密码长度不能少于6位");
        }
        if(phone == null || !phone.matches("^1[3-9]\\d{9}$")) {
            throw new RuntimeException("手机号格式不正确");
        }
        if(name == null || name.trim().isEmpty()) {
            throw new RuntimeException("姓名不能为空");
        }

        // 检查用户名是否已存在
        QueryWrapper<User> usernameQuery = new QueryWrapper<>();
        usernameQuery.eq("username", username);
        Long usernameCount = userMapper.selectCount(usernameQuery);
        if(usernameCount > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查手机号是否已存在
        QueryWrapper<User> phoneQuery = new QueryWrapper<>();
        phoneQuery.eq("phone", phone);
        Long phoneCount = userMapper.selectCount(phoneQuery);
        if(phoneCount > 0) {
            throw new RuntimeException("手机号已被注册");
        }

        // 密码加密
        String encodedPassword = passwordEncoder.encode(password);

        User newUser = new User(null, username, encodedPassword, phone, name, status != null ? status : "1");
        userMapper.insert(newUser);
        
        // 分配默认角色（普通用户角色ID为2）
        UserRole userRole = new UserRole();
        userRole.setUserId(newUser.getId());
        userRole.setRoleId(2);
        userRoleMapper.insert(userRole);
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
    @Transactional
    public void updateUser(String username, String password, String phone, String name, String status) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);
        if(user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查手机号格式和唯一性
        if(phone != null && !phone.trim().isEmpty()) {
            if(!phone.matches("^1[3-9]\\d{9}$")) {
                throw new RuntimeException("手机号格式不正确");
            }
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
            String encodedPassword = passwordEncoder.encode(password);
            user.setPassword(encodedPassword);
        }
        
        user.setPhone(phone);
        user.setName(name);
        user.setStatus(status != null ? status : user.getStatus());
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
    @Transactional
    public void deleteUserById(Integer userId) {
        if(userId == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        
        User user = userMapper.selectById(userId);
        if(user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 删除角色关联
        userRoleMapper.deleteByUserId(userId);
        // 清除用户权限缓存
        authService.clearUserPermissionsCache(userId);
        // 删除用户
        userMapper.deleteById(userId);
    }

    @Override
    public Map<String, Object> getUserInfoById(Integer userId) {
        if(userId == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        
        User user = userMapper.selectById(userId);
        if(user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("phone", user.getPhone());
        result.put("name", user.getName());
        result.put("status", user.getStatus());
        
        // 获取用户角色
        List<Integer> roleIds = getUserRoles(userId);
        result.put("roleIds", roleIds);
        
        return result;
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.selectList(null);
    }
    
    @Override
    public Page<User> getUsersByPage(Integer current, Integer size, String keyword) {
        current = current == null || current < 1 ? 1 : current;
        size = size == null || size < 1 ? 10 : size;
        
        Page<User> page = new Page<>(current, size);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        
        if(keyword != null && !keyword.trim().isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                .like("username", keyword)
                .or()
                .like("name", keyword)
                .or()
                .like("phone", keyword)
            );
        }
        
        queryWrapper.orderByDesc("id");
        return userMapper.selectPage(page, queryWrapper);
    }

    @Override
    @Transactional
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

        // 删除现有角色关联
        userRoleMapper.deleteByUserId(userId);
        
        // 添加新的角色关联
        List<UserRole> userRoles = new ArrayList<>();
        for(Integer roleId : roleIds) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoles.add(userRole);
        }
        
        if(!userRoles.isEmpty()) {
            userRoleMapper.batchInsert(userRoles);
        }
        
        // 清除用户权限缓存
        authService.clearUserPermissionsCache(userId);
    }

    @Override
    public List<Integer> getUserRoles(Integer userId) {
        return userRoleMapper.selectRoleIdsByUserId(userId);
    }

    @Override
    @Transactional
    public void updateUserStatus(Integer userId, String status) {
        if(userId == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        if(status == null || (!status.equals("0") && !status.equals("1"))) {
            throw new RuntimeException("状态值无效，应为0或1");
        }
        
        User user = userMapper.selectById(userId);
        if(user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        user.setStatus(status);
        userMapper.updateById(user);
        
        // 如果是禁用用户，清除权限缓存
        if("0".equals(status)) {
            authService.clearUserPermissionsCache(userId);
        }
    }


}
