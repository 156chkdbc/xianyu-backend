package com.tcosfish.xianyu.model.vo.product;

import lombok.Getter;

import java.util.stream.Stream;

/**
 * @author tcosfish
 */
@Getter
public enum UnitEnum {
  PIECE("piece", "件"),
  HOUR("hour", "小时"),
  DAY("day", "天"),
  SERVICE("service", "次");   // 后续可扩展

  private final String code;
  private final String desc;

  UnitEnum(String code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  /**
   * 通过 code 拿枚举，找不到默认返回 PIECE
   */
  public static UnitEnum of(String code) {
    return Stream.of(values())
      .filter(e -> e.code.equalsIgnoreCase(code))
      .findFirst()
      .orElse(PIECE);
  }
}
