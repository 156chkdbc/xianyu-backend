package com.tcosfish.xianyu.model.vo.user;

import lombok.Data;

/**
 * @author tcosfish
 */
@Data
public class LoginUserVO {
  /**
   * 登录账号
   */
  private String username;

  /**
   * 手机号
   */
  private String phone;

  /**
   * 邮箱
   */
  private String email;
}
