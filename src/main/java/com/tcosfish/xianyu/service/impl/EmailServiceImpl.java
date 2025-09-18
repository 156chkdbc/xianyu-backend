package com.tcosfish.xianyu.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcosfish.xianyu.event.EmailSendEvent;
import com.tcosfish.xianyu.model.enums.RedisKey;
import com.tcosfish.xianyu.service.EmailService;
import com.tcosfish.xianyu.task.email.EmailTask;
import com.tcosfish.xianyu.utils.EventPublisherUtil;
import com.tcosfish.xianyu.utils.RandomCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author tcosfish
 * @apiNote service 充当接口, 对应的impl负责实现
 */
@Slf4j // 自动进行日志初始化, 直接使用 log即可
@Service
public class EmailServiceImpl implements EmailService {

  private final RedisTemplate<String, String> redisTemplate;

  // 用于处理 JSON 数据的序列化和反序列化
  private final ObjectMapper objectMapper;

  @Value("${mail.verify-code.limit-expire-seconds}")
  private int limitExpireSeconds;

  @Autowired
  public EmailServiceImpl(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
    this.redisTemplate = redisTemplate;
    this.objectMapper = objectMapper;
  }

  @Override
  public String sendVerificationCode(String email) {
    if (isVerificationCodeRateLimited(email)) {
      throw new RuntimeException("验证码发送频繁, 请60s后重试");
    }

    // 随机6位数
    String verificationCode = RandomCodeUtil.generateNumberCode(6);

    // 实现异步发送邮件的逻辑
    try {

      // 创建邮件任务
      EmailTask emailTask = new EmailTask();

      // 初始化邮件任务内容
      // 1. 邮件目的邮箱
      // 2. 验证码
      // 3. 时间戳
      emailTask.setEmail(email);
      emailTask.setVerificationCode(verificationCode);
      emailTask.setTimestamp(System.currentTimeMillis());

      // 往线程池中发送消息
      EventPublisherUtil.publishEvent(new EmailSendEvent(this, emailTask));

      // 旧版本, 将邮件任务存入消息队列
      // 尤其适用于高并发场景或需要异步处理的邮件发送任务, 等待消费者拿出并执行
      // 1. 将任务对象转成 JSON 字符串
      // 2. 将 JSON 字符串保存到 Redis 模拟的消息队列中
//      String emailTaskJson = objectMapper.writeValueAsString(emailTask);
//      String queueKey = RedisKey.emailTaskQueue();
//      redisTemplate.opsForList().leftPush(queueKey, emailTaskJson);

      // 设置 email 发送注册验证码的限制
      String emailLimitKey = RedisKey.registerVerificationLimitCode(email);
      redisTemplate.opsForValue().set(emailLimitKey, "1", limitExpireSeconds, TimeUnit.SECONDS);

      return verificationCode;
    } catch (Exception e) {
      log.error("发送验证码邮件失败", e);
      throw new RuntimeException("发送验证码失败，请稍后重试");
    }
  }

  @Override
  public boolean checkVerificationCode(String email, String code) {
    String redisKey = RedisKey.registerVerificationCode(email);
    String verificationCode = redisTemplate.opsForValue().get(redisKey);

    if (verificationCode != null && verificationCode.equals(code)) {
      redisTemplate.delete(redisKey);
      return true;
    }
    return false;
  }

  @Override
  public boolean isVerificationCodeRateLimited(String email) {
    String redisKey = RedisKey.registerVerificationLimitCode(email);
    return redisTemplate.opsForValue().get(redisKey) != null;
  }
}
