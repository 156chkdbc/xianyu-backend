package com.tcosfish.xianyu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcosfish.xianyu.model.entity.OrderItem;
import com.tcosfish.xianyu.service.OrderItemService;
import com.tcosfish.xianyu.mapper.OrderItemMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【order_item(订单明细)】的数据库操作Service实现
* @createDate 2025-09-14 21:34:23
*/
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem>
    implements OrderItemService{

}




