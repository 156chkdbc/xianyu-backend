package com.tcosfish.xianyu.model.vo.order;

import com.tcosfish.xianyu.model.enums.DeliveryTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author tcosfish
 */
@Data
public class DeliveryVO {
  @Schema(description = "交付方式：1-线下自取 2-宿舍送达 3-快递")
  private DeliveryTypeEnum deliveryType;

  @Schema(description = "交付备注")
  private String deliveryRemark;

  @Schema(description = "取件码（宿舍送达时）", example = "7392")
  private String pickupCode;

  @Schema(description = "二维码 URL（可选）", example = "https://cdn.xxx/qr/7392.png")
  private String qrUrl;
}
