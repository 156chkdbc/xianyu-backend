package com.tcosfish.xianyu.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import lombok.Data;

/**
 * 用户-热数据
 * @author tcosfish
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 登录账号
     */
    private String username;

    /**
     * 密码哈希
     */
    private String passwordHash;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 1启用 0禁用
     */
    private Integer status;

    /**
     * 假删除
     */
    @TableLogic
    private Integer deleted;

    /**
     * 
     */
    private Date createtime;

    /**
     * 
     */
    private Date updatetime;
}