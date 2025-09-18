package com.tcosfish.xianyu.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import lombok.Data;

/**
 * 商品图片表：一行一图，含软删除
 * @author tcosfish
 * @TableName item_images
 */
@TableName(value ="item_images")
@Data
public class ItemImages {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商品ID，对应 item.id
     */
    private Long itemId;

    /**
     * CDN地址
     */
    private String url;

    /**
     * 顺序，从0开始
     */
    private Integer idx;

    /**
     * 是否封面：1=是，0=否
     */
    private Integer isMaster;

    /**
     * 软删除：0=正常；1=已删除
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;
}