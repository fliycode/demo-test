package com.xm.demotest.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("role_permission")
public class RolePermission {
    @TableField("role_id")
    private Integer roleId;
    
    @TableField("permission_id")
    private Integer permissionId;
}