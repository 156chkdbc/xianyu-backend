package com.tcosfish.xianyu.controller;

import com.tcosfish.xianyu.model.base.ApiResponse;
import com.tcosfish.xianyu.model.base.EmptyVO;
import com.tcosfish.xianyu.model.dto.order.CreateOrderParam;
import com.tcosfish.xianyu.model.dto.order.OrderPageParam;
import com.tcosfish.xianyu.model.dto.order.PreviewOrderParam;
import com.tcosfish.xianyu.model.vo.order.DeliveryVO;
import com.tcosfish.xianyu.model.vo.order.OrderVO;
import com.tcosfish.xianyu.model.vo.order.PreviewOrderVO;
import com.tcosfish.xianyu.model.vo.order.ReviewVO;
import com.tcosfish.xianyu.service.impl.OrderServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author tcosfish
 */
@Tag(name = "订单相关", description = "订单相关服务")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
  private final OrderServiceImpl orderService;

  @GetMapping("/preview")
  @Operation(summary = "结算页预览", description = "针对单个商品/服务的结算页预览（预校验库存、价格快照）, 需要批量时再用批量接口")
  public ApiResponse<PreviewOrderVO> orderPreview(
    @Valid @ModelAttribute PreviewOrderParam param
    ) {
    return orderService.orderPreview(param);
  }

  @PostMapping("")
  @Operation(summary = "创建订单", description = "创建订单（直接下单 or 议价成交）")
  public ApiResponse<OrderVO> createOrder(
    @Valid @RequestBody CreateOrderParam param
    ) {
    return orderService.createOrder(param);
  }

  @GetMapping("")
  @Operation(summary = "订单列表", description = "获取订单列表")
  public ApiResponse<List<OrderVO>> orders(
    @Valid @ModelAttribute OrderPageParam param
    ) {
    return orderService.getOrderList(param);
  }

  @GetMapping("/{orderId}")
  @Operation(summary = "订单详情", description = "获取订单详情")
  public ApiResponse<OrderVO> orderDetails(
    @Min(value = 1, message = "订单编号必须是正整数") @PathVariable Long orderId
  ) {
    return orderService.orderDetailById(orderId);
  }

  @PutMapping("/{orderId}/cancel")
  @Operation(summary = "取消订单", description = "买家/卖家取消订单")
  public ApiResponse<EmptyVO> cancelOrder(
    @Min(value = 1, message = "订单编号必须是正整数") @PathVariable Long orderId
  ) {
    return orderService.cancelOrder(orderId);
  }

  @PostMapping("/{orderId}/payment")
  @Operation(summary = "订单支付", description = "创建支付单")
  public ApiResponse<EmptyVO> paymentOrder(
    @Min(value = 1, message = "订单编号必须是正整数") @PathVariable Long orderId
  ) {
//    return orderService.paymentOrder(orderId);
    return null;
  }

  @PutMapping("/{orderId}/completion")
  @Operation(summary = "确认完成", description = "确认完成（双向均可，仅“已付款”状态可触发）")
  public ApiResponse<EmptyVO> completionOrder(
    @Min(value = 1, message = "订单编号必须是正整数") @PathVariable Long orderId
  ) {
    return orderService.completionOrder(orderId);
  }

  @GetMapping("/{orderId}/delivery")
  @Operation(summary = "查看交付信息", description = "返回交付方式、二维码、取件码等扩展字段")
  public ApiResponse<DeliveryVO> deliveryOrder(
    @Min(value = 1, message = "订单编号必须是正整数") @PathVariable Long orderId
  ) {
    return orderService.getDeliverOrder(orderId);
  }

  @PostMapping("/{orderId}/reviews")
  @Operation(summary = "获取订单评价", description = "返回双向评价列表（买家评卖家、卖家评买家）")
  public ApiResponse<ReviewVO> reviewsOrder(
    @Min(value = 1, message = "订单编号必须是正整数") @PathVariable Long orderId
  ) {
    return orderService.getReviewOrder(orderId);
  }
}
