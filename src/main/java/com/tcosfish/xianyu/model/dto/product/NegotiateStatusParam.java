package com.tcosfish.xianyu.model.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

/**
 * @author tcosfish
 */
@Data
public class NegotiateStatusParam {
  @NotNull
  @Schema(description = "1 接受  2 拒绝", requiredMode = REQUIRED)
  private Integer status;   // 1 accept  2 reject
}
