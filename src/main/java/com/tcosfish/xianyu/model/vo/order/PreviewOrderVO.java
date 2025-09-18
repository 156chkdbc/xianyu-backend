package com.tcosfish.xianyu.model.vo.order;

import com.tcosfish.xianyu.model.enums.DeliveryTypeEnum;
import com.tcosfish.xianyu.model.enums.ProductItemType;
import com.tcosfish.xianyu.model.vo.product.ProductNegotiateVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author tcosfish
 */
@Data
public class PreviewOrderVO {
  @Schema(description = "指定商品")
  private ItemVO item;

  @Schema(description = "指定商品相关的议价")
  private ProductNegotiateVO negotiate;

  @Schema(description = "依据历史订单, 默认选中的支付方式")
  private DeliveryTypeEnum defaultDeliveryType;

  @Schema(description = "该商品剩余的库存")
  private Integer maxQuantity;

  @Schema(description = "价格快照")
  private BigDecimal setFinalPrice;

  @Schema(description = "卖家支持的发货选项, 可选范围")
  private List<DeliveryTypeEnum> deliveryOptions;

  @Data
  static public class ItemVO {
    @Schema(description = "商品编号")
    private Long id;

    @Schema(description = "")
    private String title;

    @Schema(description = "")
    private BigDecimal price;

    @Schema(description = "")
    private Integer stock;

    @Schema(description = "")
    private String unit;

    @Schema(description = "")
    private ProductItemType itemType;

    @Schema(description = "")
    private Long sellerId;
  }
}
