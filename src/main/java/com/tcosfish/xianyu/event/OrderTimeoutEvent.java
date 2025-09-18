package com.tcosfish.xianyu.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author tcosfish
 * @description 订单超时事件（延迟任务投递）
 */
@Getter
public class OrderTimeoutEvent extends ApplicationEvent {
  private final Long orderId;
  public OrderTimeoutEvent(Object source, Long orderId) {
    super(source);
    this.orderId = orderId;
  }
}
