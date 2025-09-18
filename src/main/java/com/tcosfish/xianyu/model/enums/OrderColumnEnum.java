package com.tcosfish.xianyu.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author tcosfish
 */
@Getter
@AllArgsConstructor
public enum OrderColumnEnum {
  CREATETIME("createtime"),
  PAY_TIME("pay_time"),
  REAL_AMOUNT("real_amount");

  private final String column;

  public static OrderColumnEnum of(String name) {
    for (OrderColumnEnum e : values()) {
      if (e.name().equalsIgnoreCase(name)) {
        return e;
      }
    }
    return null;
  }
}
