package com.tcosfish.xianyu.aspect;

import com.alibaba.fastjson2.JSON;
import com.tcosfish.xianyu.annotation.TraceLog;
import com.tcosfish.xianyu.event.TraceLogEvent;
import com.tcosfish.xianyu.model.entity.TraceLogRecord;
import com.tcosfish.xianyu.scope.RequestScopeData;
import com.tcosfish.xianyu.service.TraceLogRecordService;
import com.tcosfish.xianyu.utils.EventPublisherUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * @author tcosfish
 * @apiNote 切面日志文件记录
 */
@Aspect
@Component
// 前往配置寻找 trace.log.enabled, 默认为true
@ConditionalOnProperty(name = "trace.log.enabled", havingValue = "true", matchIfMissing = true)
public class TraceLogAspect {
  private static final String TRACE_ID_KEY = "traceId";

  @Resource
  private HttpServletRequest request;

  @Resource
  private RequestScopeData requestScopeData;

  @Resource // 在容器中寻找对应名称的Bean
  private TraceLogRecordService traceLogRecordService;

  @Around("@annotation(traceLog)")
  public Object around(ProceedingJoinPoint joinPoint, TraceLog traceLog) throws Throwable {

    TraceLogRecord record = new TraceLogRecord();
    record.setTraceId(MDC.get(TRACE_ID_KEY));
    record.setUserId(getUserId()); // 你的登录上下文
    record.setIp(getClientIp());
    record.setClazz(joinPoint.getTarget().getClass().getSimpleName());
    record.setMethod(joinPoint.getSignature().getName());
    record.setDescription(traceLog.desc());

    if (traceLog.includeParams()) {
      // 处理参数中的MultipartFile
      Object[] args = joinPoint.getArgs();
      List<Object> processedArgs = new ArrayList<>();
      for (Object arg : args) {
        if (arg instanceof MultipartFile file) {
          // 只保留文件元信息，避免序列化二进制内容
          Map<String, Object> fileInfo = new HashMap<>();
          fileInfo.put("fileName", file.getOriginalFilename());
          fileInfo.put("contentType", file.getContentType());
          fileInfo.put("size", file.getSize());
          processedArgs.add(fileInfo);
        } else {
          processedArgs.add(arg);
        }
      }
      // 序列化处理后的参数并脱敏
      record.setParams(desensitize(JSON.toJSONString(processedArgs)));
    }

    long start = System.currentTimeMillis();
    Object result = null;
    Throwable ex = null;
    try {
      result = joinPoint.proceed();
      return result;
    } catch (Throwable e) {
      ex = e;
      throw e;
    } finally {
      long cost = System.currentTimeMillis() - start;
      record.setCost((int) cost);
      record.setSuccess(ex == null ? 1 : 0);
      record.setErrMsg(ex == null ? "" : ExceptionUtils.getRootCauseMessage(ex));
      if (traceLog.includeReturn() && result != null) {
        record.setResult(JSON.toJSONString(result));
      }
      // 发送日志提交事件, 通过LogTaskConsumer监听到进行处理
      // traceLogRecordService.asyncSave(record);
      EventPublisherUtil.publishEvent(new TraceLogEvent(this, record));
    }
  }

  /** 简易脱敏：把手机号、身份证、邮箱等替换成* */
  private String desensitize(String json) {
    // 用正则或 JSONPath 均可，这里示例
    return json.replaceAll("(\"phone\"\\s*:\\s*\")\\d{4}", "$1****");
  }

  private Long getUserId() {
    // 从 SecurityContext / Session / JWT 解析
    return Optional.ofNullable(requestScopeData.getUserId())
      .orElse(0L);
  }

  private String getClientIp() {
    return Optional.ofNullable(request.getHeader("X-Forwarded-For"))
      .map(ip -> ip.split(",")[0].trim())
      .orElse(request.getRemoteAddr());
  }
}
