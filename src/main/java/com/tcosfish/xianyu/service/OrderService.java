package com.tcosfish.xianyu.service;

import com.tcosfish.xianyu.model.base.ApiResponse;
import com.tcosfish.xianyu.model.base.EmptyVO;
import com.tcosfish.xianyu.model.dto.order.CreateOrderParam;
import com.tcosfish.xianyu.model.dto.order.OrderPageParam;
import com.tcosfish.xianyu.model.dto.order.PreviewOrderParam;
import com.tcosfish.xianyu.model.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tcosfish.xianyu.model.vo.order.DeliveryVO;
import com.tcosfish.xianyu.model.vo.order.OrderVO;
import com.tcosfish.xianyu.model.vo.order.PreviewOrderVO;
import com.tcosfish.xianyu.model.vo.order.ReviewVO;

import java.util.List;

/**
* @author Administrator
* @description 针对表【order(订单)】的数据库操作Service
* @createDate 2025-09-14 21:33:40
*/
public interface OrderService extends IService<Order> {

  ApiResponse<PreviewOrderVO> orderPreview(PreviewOrderParam param);

  ApiResponse<OrderVO> createOrder(CreateOrderParam param);

  ApiResponse<List<OrderVO>> getOrderList(OrderPageParam param);

  ApiResponse<OrderVO> orderDetailById(Long orderId);

  ApiResponse<EmptyVO> cancelOrder(Long orderId);

  ApiResponse<EmptyVO> completionOrder(Long orderId);

  void autoComplete(Long id);

  ApiResponse<DeliveryVO> getDeliverOrder(Long orderId);

  ApiResponse<ReviewVO> getReviewOrder(Long orderId);

  /** 支付成功后驱动订单状态 */
  void paySuccess(String orderNo);
}
