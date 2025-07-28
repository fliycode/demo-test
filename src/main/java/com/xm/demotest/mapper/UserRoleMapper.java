package com.xm.demotest.mapper;

import com.xm.demotest.pojo.UserRole;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserRoleMapper {

    /**
     * 根据用户ID删除用户角色关联
     */
    @Delete("DELETE FROM user_role WHERE user_id = #{userId}")
    void deleteByUserId(Integer userId);

    /**
     * 根据角色ID删除用户角色关联
     */
    @Delete("DELETE FROM user_role WHERE role_id = #{roleId}")
    void deleteByRoleId(Integer roleId);

    /**
     * 批量插入用户角色关联
     */
    void batchInsert(List<UserRole> userRoles);

    /**
     * 根据用户ID查询角色ID列表
     */
    @Select("SELECT role_id FROM user_role WHERE user_id = #{userId}")
    List<Integer> selectRoleIdsByUserId(Integer userId);

}
