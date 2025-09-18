package com.tcosfish.xianyu.event;

import com.tcosfish.xianyu.model.entity.TraceLogRecord;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author tcosfish
 * @description 日志事件
 */
@Getter
public class TraceLogEvent extends ApplicationEvent {
  private final TraceLogRecord log;

  public TraceLogEvent(Object source, TraceLogRecord log) {
    super(source);
    this.log = log;
  }
}
