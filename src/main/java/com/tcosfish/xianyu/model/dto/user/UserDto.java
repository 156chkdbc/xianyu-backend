package com.tcosfish.xianyu.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author tcosfish
 */
@Data
public class UserDto {

  @Schema(description = "用户名", example = "小鱼儿")
  @Pattern(regexp = "^[a-zA-Z0-9_\\u4e00-\\u9fa5]+$", message = "用户名只能包含字母、数字、下划线或中文")
  @Length(min = 2, max = 16, message = "用户名长度必须在 2-16 位之间")
  private String username;

  @Schema(description = "手机号", example = "13812345678")
  @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
  private String phone;

  @Schema(description = "邮箱", example = "demo@tcosfish.com")
  @Email(message = "邮箱格式不正确")
  private String email;
}
