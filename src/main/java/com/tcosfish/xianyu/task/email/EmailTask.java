package com.tcosfish.xianyu.task.email;

import lombok.Data;

/**
 * @author tcosfish
 * @apiNote 邮件任务, 提供给定时任务使用
 */
@Data
public class EmailTask {
  private String email;
  private String verificationCode;
  private long timestamp;
}
