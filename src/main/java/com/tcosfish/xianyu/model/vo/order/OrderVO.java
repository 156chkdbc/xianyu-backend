package com.tcosfish.xianyu.model.vo.order;

import com.tcosfish.xianyu.model.enums.CloseReasonEnum;
import com.tcosfish.xianyu.model.enums.DeliveryTypeEnum;
import com.tcosfish.xianyu.model.enums.OrderStatusEnum;
import com.tcosfish.xianyu.model.enums.OrderTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author tcosfish
 */
@Data
public class OrderVO {
  @Schema(description = "订单ID", example = "88")
  private Long id;

  @Schema(description = "订单编号", example = "202509141523001")
  private String orderNo;

  @Schema(description = "买家ID", example = "1001")
  private Long buyerId;

  @Schema(description = "卖家ID", example = "2002")
  private Long sellerId;

  @Schema(description = "订单类型", example = "商品")
  private OrderTypeEnum orderType;

  @Schema(description = "订单原价（元）", example = "20.00")
  private BigDecimal totalAmount;

  @Schema(description = "实付金额（元）", example = "15.00")
  private BigDecimal realAmount;

  @Schema(description = "订单状态", example = "待付款")
  private OrderStatusEnum status;

  @Schema(description = "付款时间")
  private LocalDateTime payTime;

  @Schema(description = "完成时间")
  private LocalDateTime finishTime;

  @Schema(description = "议价ID（0 表示直接下单）", example = "33")
  private Long negotiationId;

  @Schema(description = "交付方式", example = "宿舍送达")
  private DeliveryTypeEnum deliveryType;

  @Schema(description = "交付备注")
  private String deliveryRemark;

  @Schema(description = "订单过期时间")
  private LocalDateTime expireTime;

  @Schema(description = "关闭原因")
  private CloseReasonEnum closeReason;

  @Schema(description = "能否取消", example = "true")
  private Boolean canCancel;

  @Schema(description = "能否支付", example = "true")
  private Boolean canPay;

  @Schema(description = "能否确认完成", example = "false")
  private Boolean canConfirm;

  @Schema(description = "明细列表")
  private List<OrderItemVO> items;

  @Schema(description = "创建时间")
  private LocalDateTime createtime;

  @Schema(description = "更新时间")
  private LocalDateTime updatetime;
}
