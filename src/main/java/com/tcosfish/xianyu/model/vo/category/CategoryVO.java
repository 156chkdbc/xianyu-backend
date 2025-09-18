package com.tcosfish.xianyu.model.vo.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author tcosfish
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryVO {
  private Long id;
  private String name;
  private Long parentId;
  private List<CategoryVO> children;
}
