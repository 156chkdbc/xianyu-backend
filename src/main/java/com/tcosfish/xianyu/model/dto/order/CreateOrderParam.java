package com.tcosfish.xianyu.model.dto.order;

import com.tcosfish.xianyu.model.enums.DeliveryTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * @author tcosfish
 */
@Data
public class CreateOrderParam implements Serializable {
  @Schema(description = "商品/服务ID", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull
  private Long itemId;

  @Schema(description = "议价ID，0 代表直接下单", example = "0")
  @PositiveOrZero // 正整数或为0
  private Long negotiationId = 0L;

  @Schema(description = "购买数量/时长（小时）", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
  @Min(1)
  private Integer quantity;

  @Schema(description = "交付方式：1-线下自取 2-宿舍送达 3-快递", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull
  private DeliveryTypeEnum deliveryType;

  @Schema(description = "交付备注（≤200字）", example = "周六下午3点宿舍楼下取件")
  @Size(max = 200)
  private String deliveryRemark;
}
