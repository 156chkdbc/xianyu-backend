package com.tcosfish.xianyu.model.base;

import lombok.Data;

/**
 * @author tcosfish
 * @apiNote vo: view-object 视图响应类, 无数据返回发情况
 */
@Data
public class EmptyVO {
  private boolean empty = true;
}
