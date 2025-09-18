package com.tcosfish.xianyu.converter;

import com.tcosfish.xianyu.model.entity.ItemImages;
import com.tcosfish.xianyu.model.vo.product.ImageVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author tcosfish
 */
@Mapper(componentModel = "spring")
public interface ImageConverter {

  @Mapping(source = "id", target = "imageId")
  @Mapping(source = "url", target = "imageUrl")
  ImageVO toImageVO(ItemImages entity);
}
