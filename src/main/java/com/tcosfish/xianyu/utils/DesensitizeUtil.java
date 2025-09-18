package com.tcosfish.xianyu.utils;

/**
 * @author tcosfish
 */
public class DesensitizeUtil {
  /**
   * 返回掩码后的 username + phone
   * 规则：
   *  username 保留首末字符，中间用 * 填充，最少 2 个字符
   *  phone 保留前 3 后 4，中间用 **** 填充
   */
  public static String desensitize(String username, String phone) {
    if (username == null) {
      username = "";
    }
    if (phone == null) {
      phone = "";
    }

    // username: 首 + *** + 末
    int uLen = username.length();
    String userMask = uLen <= 2 ? username :
      username.charAt(0) + "*".repeat(uLen - 2) + username.charAt(uLen - 1);

    // phone: 138****8000
    String phoneMask = phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");

    return userMask + " " + phoneMask;
  }
}
