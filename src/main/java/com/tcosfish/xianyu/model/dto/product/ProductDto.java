package com.tcosfish.xianyu.model.dto.product;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tcosfish.xianyu.model.enums.ProductItemType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author tcosfish
 */
@Data
public class ProductDto {
  /**
   * 主键
   */
  @TableId(type = IdType.AUTO)
  private Long id;

  /**
   * 标题
   */
  private String title;

  /**
   * 描述
   */
  private String description;

  /**
   * 单价
   */
  private BigDecimal price;

  /**
   * 单位：piece|hour|day
   */
  private String unit;

  /**
   * 库存/可预约次数
   */
  private Integer stock;

  /**
   * 1可议价 0不可
   */
  private Integer negotiable;

  /**
   * 业务形态
   */
  private ProductItemType itemType;

  /**
   * 1上架 2已售/已约 3下架
   */
  private Integer status;

  /**
   * 浏览量
   */
  private Integer viewCount;

  /**
   * 创建时间
   */
  private Date createtime;

  /* ====== 卖家冗余字段 ====== */
  private String sellerNick;

  private String sellerAvatar;
}
