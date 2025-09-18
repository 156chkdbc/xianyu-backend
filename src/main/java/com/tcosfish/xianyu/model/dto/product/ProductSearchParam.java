package com.tcosfish.xianyu.model.dto.product;

import com.tcosfish.xianyu.model.enums.ProductItemType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * @author tcosfish
 */
@Data
@Schema(description = "商品搜索参数")
public class ProductSearchParam {
  @Size(max = 64, message = "标题关键词不能超过64个字符")
  @Schema(description = "模糊搜索-标题关键词", example = "高等数学")
  private String keyword;

  @Positive
  @Schema(description = "发布者用户ID", example = "123456")
  private Long sellerId;

  @Positive
  @Schema(description = "项目分类ID", example = "12")
  private Long categoryId;

  @NotNull
  @Schema(description = "项目类型枚举值", example = "product")
  private ProductItemType itemType;

  @Min(0) @Max(1)
  @Schema(description = "是否可议价：0 不可议价，1 可议价", example = "1")
  private Integer negotiable;

  @Schema(description = "价格区间下限")
  private Double priceFrom;

  @Schema(description = "价格区间上限")
  private Double priceTo;

  /* ====== 分页参数 ====== */

  @NotNull(message = "page 不能为空")
  @Min(value = 1, message = "page 必须≥1")
  @Schema(description = "页码，从 1 开始", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
  private Integer page;

  @NotNull(message = "pageSize 不能为空")
  @Min(1) @Max(200)
  @Schema(description = "每页条数，最大 200", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
  private Integer pageSize;

  @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "排序字段仅限字母、数字、下划线")
  @Schema(description = "排序字段名", example = "create_time")
  private String orderByColumn;

  @AssertTrue(message = "isAsc 只能为 true 或 false")
  @Schema(description = "是否升序；true 升序，false 降序；默认 true", example = "true", hidden = true)
  private Boolean isAsc;
}
