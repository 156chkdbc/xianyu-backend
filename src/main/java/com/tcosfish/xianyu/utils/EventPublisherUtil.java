package com.tcosfish.xianyu.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

/**
 * @author tcosfish
 * @description 事件发布工具（静态方便）
 */
@Component
public class EventPublisherUtil implements ApplicationContextAware {
  private static ApplicationContext context;
  @Override
  public void setApplicationContext(ApplicationContext ac) {
    context = ac;
  }
  public static void publishEvent(ApplicationEvent event) {
    context.publishEvent(event);
  }
}
