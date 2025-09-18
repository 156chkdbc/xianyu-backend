package com.tcosfish.xianyu.task.order;

import com.tcosfish.xianyu.event.OrderFinishedEvent;
import com.tcosfish.xianyu.event.OrderTimeoutEvent;
import com.tcosfish.xianyu.model.entity.Order;
import com.tcosfish.xianyu.model.enums.OrderStatusEnum;
import com.tcosfish.xianyu.service.OrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author tcosfish
 * @description 订单任务消费者
 */
@Slf4j
@Component
public class OrderTaskConsumer {

  @Resource
  private OrderService orderService;

  /* ---------- 1. 延迟关单 ---------- */
  @EventListener  // 加上这个这个注解后, 会监听 OrderTimeEvent类型的事件
  @Async("businessExecutor")   // 异步，使用业务线程池, 指定监听的线程池
  public void onTimeout(OrderTimeoutEvent event) {
    Order order = orderService.getById(event.getOrderId());
    if (order == null) {
      return;
    }
    // 只有"已付款"且 24h 内未完成 → 自动完成
    if (OrderStatusEnum.PAID.equals(order.getStatus())) {
      orderService.autoComplete(order.getId());
      log.info(order.getId() + ", 该订单已自动完成确认");
    }
  }

  /* ---------- 2. 订单完成后处理 ---------- */
  @EventListener
  @Async("businessExecutor")
  public void onFinished(OrderFinishedEvent event) {
    Long orderId = event.getOrderId();
    // 1. 双方发送评价提醒
    // 2. 积分入账
    // 3. 分账调用
    log.info("[异步]订单完成事件处理完毕, orderId={}", orderId);
  }
}
