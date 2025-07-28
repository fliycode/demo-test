package com.xm.demotest.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("user_role")
public class UserRole {
    @TableField("user_id")
    private Integer userId;

    @TableField("role_id")
    private Integer roleId;
}