package com.tcosfish.xianyu.task.email;

import com.tcosfish.xianyu.event.EmailSendEvent;

/**
 * @author tcosfish
 */
public interface EmailTaskConsumerInterface {
  void handleEmailSendEvent(EmailSendEvent event);
}
