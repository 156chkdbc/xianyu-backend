package com.tcosfish.xianyu.converter;

import com.tcosfish.xianyu.model.entity.Review;
import com.tcosfish.xianyu.model.enums.OrderItemTypeEnum;
import com.tcosfish.xianyu.model.vo.order.ReviewVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Objects;

/**
 * @author tcosfish
 */
@Mapper(componentModel = "spring")
public interface ReviewConverter {

  @Mapping(source = "itemType", target = "itemType", qualifiedByName = "reviewIntToReviewType")
  ReviewVO toReviewVO(Review entity);

  @Named("reviewIntToReviewType")
  default OrderItemTypeEnum map(Integer itemType) {
    if (itemType == null) {
      return null;
    }
    for (OrderItemTypeEnum type : OrderItemTypeEnum.values()) {
      if (Objects.equals(type.getValue(), itemType)) { // 忽略大小写匹配
        return type;
      }
    }
    throw new IllegalArgumentException("无效的商品类型字符串: " + itemType);
  }
}
