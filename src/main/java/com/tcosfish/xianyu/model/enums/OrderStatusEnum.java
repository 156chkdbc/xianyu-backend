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
public enum OrderStatusEnum implements IEnum<Integer> {

  WAIT_PAY(1, "待付款"),
  PAID(2, "已付款"),
  FINISHED(3, "已完成"),
  CANCELLED(4, "已取消");

  @EnumValue
  private final Integer code;
  private final String desc;

  @Override
  public Integer getValue() {
    return code;
  }

  public static OrderStatusEnum of(Integer code) {
    for (OrderStatusEnum e : values()) {
      if (e.code.equals(code)) {
        return e;
      }
    }
    return null;
  }
}