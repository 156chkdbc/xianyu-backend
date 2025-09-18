package com.tcosfish.xianyu.task.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcosfish.xianyu.event.EmailSendEvent;
import com.tcosfish.xianyu.model.enums.RedisKey;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.concurrent.TimeUnit;

/**
 * @author tcosfish
 * 邮件任务消费者 —— 完整可运行版本（Spring Boot 3.5.5）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailTaskConsumer implements SmartLifecycle, EmailTaskConsumerInterface  {

  private final RedisTemplate<String, String> redisTemplate;
  private final ObjectMapper objectMapper;
  private final JavaMailSender javaMailSender;
  private final TemplateEngine templateEngine;
  private final ThreadPoolTaskScheduler scheduler;
  private final ApplicationEventPublisher eventPublisher;

  /* ----------- 可配置项 ----------- */
  @Value("${spring.mail.username}")
  private String from;

  @Value("${mail.verify-code.template-path}")
  private String templatePath;

  @Value("${email.consumer.block-time-seconds:30}")
  private long blockTimeSeconds;

  /* ----------- 运行状态 ----------- */
  private volatile boolean running = false;   // SmartLifecycle 接口要求
  private volatile Thread consumerThread;

  /* ========== SmartLifecycle  ========== */
  @Override
  public int getPhase() {
    // 足够晚，但比 LettuceConnectionFactory 早（默认 phase=Integer.MAX_VALUE）
    return Integer.MAX_VALUE - 100;
  }

  @Override
  public boolean isAutoStartup() {
    return true;
  }

  @Override
  public void start() {
    running = true;
    scheduler.execute(() -> {
      consumerThread = Thread.currentThread();
      log.info("邮件消费者线程启动：{}", consumerThread.getName());
      try {
        processEmailTasks();
      } finally {
        consumerThread = null;
        log.info("邮件消费者线程退出：{}", Thread.currentThread().getName());
      }
    });
  }

  @Override
  public void stop() {
    stopGracefully();
  }

  @Override
  public boolean isRunning() {
    return running;
  }

  /* ========== 优雅停止 ========== */
  private void stopGracefully() {
    log.info("开始优雅停止邮件消费者...");
    running = false;                 // 1. 发信号

    Thread ct = consumerThread;      // 2. 快照，避免并发置空
    if (ct == null) {                // 3. 已经自己跑完了
      log.info("消费者线程已结束，无需额外处理");
      return;
    }
    if (!ct.isAlive()) {             // 4. 虽然非 null，但已执行完毕
      log.info("消费者线程已死亡");
      return;
    }

    // 5. 真正需要中断并等待
    ct.interrupt();
    log.info("已中断消费者线程：{}", ct.getName());
    try {
      ct.join(5000);
      if (ct.isAlive()) {
        log.warn("消费者线程 {} 超时未退出", ct.getName());
      } else {
        log.info("消费者线程 {} 正常退出", ct.getName());
      }
    } catch (InterruptedException e) {
      log.warn("等待线程退出时被中断", e);
      Thread.currentThread().interrupt();
    }
    log.info("邮件消费者停止完成");
  }

  /* ========== 任务循环 ========== */
  private void processEmailTasks() {
    String queueKey = RedisKey.emailTaskQueue();
    long base = blockTimeSeconds;
    long max = 30;
    long current = base;

    while (running) {
      try {
        // 1. 探活：连接已关闭就 sleep 再重试
        if (!redisAlive()) {
          Thread.sleep(1000);
          continue;
        }

        // 2. 阻塞 pop（会被中断）
        String json;
        try {
          json = redisTemplate.opsForList()
            .rightPop(queueKey, current, TimeUnit.SECONDS);
        } catch (Exception e) {
          if (Thread.currentThread().isInterrupted()) {
            log.info("线程中断，退出循环");
            break;
          }
          // 非中断异常，打印后继续
          log.error("redis rightPop 异常", e);
          Thread.sleep(1000);
          continue;
        }

        if (json == null) {          // 队列为空
          current = Math.min(current * 2, max);
          continue;
        }

        // 3. 拿到任务 -> 发布事件
        current = base;
        EmailTask task = objectMapper.readValue(json, EmailTask.class);
        eventPublisher.publishEvent(new EmailSendEvent(this, task));

      } catch (InterruptedException e) {
        log.info("线程被中断，正常退出");
        Thread.currentThread().interrupt();
        break;
      } catch (Exception e) {
        log.error("邮件任务处理异常（非中断）", e);
        try {
          Thread.sleep(1000);
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          break;
        }
      }
    }
  }

  /* ========== 轻量级探活 ========== */
  private boolean redisAlive() {
    try {
      redisTemplate.hasKey("ping");
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /* ========== 事件监听（发送邮件） ========== */
  @Override
  @Async("businessExecutor")   // 业务线程池，需自行配置
  @EventListener(EmailSendEvent.class)
  public void handleEmailSendEvent(EmailSendEvent event) {
    EmailTask task = event.getEmailTask();
    int maxRetries = 3;
    int retry = 0;
    boolean ok = false;

    while (retry < maxRetries && !ok) {
      try {
        sendEmail(task);
        ok = true;
        log.info("邮件发送成功: {}", task.getEmail());
      } catch (Exception e) {
        retry++;
        log.error("邮件发送失败，重试 {}/{}", retry, maxRetries, e);
        if (retry < maxRetries) {
          try {
            Thread.sleep(1000L << retry);
          } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            return;
          }
        }
      }
    }
    if (!ok) {
      log.error("邮件多次发送失败，重新入队: {}", task.getEmail());
      try {
        redisTemplate.opsForList()
          .rightPush(RedisKey.emailTaskQueue(), objectMapper.writeValueAsString(task));
      } catch (Exception e) {
        log.error("任务回写队列失败", e);
      }
    }
  }

  /* ========== 真正发邮件 ========== */
  private void sendEmail(EmailTask task) throws MessagingException {
    MimeMessage mime = javaMailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mime, true);
    helper.setFrom(from);
    helper.setTo(task.getEmail());
    helper.setSubject("闲鱼验证码: " + task.getVerificationCode());

    Context ctx = new Context();
    ctx.setVariable("verifyCode", task.getVerificationCode());
    String html = templateEngine.process(templatePath, ctx);
    helper.setText(html, true);
    javaMailSender.send(mime);

    // 验证码写入 Redis，5 min 过期
    redisTemplate.opsForValue()
      .set(RedisKey.registerVerificationCode(task.getEmail()),
        task.getVerificationCode(),
        5, TimeUnit.MINUTES);
  }
}