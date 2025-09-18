package com.tcosfish.xianyu.model.vo.product;

import com.tcosfish.xianyu.model.enums.NegotiationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author tcosfish
 */
@Data
public class NegotiateStatusVO {
  @Schema(description = "议价ID")
  private Long negotiationId;

  @Schema(description = "最新状态 2已接受 3已拒绝")
  private NegotiationStatus status;

  @Schema(description = "接受时才返回订单号")
  private String orderNo;   // 拒绝时 null
}
