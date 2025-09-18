package com.tcosfish.xianyu.converter;

import com.tcosfish.xianyu.model.entity.Item;
import com.tcosfish.xianyu.model.entity.Negotiation;
import com.tcosfish.xianyu.model.enums.ProductItemType;
import com.tcosfish.xianyu.model.vo.order.PreviewOrderVO;
import com.tcosfish.xianyu.model.vo.product.ProductNegotiateVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Objects;

/**
 * @author tcosfish
 */
@Mapper(componentModel = "spring")
public interface ProductConverter {

  @Mapping(source = "itemType", target = "itemType", qualifiedByName = "objectToItemType")
  PreviewOrderVO.ItemVO toProductVO(Item entity);

  @Mapping(source = "id", target = "negotiationId")
  @Mapping(source = "createtime", target = "createTime")
  @Mapping(target = "buyerNick", ignore = true)
  ProductNegotiateVO toNegotiate(Negotiation entity);

  @Named("objectToItemType")
  default ProductItemType map(Integer itemType) {
    if (itemType == null) {
      return null;
    }
    for (ProductItemType type : ProductItemType.values()) {
      if (Objects.equals(type.getValue(), itemType)) { // 忽略大小写匹配
        return type;
      }
    }
    throw new IllegalArgumentException("无效的商品类型字符串: " + itemType);
  }
}
