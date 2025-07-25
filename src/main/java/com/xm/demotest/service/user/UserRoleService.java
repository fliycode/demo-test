package com.xm.demotest.service.user;

import java.util.List;

public interface UserRoleService {
    void assignRoleToUser(Integer userId, Integer roleId);
    void removeRoleFromUser(Integer userId, Integer roleId);
    List<Integer> getRoleIdsByUserId(Integer userId);
}