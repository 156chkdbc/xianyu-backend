package com.tcosfish.xianyu.task.traceLog;

import com.tcosfish.xianyu.event.TraceLogEvent;
import com.tcosfish.xianyu.model.entity.TraceLogRecord;
import com.tcosfish.xianyu.service.impl.TraceLogRecordServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author tcosfish
 * @description 日志任务消费者
 */
@Component
public class LogTaskConsumer {

  @Resource
  private TraceLogRecordServiceImpl traceLogRecordService;

  @EventListener
  @Async("logExecutor")   // 独立小池子，不影响业务
  public void handle(TraceLogEvent event) {
    TraceLogRecord record = event.getLog();
    if (record.getCreateTime() == null) {
      record.setCreateTime(new Date());
    }
    // 日志验证
    System.out.println(">>>> async thread: " + Thread.currentThread().getName());

    traceLogRecordService.save(record);
  }
}
