package com.tcosfish.xianyu.model.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * @author tcosfish
 */
@Data
public class CreateCategoryDto {
  @Schema(description = "类别名称")
  private String name;

  @Schema(description = "夫类别ID, 最高为0")
  @Min(value = 0, message = "最高级别为0")
  private Long parentId;
}
