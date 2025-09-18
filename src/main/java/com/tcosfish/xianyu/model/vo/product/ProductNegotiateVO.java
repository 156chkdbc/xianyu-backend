package com.tcosfish.xianyu.model.vo.product;

import com.tcosfish.xianyu.model.enums.NegotiationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author tcosfish
 */
@Data
public class ProductNegotiateVO {
  @Schema(description = "议价记录ID")
  private Long negotiationId;

  @Schema(description = "买家ID")
  private Long buyerId;

  @Schema(description = "买家昵称（脱敏）, 脱敏 = 把数据变成“够用但看不懂是谁”")
  private String buyerNick;

  @Schema(description = "出价")
  private BigDecimal price;

  @Schema(description = "留言")
  private String message;

  @Schema(description = "状态")
  private NegotiationStatus status;

  @Schema(description = "轮次")
  private Integer round;

  @Schema(description = "有效期截止")
  private LocalDateTime expireAt;

  @Schema(description = "创建时间")
  private LocalDateTime createTime;
}
