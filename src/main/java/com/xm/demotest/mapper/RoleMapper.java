package com.xm.demotest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xm.demotest.pojo.Role;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    // 这里可以添加自定义的查询方法
    // 例如：List<Role> findRolesByUserId(Integer userId);
}
