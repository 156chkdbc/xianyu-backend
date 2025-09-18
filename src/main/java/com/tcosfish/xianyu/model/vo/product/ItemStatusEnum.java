package com.tcosfish.xianyu.model.vo.product;

import lombok.Getter;

import java.util.stream.Stream;

/**
 * @author tcosfish
 * 商品状态枚举
 */
@Getter
public enum ItemStatusEnum {

  ON_SALE(1, "在售"),
  SOLD(2, "已售/已约"),
  OFF_SHELF(3, "已下架");

  private final Integer code;
  private final String desc;

  ItemStatusEnum(Integer code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public static ItemStatusEnum of(Integer code) {
    return Stream.of(values())
      .filter(e -> e.code.equals(code))
      .findFirst()
      .orElse(ON_SALE);   // 兜底，避免 NPE
  }
}
