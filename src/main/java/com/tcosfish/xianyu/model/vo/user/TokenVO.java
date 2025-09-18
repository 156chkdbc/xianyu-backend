package com.tcosfish.xianyu.model.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author tcosfish
 */
@Schema(description = "token 元数据")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenVO {

  @Schema(description = "访问令牌", example = "eyJhbGciOiJIUzI1NiJ9...")
  @NotBlank
  private String accessToken;

  @Schema(description = "刷新令牌（可选）", example = "a5b6c7d8-e9f0-1111-2222-333344445555")
  private String refreshToken;

  @Schema(description = "令牌类型", example = "Bearer", defaultValue = "Bearer")
  private String tokenType = "Bearer";

  @Schema(description = "过期时间戳（秒）", example = "1728000000")
  private Long expireAt;

  @Schema(description = "剩余有效时间（秒）", example = "7200")
  private Long expiresIn;
}
