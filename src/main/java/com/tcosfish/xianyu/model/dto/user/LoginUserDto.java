package com.tcosfish.xianyu.model.dto.user;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author tcosfish
 */
@Data
public class LoginUserDto {
  @NotBlank()
  private String username;

  @Email(message = "邮箱格式不正确")
  private String email;

  @NotBlank(message = "密码不能为空")
  @Size(min = 6, max = 32, message = "密码长度必须在 6 到 32 个字符之间")
  private String password;

  @AssertTrue(message = "账号和邮箱必须至少提供一个")
  private boolean isValidLogin() {
    return username != null || email != null;
  }
}
