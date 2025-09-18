package com.tcosfish.xianyu.model.dto.product;

import com.tcosfish.xianyu.model.enums.NegotiationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * @author tcosfish
 */
@Data
public class ProductNegotiateParam {

  @Schema(description = "状态筛选：1待回应 2已接受 3已拒绝 4已失效 5已取消；不传=全部")
  private NegotiationStatus status;

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
