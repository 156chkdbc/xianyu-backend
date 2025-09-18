package com.tcosfish.xianyu.model.base;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author tcosfish
 * @apiNote
 */
@Data
@AllArgsConstructor
public class Pagination {
  private Integer page;  // 当前页码
  private Integer pageSize;  // 每页显示的记录数
  private Long total;  // 总记录数
}
