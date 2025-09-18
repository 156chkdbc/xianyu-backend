package com.tcosfish.xianyu.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tcosfish.xianyu.model.enums.CloseReasonEnum;
import com.tcosfish.xianyu.model.enums.DeliveryTypeEnum;
import com.tcosfish.xianyu.model.enums.OrderStatusEnum;
import com.tcosfish.xianyu.model.enums.OrderTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单主表
 * @author tcosfish
 */
@Schema(description = "订单主表")
@Data
@Builder
@TableName("`order`")
public class Order {
    @Schema(description = "主键", example = "88")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "订单编号", example = "202509141523001")
    private String orderNo;

    @Schema(description = "买家/雇主用户ID", example = "1001", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long buyerId;

    @Schema(description = "卖家/服务者用户ID", example = "2002", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long sellerId;

    // MyBatis-Plus 会自动用 getValue() 存库、构造时反向映射。
    @Schema(description = "订单类型：1-商品 2-服务", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private OrderTypeEnum orderType;

    @Schema(description = "订单原价（元）", example = "20.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal totalAmount;

    @Schema(description = "实付金额（元）", example = "15.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal realAmount;

    @Schema(description = "订单状态：1-待付款 2-已付款 3-已完成 4-已取消", example = "1")
    private OrderStatusEnum status;

    @Schema(description = "付款时间", example = "2025-09-14T15:30:00")
    private Date payTime;

    @Schema(description = "完成时间", example = "2025-09-15T10:00:00")
    private Date finishTime;

    @Schema(description = "议价ID，NULL表示直接下单", example = "33")
    private Long negotiationId;

    @Schema(description = "假删除：0-正常 1-已删除", example = "0")
    private Integer deleted;

    @Schema(description = "订单过期时间（待付款状态）", example = "2025-09-14T23:59:59")
    private Date expireTime;

    @Schema(description = "订单关闭时间", example = "2025-09-15T00:00:00")
    private Date closeTime;

    @Schema(description = "关闭原因：1-用户取消 2-超时未支付 3-卖家拒单", example = "2")
    private CloseReasonEnum closeReason;

    @Schema(description = "交付方式：1-线下自取 2-宿舍送达 3-快递", example = "1")
    private DeliveryTypeEnum deliveryType;

    @Schema(description = "交付备注", example = "周六下午3点宿舍楼下取件")
    private String deliveryRemark;

    @Schema(description = "创建时间", example = "2025-09-14T15:23:00")
    private Date createtime;

    @Schema(description = "更新时间", example = "2025-09-14T15:23:00")
    private Date updatetime;
}