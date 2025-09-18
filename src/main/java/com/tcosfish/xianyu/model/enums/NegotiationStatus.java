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
public enum NegotiationStatus implements IEnum<Integer> {

  PENDING(1, "待回应"),
  ACCEPTED(2, "已接受"),
  REJECTED(3, "已拒绝"),
  EXPIRED(4, "已失效"),
  CANCELLED(5, "已取消");

  @EnumValue // 存入数据库的值
  private final Integer code;
  private final String desc;

  @Override
  public Integer getValue() {
    return code;
  }
}
