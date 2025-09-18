package com.tcosfish.xianyu.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author tcosfish
 */
@Getter
@AllArgsConstructor
public enum ProductItemType implements IEnum<Integer> {

  PRODUCT(1, "商品"),
  SKILL(2, "服务");

  @EnumValue
  private final Integer code;
  private final String desc;

  @Override
  public Integer getValue() {
    return code;
  }

  public static ProductItemType of(Integer code) {
    for (ProductItemType e : values()) {
      if (e.code.equals(code)) {
        return e;
      }
    }
    return null;
  }
}
