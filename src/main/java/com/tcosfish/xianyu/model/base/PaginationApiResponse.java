package com.tcosfish.xianyu.model.base;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author tcosfish
 * @apiNote PaginationApiResponse类用于处理分页的API响应
 */
@Data
@EqualsAndHashCode(callSuper = true) // 调用父类
public class PaginationApiResponse<T> extends ApiResponse<T> {
  private final Pagination pagination;

  public PaginationApiResponse(int code, String msg, T data, Pagination pagination) {
    super(code, msg, data); // 调用父类ApiResponse的构造方法
    this.pagination = pagination; // 初始化分页信息
  }
}
