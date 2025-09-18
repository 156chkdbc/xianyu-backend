package com.tcosfish.xianyu.model.dto.product;

import com.tcosfish.xianyu.model.enums.ProductItemType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author tcosfish
 */
@Data
@Schema(description = "发布商品/服务参数")
public class CreateProductParam {

  @NotNull(message = "类目ID不能为空")
  @Positive(message = "类目ID必须为正数")
  @Schema(description = "类目ID", example = "12", requiredMode = Schema.RequiredMode.REQUIRED)
  private Long categoryId;

  @NotBlank(message = "标题不能为空")
  @Size(max = 200, message = "标题长度不能超过200字符")
  @Schema(description = "商品/服务标题", example = "高等数学一对一辅导", requiredMode = Schema.RequiredMode.REQUIRED)
  private String title;

  @Size(max = 2000, message = "描述长度不能超过2000字符")
  @Schema(description = "详细描述", example = "985高校数学系在读，可上门授课")
  private String description;

  @NotNull(message = "单价不能为空")
  @DecimalMin(value = "0.01", message = "单价必须≥0.01")
  @Digits(integer = 8, fraction = 2, message = "单价格式错误，整数≤8位，小数≤2位")
  @Schema(description = "单价（元）", example = "99.90", requiredMode = Schema.RequiredMode.REQUIRED)
  private BigDecimal price;

  @NotBlank(message = "单位不能为空")
  @Pattern(regexp = "^(piece|hour|day)$", message = "单位只能是 piece、hour、day 之一")
  @Schema(description = "计价单位", example = "hour", requiredMode = Schema.RequiredMode.REQUIRED)
  private String unit;

  @NotNull(message = "库存不能为空")
  @Min(value = 0, message = "库存不能小于0")
  @Max(value = 99999, message = "库存不能超过99999")
  @Schema(description = "库存/可预约次数", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
  private Integer stock;

  @NotNull(message = "是否可议价不能为空")
  @Min(value = 0, message = "是否可议价值非法")
  @Max(value = 1, message = "是否可议价值非法")
  @Schema(description = "是否可议价：0 不可议价，1 可议价", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
  private Integer negotiable;

  @NotNull(message = "项目类型不能为空")
  @Schema(description = "项目类型", example = "skill", requiredMode = Schema.RequiredMode.REQUIRED)
  private ProductItemType itemType;
}