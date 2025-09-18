package com.tcosfish.xianyu.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tcosfish.xianyu.model.enums.OrderItemTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单明细表
 * @author tcosfish
 */
@Schema(description = "订单明细表")
@Data
@Builder
@TableName("order_item")
public class OrderItem {
    @Schema(description = "主键", example = "101")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "订单ID", example = "88", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long orderId;

    @Schema(description = "明细类型：1-商品 2-服务", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private OrderItemTypeEnum itemType;

    @Schema(description = "商品或服务ID", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long itemId;

    @Schema(description = "标题快照（下单时）", example = "线性代数教材（第九版）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String itemTitle;

    @Schema(description = "成交单价（元）", example = "15.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal unitPrice;

    @Schema(description = "数量/时长（小时）", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;

    @Schema(description = "小计金额（元）", example = "15.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @Schema(description = "交付方式：1-线下自取 2-宿舍送达 3-快递", example = "1")
    private Integer deliveryType;

    @Schema(description = "创建时间", example = "2025-09-14T15:23:01")
    private Date createtime;
}