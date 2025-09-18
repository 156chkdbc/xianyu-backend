package com.tcosfish.xianyu.converter;

import com.tcosfish.xianyu.model.entity.Order;
import com.tcosfish.xianyu.model.vo.order.OrderVO;
import org.mapstruct.Mapper;

/**
 * @author tcosfish
 */
@Mapper(componentModel = "spring")
public interface OrderConverter {
  OrderVO toOrderVO(Order entity);
}
