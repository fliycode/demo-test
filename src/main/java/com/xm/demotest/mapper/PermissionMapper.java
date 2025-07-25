package com.xm.demotest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xm.demotest.pojo.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    @Select("")
    Permission selectPermissionByUserId(Integer userId);
}
