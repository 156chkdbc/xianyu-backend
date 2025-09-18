package com.tcosfish.xianyu.model.vo.order;

import com.tcosfish.xianyu.model.enums.OrderItemTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author tcosfish
 */
@Data
public class ReviewVO {
  @Schema(description = "评价 ID")
  private Long id;

  @Schema(description = "评价人用户 ID")
  private Long reviewerId;

  @Schema(description = "被评价人用户 ID")
  private Long revieweeId;

  @Schema(description = "1-商品 2-服务")
  private OrderItemTypeEnum itemType;

  @Schema(description = "订单 ID")
  private Long orderId;

  @Schema(description = "评分 1-5")
  private Integer score;

  @Schema(description = "评价内容")
  private String content;

  @Schema(description = "评价时间")
  private Date createtime;
}
