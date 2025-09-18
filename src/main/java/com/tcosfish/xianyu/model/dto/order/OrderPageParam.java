package com.tcosfish.xianyu.model.dto.order;

/**
 * @author tcosfish
 */

import com.tcosfish.xianyu.model.enums.OrderColumnEnum;
import com.tcosfish.xianyu.model.enums.OrderStatusEnum;
import com.tcosfish.xianyu.model.enums.OrderTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 订单分页查询参数
 * @author tcosfish
 */
@Data
public class OrderPageParam implements Serializable {

  @Schema(description = "查询视角：buyer | seller", example = "buyer", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull
  private String role;

  @Schema(description = "订单类型：1-商品 2-服务", example = "1")
  private OrderTypeEnum orderType;

  @Schema(description = "订单状态：1-待付款 2-已付款 3-已完成 4-已取消")
  private OrderStatusEnum status;

  @Schema(description = "开始日期（yyyy-MM-dd）", example = "2025-09-01")
  private LocalDate dateFrom;

  @Schema(description = "结束日期（yyyy-MM-dd）", example = "2025-09-15")
  private LocalDate dateTo;

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
  @Schema(description = "排序字段名", example = "排序字段：CREATETIME | PAY_TIME | REAL_AMOUNT")
  private OrderColumnEnum orderByColumn;

  @AssertTrue(message = "isAsc 只能为 true 或 false")
  @Schema(description = "是否升序；true 升序，false 降序；默认 true", example = "true", hidden = true)
  private Boolean isAsc;
}
