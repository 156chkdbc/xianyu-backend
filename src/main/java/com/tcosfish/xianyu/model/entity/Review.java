package com.tcosfish.xianyu.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 评价
 * @TableName review
 */
@TableName(value ="review")
@Data
public class Review {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 评价人用户ID
     */
    private Long reviewerId;

    /**
     * 被评价人用户ID
     */
    private Long revieweeId;

    /**
     * 1商品 2服务
     */
    private Integer itemType;

    /**
     * 商品或服务ID
     */
    private Long itemId;

    /**
     * 评分 1-5
     */
    private Integer score;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 假删除
     */
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