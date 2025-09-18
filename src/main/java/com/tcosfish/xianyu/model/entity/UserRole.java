package com.tcosfish.xianyu.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 用户角色
 * @author tcosfish
 * @TableName user_role
 */
@TableName(value ="user_role")
@Data
public class UserRole {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private Long userId;

    /**
     * 
     */
    private Long roleId;

    /**
     * 
     */
    private Date createtime;
}