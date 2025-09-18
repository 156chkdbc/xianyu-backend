package com.tcosfish.xianyu.model.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author tcosfish
 */
@Data
public class CreateNegotiateParam {
  @Schema(description = "买家出价", requiredMode = Schema.RequiredMode.REQUIRED, example = "12.50")
  @NotNull(message = "price 不能为空")
  @DecimalMin(value = "0.01", message = "出价必须 ≥ 0.01")
  @Digits(integer = 8, fraction = 2, message = "价格格式错误")
  private BigDecimal price;

  @Schema(description = "买家留言（可选）", example = "学长，诚心要，能否包邮？")
  @Size(max = 200, message = "留言不能超过200字")
  private String message;
}
