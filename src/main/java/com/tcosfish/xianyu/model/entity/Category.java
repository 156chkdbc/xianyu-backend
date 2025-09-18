package com.tcosfish.xianyu.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import lombok.Data;

/**
 * 类目
 * @author tcofish
 * @TableName category
 */
@TableName(value ="category")
@Data
public class Category {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 父节点ID，0=一级
     */
    private Long parentId;

    /**
     * 类目名称
     */
    private String name;

    /**
     * 层级
     */
    private Integer level;

    /**
     * 假删除
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    private Date createtime;

    /**
     * 更新时间
     */
    private Date updatetime;
}