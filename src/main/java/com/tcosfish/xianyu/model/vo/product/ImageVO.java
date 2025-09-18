package com.tcosfish.xianyu.model.vo.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author tcosfish
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ImageVO {
  private Long imageId;
  private String imageUrl;
  private boolean isMaster;
}
