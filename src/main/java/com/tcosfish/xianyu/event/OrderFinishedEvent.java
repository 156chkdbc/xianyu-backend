package com.tcosfish.xianyu.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author tcosfish
 * @description 订单完成事件（立即投递）
 */
@Getter
public class OrderFinishedEvent extends ApplicationEvent {
  private final Long orderId;
  public OrderFinishedEvent(Object source, Long orderId) {
    super(source);
    this.orderId = orderId;
  }
}
