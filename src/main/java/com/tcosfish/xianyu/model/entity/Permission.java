package com.tcosfish.xianyu.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import lombok.Data;

/**
 * 权限
 * @author tcosfish
 * @TableName permission
 */
@TableName(value ="permission")
@Data
public class Permission {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private String permCode;

    /**
     * 
     */
    private String permName;

    /**
     * menu/api
     */
    private String resourceType;

    /**
     * 前端路由或后端API
     */
    private String resourcePath;

    /**
     * HTTP方法
     */
    private String method;

    /**
     * 
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