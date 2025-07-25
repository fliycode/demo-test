package com.xm.demotest.service.Impl.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xm.demotest.mapper.UserMapper;
import com.xm.demotest.pojo.User;
import com.xm.demotest.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

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

        User newUser = new User(null,username, password, phone, name, status);
        userMapper.insert(newUser);
    }

    @Override
    public void deleteUser(String username) {

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        List<User> user = userMapper.selectList(queryWrapper);
        if(user.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        userMapper.delete(queryWrapper);
    }

    @Override
    public void updateUser(String username, String password, String phone, String name, String status) {

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        List<User> user = userMapper.selectList(queryWrapper);
        if(user.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }

        userMapper.update(new User(null, username, password, phone, name, status), queryWrapper);
    }

    /**
     * 获取用户信息 ,此处应该是根据用户名获取指定用户信息，但是没有写好
     * @param username
     * @return
     */
    @Override
    public Map<String, Object> getUserInfo(String username) {
        Map<String, Object> result = new HashMap<>();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        List<User> user = userMapper.selectList(queryWrapper);

        if(user.isEmpty()) {
            result.put("error_message", "用户不存在");
            return result;
        }


        result.put("username", user.get(0).getUsername());
        result.put("phone", user.get(0).getPhone());
        result.put("name", user.get(0).getName());
        result.put("status", user.get(0).getStatus());

        return result;
    }
}
