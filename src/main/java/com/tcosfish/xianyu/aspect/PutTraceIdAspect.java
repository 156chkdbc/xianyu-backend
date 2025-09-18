package com.tcosfish.xianyu.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author tcosfish
 */
@Aspect
@Component
public class PutTraceIdAspect {
  private static final String TRACE_ID_KEY = "traceId";

  /**
   * 切面切入点，拦截所有控制器的方法。
   */
  @Pointcut("@within(org.springframework.stereotype.Controller) || " +
    "@within(org.springframework.web.bind.annotation.RestController)")
  public void controller() {}

  @Before("controller()")
  public void addTraceIdToLog() {
    // 如果当前 MDC 中没有 traceId，则生成一个新的
    if (MDC.get(TRACE_ID_KEY) == null) {
      String traceId = UUID.randomUUID().toString();
      MDC.put(TRACE_ID_KEY, traceId);
    }
  }
}
