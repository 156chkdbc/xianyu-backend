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
public enum DeliveryTypeEnum implements IEnum<Integer> {

  SELF_PICK(1, "线下自取"),
  DORM_DELIVER(2, "宿舍送达"),
  EXPRESS(3, "快递");

  @EnumValue
  private final Integer code;
  private final String desc;

  @Override
  public Integer getValue() {
    return code;
  }

  public static DeliveryTypeEnum of(Integer code) {
    for (DeliveryTypeEnum e : values()) {
      if (e.code.equals(code)) {
        return e;
      }
    }
    return null;
  }
}
