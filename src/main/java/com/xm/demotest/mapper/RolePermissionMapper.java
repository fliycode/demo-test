package com.xm.demotest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xm.demotest.pojo.RolePermission;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    /**
     * 根据角色ID删除角色权限关联
     */
    @Delete("DELETE FROM role_permission WHERE role_id = #{roleId}")
    void deleteByRoleId(Integer roleId);

    /**
     * 根据权限ID删除角色权限关联
     */
    @Delete("DELETE FROM role_permission WHERE permission_id = #{permissionId}")
    void deleteByPermissionId(Integer permissionId);

    /**
     * 批量插入角色权限关联
     */
    void batchInsert(List<RolePermission> rolePermissions);

    /**
     * 查询角色的权限ID列表
     */
    @Select("SELECT permission_id FROM role_permission WHERE role_id = #{roleId}")
    List<Integer> selectPermissionIdsByRoleId(Integer roleId);
}