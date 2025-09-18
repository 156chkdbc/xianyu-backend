package com.tcosfish.xianyu.task.order;

import com.tcosfish.xianyu.utils.EventPublisherUtil;
import com.tcosfish.xianyu.event.OrderTimeoutEvent;
import jakarta.annotation.Resource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * @author tcosfish
 * @description 延迟关闭订单 -- 生产商
 */
@Component
public class DelayCloseProducer {

  @Resource(name = "businessExecutor")
  private ThreadPoolTaskScheduler scheduler;

  public void send(Long orderId, long delay, TimeUnit unit) {
    scheduler.schedule(
      () -> EventPublisherUtil.publishEvent(new OrderTimeoutEvent(this, orderId)),
      Instant.now().plus(delay, unit.toChronoUnit())
    );
  }
}
