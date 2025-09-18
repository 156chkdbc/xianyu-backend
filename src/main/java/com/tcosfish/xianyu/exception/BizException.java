package com.tcosfish.xianyu.exception;

/**
 * @author tcosfish
 * @description 业务异常，触发事务回滚，全局异常处理器会把它转成友好响应
 */
public class BizException extends RuntimeException{
  public BizException(String message) {
    super(message);
  }
}
