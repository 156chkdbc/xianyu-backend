package com.tcosfish.xianyu.model.vo.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author tcosfish
 * @description 热度榜参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotProductVO {
  private Long    productId;
  private String  title;
  private BigDecimal price;
  private Integer views;   // 实时值
}
