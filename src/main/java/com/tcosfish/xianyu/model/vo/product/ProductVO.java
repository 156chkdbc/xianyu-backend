package com.tcosfish.xianyu.model.vo.product;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
/**
 * @author tcosfish
 * 商品卡片信息-返回前端
 */
@Data
@Schema(description = "商品卡片信息")
public class ProductVO implements Serializable {

  // “只要你的类实现了 Serializable，就顺手写个 serialVersionUID = 1L，能救命。”
  // 在 VO 里它现在可能用不上，但缓存、RPC、消息场景一旦接入，就能避免“神秘 InvalidClassException”。
  @Serial
  private static final long serialVersionUID = 1L;

  @Schema(description = "商品主键", example = "123456")
  private Long id;

  @Schema(description = "标题", example = "高等数学二手教材")
  private String title;

  @Schema(description = "描述", example = "九成新，无笔记")
  private String description;

  @Schema(description = "单价-字符串（已带单位）", example = "12.50元/件")
  private String priceLabel;

  @Schema(description = "库存/可预约次数", example = "3")
  private Integer stock;

  @Schema(description = "是否可议价", example = "可议价")
  private String negotiableLabel;

  @Schema(description = "商品类型", example = "PRODUCT")
  private String itemType;

  @Schema(description = "状态", example = "在售")
  private String statusLabel;

  @Schema(description = "浏览量", example = "1024")
  private Integer viewCount;

  @Schema(description = "发布时间", example = "2025-09-10 14:23:56")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private String createTime;

  /* ====== 卖家冗余字段 ====== */
  @Schema(description = "卖家昵称", example = "鱼小二")
  private String sellerNick;

  @Schema(description = "卖家头像", example = "https://cdn.xianyu.com/avatar.jpg")
  private String sellerAvatar;
}