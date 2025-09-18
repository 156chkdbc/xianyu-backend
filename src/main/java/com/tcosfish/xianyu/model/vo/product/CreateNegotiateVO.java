package com.tcosfish.xianyu.model.vo.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author tcosfish
 */
@Data
public class CreateNegotiateVO {

  @Schema(description = "议价记录ID")
  private Long negotiationId;

  @Schema(description = "商品ID")
  private Long itemId;

  @Schema(description = "买家出价")
  private java.math.BigDecimal price;

  @Schema(description = "当前状态：1待回应 2已接受 3已拒绝 4已失效 5已取消")
  private Integer status;

  @Schema(description = "本轮议价轮次")
  private Integer round;

  @Schema(description = "有效期截止时间")
  private LocalDateTime expireAt;
}
