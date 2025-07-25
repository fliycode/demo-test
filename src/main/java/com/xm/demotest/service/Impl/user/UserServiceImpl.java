package com.xm.demotest.service.Impl.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xm.demotest.common.exception.BusinessException;
import com.xm.demotest.mapper.UserMapper;
import com.xm.demotest.mapper.UserRoleMapper;
import com.xm.demotest.pojo.User;
import com.xm.demotest.pojo.UserRole;
import com.xm.demotest.service.auth.AuthService;
import com.xm.demotest.service.user.UserService;
import com.xm.demotest.utils.PasswordUtil;
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
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private UserRoleMapper userRoleMapper;
    
    @Autowired
    private AuthService authService;

    @Override
    public void addUser(String username, String password, String phone, String name, String status) {
        // 检查用户名是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        if (userMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 检查手机号是否已存在
        if (phone != null && !phone.trim().isEmpty()) {
            queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("phone", phone);
            if (userMapper.selectCount(queryWrapper) > 0) {
                throw new BusinessException("手机号已存在");
            }
        }

        if (username == null || username.trim().isEmpty()) {
            throw new BusinessException("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new BusinessException("密码不能为空");
        }

        // 加密密码
        String encodedPassword = PasswordUtil.encode(password);
        
        User newUser = new User(null, username, encodedPassword, phone, name, status);
        userMapper.insert(newUser);
        log.info("创建用户成功: {}", username);
    }

    @Override
    @Transactional
    public void deleteUser(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 删除用户角色关联
        userRoleMapper.deleteByUserId(user.getId());
        
        // 清除用户权限缓存
        authService.clearUserPermissionsCache(user.getId());
        
        // 删除用户
        userMapper.delete(queryWrapper);
        log.info("删除用户成功: {}", username);
    }

    @Override
    @Transactional
    public void deleteUserById(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 删除用户角色关联
        userRoleMapper.deleteByUserId(userId);
        
        // 清除用户权限缓存
        authService.clearUserPermissionsCache(userId);
        
        // 删除用户
        userMapper.deleteById(userId);
        log.info("删除用户成功: {}", user.getUsername());
    }

    @Override
    public void updateUser(String username, String password, String phone, String name, String status) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查手机号是否与其他用户冲突
        if (phone != null && !phone.trim().isEmpty()) {
            QueryWrapper<User> phoneQuery = new QueryWrapper<>();
            phoneQuery.eq("phone", phone).ne("id", user.getId());
            if (userMapper.selectCount(phoneQuery) > 0) {
                throw new BusinessException("手机号已存在");
            }
        }

        // 更新用户信息
        if (password != null && !password.trim().isEmpty()) {
            user.setPassword(PasswordUtil.encode(password));
        }
        user.setPhone(phone);
        user.setName(name);
        user.setStatus(status);

        userMapper.updateById(user);
        
        // 清除用户权限缓存（状态变更可能影响权限）
        authService.clearUserPermissionsCache(user.getId());
        
        log.info("更新用户成功: {}", username);
    }

    @Override
    public Map<String, Object> getUserInfo(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new BusinessException("用户不存在");
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
    public User getUserById(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.selectList(null);
    }

    @Override
    public Page<User> getUsersByPage(Integer pageNum, Integer pageSize) {
        Page<User> page = new Page<>(pageNum, pageSize);
        return userMapper.selectPage(page, null);
    }

    @Override
    @Transactional
    public void assignRoles(Integer userId, List<Integer> roleIds) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 删除原有的用户角色关联
        userRoleMapper.deleteByUserId(userId);
        
        // 插入新的用户角色关联
        if (roleIds != null && !roleIds.isEmpty()) {
            List<UserRole> userRoles = roleIds.stream()
                    .map(roleId -> new UserRole(userId, roleId))
                    .collect(Collectors.toList());
            userRoleMapper.batchInsert(userRoles);
        }
        
        // 清除用户权限缓存
        authService.clearUserPermissionsCache(userId);
        
        log.info("为用户 {} 分配角色: {}", userId, roleIds);
    }

    @Override
    public List<Integer> getUserRoles(Integer userId) {
        return userRoleMapper.selectRoleIdsByUserId(userId);
    }

    @Override
    public void updateUserStatus(Integer userId, String status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        user.setStatus(status);
        userMapper.updateById(user);
        
        // 清除用户权限缓存
        authService.clearUserPermissionsCache(userId);
        
        log.info("更新用户 {} 状态为: {}", userId, status);
    }
}
