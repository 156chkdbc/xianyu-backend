package com.tcosfish.xianyu.model.vo.category;

import lombok.Data;

/**
 * @author tcosfish
 */
@Data
public class CategoryFlatVO {
  private Long id;
  private String name;
  private Long parentId;
}
