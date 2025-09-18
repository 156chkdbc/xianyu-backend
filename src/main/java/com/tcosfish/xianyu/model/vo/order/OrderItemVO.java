package com.tcosfish.xianyu.model.vo.order;

import com.tcosfish.xianyu.model.enums.OrderItemTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author tcosfish
 */
@Data
public class OrderItemVO {
  @Schema(description = "明细ID", example = "101")
  private Long id;

  @Schema(description = "明细类型：商品 | 服务", example = "商品")
  private OrderItemTypeEnum itemType;

  @Schema(description = "商品/服务ID", example = "123")
  private Long itemId;

  @Schema(description = "标题快照", example = "线性代数教材（第九版）")
  private String itemTitle;

  @Schema(description = "成交单价（元）", example = "15.00")
  private BigDecimal unitPrice;

  @Schema(description = "数量/时长", example = "1")
  private Integer quantity;

  @Schema(description = "小计金额（元）", example = "15.00")
  private BigDecimal amount;
}
