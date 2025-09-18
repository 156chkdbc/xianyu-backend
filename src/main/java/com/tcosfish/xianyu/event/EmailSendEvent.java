package com.tcosfish.xianyu.event;

import com.tcosfish.xianyu.task.email.EmailTask;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author tcosfish
 * @description 邮件发送类
 */
@Getter
public class EmailSendEvent extends ApplicationEvent {
  private final EmailTask emailTask;

  public EmailSendEvent(Object source, EmailTask emailTask) {
    super(source);
    this.emailTask = emailTask;
  }
}
