package com.tcosfish.xianyu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author tcosfish
 * @description 线程池统一配置（合并版）, scheduler + Async
 */
@Configuration
@EnableScheduling
public class ExecutorConfig {
  /**
   * 业务延迟任务 + 异步事件（订单关单、完成事件）
   */
  @Bean("businessExecutor")
  public ThreadPoolTaskScheduler businessScheduler() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(5);
    scheduler.setThreadNamePrefix("biz-scheduler-");
    scheduler.setWaitForTasksToCompleteOnShutdown(true);
    scheduler.setAwaitTerminationSeconds(30);
    scheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    return scheduler;
  }

  /**
   * 仅 TraceLog 落库，IO 密集，可丢可重试
   */
  @Bean("logExecutor")
  public ThreadPoolTaskExecutor logExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(4);
    executor.setMaxPoolSize(8);
    executor.setQueueCapacity(2000);
    executor.setThreadNamePrefix("trace-log-");
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.initialize();
    return executor;
  }
}
