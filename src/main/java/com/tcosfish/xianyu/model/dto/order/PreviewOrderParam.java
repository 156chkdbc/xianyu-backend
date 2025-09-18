package com.tcosfish.xianyu.model.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.io.Serializable;

/**
 * @author tcosfish
 */
@Data
public class PreviewOrderParam implements Serializable {
  @Schema(description = "商品id")
  @Positive
  private Long productId;

  @Schema(description = "议价id")
  @Positive
  private Long negotiateId;
}
