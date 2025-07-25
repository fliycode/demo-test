package com.xm.demotest.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("permission")
public class Permission {
    private Integer id;
    @TableField(value = "parent_id")
    private Integer parentId;
    @TableField(value = "permission_name")
    private String permissionName;
    @TableField(value = "permission_str")
    private String permissionStr;

}
