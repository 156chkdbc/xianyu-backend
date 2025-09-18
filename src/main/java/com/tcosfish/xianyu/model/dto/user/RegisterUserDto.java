package com.tcosfish.xianyu.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author tcosfish
 */
@Data
public class RegisterUserDto {
  /**
   * 用户昵称
   * 必填，长度 1-16，支持中文、字母、数字、下划线、分隔符
   */
  @NotBlank(message = "用户名不能为空")
  @Size(max = 16, message = "用户名长度不能超过 16 个字符")
  @Pattern(regexp = "^[\\u4e00-\\u9fa5_a-zA-Z0-9\\-.]+$", message = "用户名只能包含中文、字母、数字、下划线、分隔符")
  private String username;

  /**
   * 登录密码
   * 必填，长度 6-32
   */
  @NotBlank(message = "密码不能为空")
  @Size(min = 6, max = 32, message = "密码长度必须在 6 到 32 个字符之间")
  private String password;

  /**
   * 用户号码
   * 必填，长度 6-32，支持字母、数字和下划线
   */
  @NotBlank(message = "用户号码不能为空")
  @Size(min = 6, max = 32, message = "号码长度必须在 6 到 32 个字符之间")
  @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "号码只能包含字母、数字和下划线")
  private String phone;

  /**
   * 邮箱, 必填
   */
  @NotBlank(message = "邮箱不能为空")
  @Email(message = "邮箱格式不正确")
  private String email;

  /**
   * 验证码
   */
  @Size(min = 6, max = 6, message = "验证码长度必须为6位")
  private String verifyCode;
}
