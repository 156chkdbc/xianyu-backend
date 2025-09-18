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
public enum CloseReasonEnum implements IEnum<Integer> {

  USER_CANCEL(1, "用户取消"),
  TIMEOUT(2, "超时未支付"),
  SELLER_REJECT(3, "卖家拒单");

  @EnumValue
  private final Integer code;
  private final String desc;

  @Override
  public Integer getValue() {
    return code;
  }

  public static CloseReasonEnum of(Integer code) {
    for (CloseReasonEnum e : values()) {
      if (e.code.equals(code)) {
        return e;
      }
    }
    return null;
  }
}