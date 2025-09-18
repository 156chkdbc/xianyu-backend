package com.tcosfish.xianyu.utils;

/**
 * @author tcosfish
 */
public class StudentNoUtil {
  static public String generator(Long userId) {
    int l = userId.toString().length();
    int n = 3;
    StringBuilder code = new StringBuilder();
    while(n-l>0) {
      code.append("0");
      n--;
    }
    code.append(userId);
    return "2025"+code+"001";
  }
}
