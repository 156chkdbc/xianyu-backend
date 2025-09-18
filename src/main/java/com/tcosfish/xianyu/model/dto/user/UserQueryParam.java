package com.tcosfish.xianyu.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author tcosfish
 * @apiNote 筛选条件 + 分页
 */
@Data
public class UserQueryParam {
  @Min(value = 1, message = "userId 必须为正整数")
  private Long userId;

  @Email
  private String email;

  @Length(max = 16, message = "用户名长度不能超过 16 个字符")
  private String username;

  @NotNull(message = "page 不能为空")
  @Min(value = 1, message = "page 必须为正整数")
  private Integer page;

  @NotNull(message = "pageSize 不能为空")
  @Min(value = 1, message = "pageSize 必须为正整数")
  @Max(value = 200, message = "pageSize 不能超过 200")
  private Integer pageSize;
}
