package com.tcosfish.xianyu.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import com.tcosfish.xianyu.model.enums.NegotiationStatus;
import lombok.Data;

/**
 * @author tcosfish
 * 议价记录表
 * @TableName negotiation
 */
@TableName(value ="negotiation")
@Data
public class Negotiation {

    /** 主键 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 商品ID */
    private Long itemId;

    /** 买家用户ID */
    private Long buyerId;

    /** 卖家用户ID */
    private Long sellerId;

    /** 买家出价 */
    private BigDecimal price;

    /**
     * 状态
     * 1待回应 2已接受 3已拒绝 4已失效 5已取消
     */
    private NegotiationStatus status;

    /** 本轮议价轮次（默认1，最多3）, 每次二次议价时 +1 */
    private Integer round;

    /** 买家留言 */
    private String message;

    /** 有效期截止时间, 业务代码写入（当前时间 + 24h） */
    private LocalDateTime expireAt;

    /** 逻辑删除标识 */
    @TableLogic
    private Integer deleted;

    /** 创建时间 */
    private Date createtime;

    /** 更新时间 */
    private Date updatetime;
}