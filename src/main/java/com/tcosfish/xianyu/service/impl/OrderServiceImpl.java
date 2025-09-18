package com.tcosfish.xianyu.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcosfish.xianyu.annotation.NeedLogin;
import com.tcosfish.xianyu.annotation.TraceLog;
import com.tcosfish.xianyu.converter.OrderConverter;
import com.tcosfish.xianyu.converter.ProductConverter;
import com.tcosfish.xianyu.converter.ReviewConverter;
import com.tcosfish.xianyu.model.vo.order.DeliveryVO;
import com.tcosfish.xianyu.model.vo.order.ReviewVO;
import com.tcosfish.xianyu.service.*;
import com.tcosfish.xianyu.task.order.DelayCloseProducer;
import com.tcosfish.xianyu.utils.EventPublisherUtil;
import com.tcosfish.xianyu.event.OrderFinishedEvent;
import com.tcosfish.xianyu.exception.BizException;
import com.tcosfish.xianyu.model.base.ApiResponse;
import com.tcosfish.xianyu.model.base.EmptyVO;
import com.tcosfish.xianyu.model.base.Pagination;
import com.tcosfish.xianyu.model.dto.order.CreateOrderParam;
import com.tcosfish.xianyu.model.dto.order.OrderPageParam;
import com.tcosfish.xianyu.model.dto.order.PreviewOrderParam;
import com.tcosfish.xianyu.model.entity.*;
import com.tcosfish.xianyu.model.enums.*;
import com.tcosfish.xianyu.model.vo.order.OrderVO;
import com.tcosfish.xianyu.model.vo.order.PreviewOrderVO;
import com.tcosfish.xianyu.model.vo.product.ItemStatusEnum;
import com.tcosfish.xianyu.scope.RequestScopeData;
import com.tcosfish.xianyu.mapper.OrderMapper;
import com.tcosfish.xianyu.utils.ApiResponseUtil;
import com.tcosfish.xianyu.utils.RandomCodeUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 * @description 针对表【order(订单)】的数据库操作Service实现
 * @createDate 2025-09-14 21:33:40
 */
@Slf4j
@Service
@AllArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
  implements OrderService {

  private final ItemServiceImpl itemService;
  private final NegotiationService negotiationService;
  private final ProductConverter productConverter;
  private final RequestScopeData requestScopeData;
  private final OrderItemService orderItemService;
  private final OrderConverter orderConverter;
  private final DelayCloseProducer delayCloseProducer;
  private final ReviewService reviewService;
  private final ReviewConverter reviewConverter;
  private final StringRedisTemplate redisTemplate;

  @Override
  @NeedLogin
  @TraceLog(desc = "结算页预览")
  @Transactional(rollbackFor = Exception.class)
  public ApiResponse<PreviewOrderVO> orderPreview(PreviewOrderParam param) {
    Long userId = requestScopeData.getUserId();
    Long productId = param.getProductId();
    Long negotiateId = param.getNegotiateId();
    // 1. 商品表
    Item item = itemService.getById(productId);
    if (item == null) {
      throw new BizException("未找到指定商品");
    }
    // 仅商品卖家或参与议价的买家可查看预览
    boolean isSeller = Objects.equals(item.getSellerId(), userId);
    boolean isNegotiator = false;
    if (negotiateId != null && negotiateId > 0) {
      Negotiation negotiation = negotiationService.getById(negotiateId);
      isNegotiator = negotiation != null && Objects.equals(negotiation.getBuyerId(), userId);
    }
    if (!isSeller && !isNegotiator) {
      throw new BizException("无权限查看该商品结算预览");
    }
    if (Objects.equals(item.getStatus(), ItemStatusEnum.OFF_SHELF.getCode())) {
      throw new BizException("该商品已下架");
    }
    // 2. 议价表
    Negotiation negotiation = null;
    if (negotiateId != null && negotiateId > 0) {
      negotiation = negotiationService.lambdaQuery()
        .eq(Negotiation::getId, negotiateId)
        .eq(Negotiation::getItemId, productId)
        .eq(Negotiation::getStatus, NegotiationStatus.ACCEPTED)
        .one();
      if (negotiation == null) {
        throw new BizException("未找到指定的议价信息");
      }
    }
    // 3. 历史订单（可选）
    // 记住用户上一次怎么交货/取货，减少重复选择，提升体验。
    Order order = this.lambdaQuery()
      .and(o -> o.eq(Order::getNegotiationId, negotiateId)
        .eq(Order::getBuyerId, userId))
      .or(o -> o.eq(Order::getNegotiationId, negotiateId)
        .eq(Order::getSellerId, userId))
      .last("limit 1")
      .one();
    // 4. 组装 VO
    PreviewOrderVO vo = new PreviewOrderVO();
    vo.setItem(productConverter.toProductVO(item));
    vo.setNegotiate(productConverter.toNegotiate(negotiation));
    vo.setSetFinalPrice(negotiation == null ? item.getPrice() : negotiation.getPrice());
    vo.setMaxQuantity(item.getStock());
    vo.setDefaultDeliveryType(order == null ? DeliveryTypeEnum.SELF_PICK : order.getDeliveryType());
    List<DeliveryTypeEnum> deliveryOptions = JSON.parseArray(item.getDeliveryOptions().toString())
      .stream()
      .map(v -> DeliveryTypeEnum.of((int) v)).toList();
    vo.setDeliveryOptions(deliveryOptions);   // 或从商品扩展字段读取

    return ApiResponseUtil.success("获取该商品的结算页预览", vo);
  }

  @Override
  @NeedLogin
  @TraceLog(desc = "创建订单")
  @Transactional(rollbackFor = Exception.class)
  public ApiResponse<OrderVO> createOrder(CreateOrderParam param) {
    Long userId = requestScopeData.getUserId();
    Long negotiationId = param.getNegotiationId();
    Long itemId = param.getItemId();
    Integer quantity = param.getQuantity();
    String key = RedisKey.createOrder(userId, itemId, negotiationId);
    // 幂等性控制, 防止重复提交
    Long count = redisTemplate.opsForValue().increment(key, 1);
    redisTemplate.expire(key, 5, TimeUnit.MINUTES);
    if (count == null) {
      // 可根据业务需求处理：重试、抛出异常或记录日志
      throw new BizException("系统繁忙，请稍后再试");
    }
    if (count > 1) {
      throw new BizException("请勿重复提交");
    }
    // 基础校验
    if (quantity == null || quantity < 1) {
      throw new BizException("购买数量必须 ≥ 1");
    }

    if (param.getDeliveryType() == null) {
      throw new BizException("请选择交付方式");
    }
    // 加载数据
    Item item = itemService.lambdaQuery().eq(Item::getId, itemId).one();
    Negotiation negotiation = negotiationService.lambdaQuery()
      .eq(Negotiation::getId, negotiationId)
      .eq(Negotiation::getItemId, itemId)
      .eq(Negotiation::getStatus, NegotiationStatus.ACCEPTED)
      .one();
    // 业务规则校验
    if (item == null) {
      throw new BizException("商品不存在");
    }
    if (!item.getStatus().equals(ItemStatusEnum.ON_SALE.getCode())) {
      throw new BizException("商品已下架或已售出");
    }
    if (quantity > item.getStock()) {
      throw new BizException("库存不足，最大可购" + item.getStock());
    }
    if (item.getSellerId().equals(userId)) {
      throw new BizException("不能购买自己的商品");
    }
    // 交付方式合法性
    List<Integer> options = JSON.parseArray(item.getDeliveryOptions().toString(), Integer.class);
    if (!options.contains(param.getDeliveryType().getCode())) {
      throw new BizException("当前商品不支持所选交付方式");
    }
    // 库存
    if (quantity > item.getStock()) {
      throw new BizException("库存不足");
    }
    // 计算最终价格
     BigDecimal finalPrice = negotiation == null ? item.getPrice() : negotiation.getPrice();
    // 乐观锁扣除库存
    if (!itemService.lambdaUpdate()
      .setSql("stock = stock - " + quantity)
      .eq(Item::getId, itemId)
      .ge(Item::getStock, quantity) // 关键修改：库存≥购买数量
      .update()) {
      throw new BizException("库存不足或已被抢, 请稍后再试");
    }
    // 生成订单
    Order order = Order.builder()
      .orderNo(RandomCodeUtil.generateNumberCode(10)) // 雪花算法生成订单号
      .buyerId(userId).sellerId(item.getSellerId())
      .orderType(OrderTypeEnum.of(item.getItemType())) // 商品/服务
      .totalAmount(item.getPrice().multiply(BigDecimal.valueOf(quantity)))
      .realAmount(finalPrice.multiply(BigDecimal.valueOf(quantity)))
      .status(OrderStatusEnum.WAIT_PAY)
      .negotiationId(negotiationId)
      .deliveryType(param.getDeliveryType()).deliveryRemark(param.getDeliveryRemark())
      .expireTime(Date.from(Instant.now().plus(24, ChronoUnit.HOURS))) // 24小时后关闭
      .build();
    // 金额一致性校验
    BigDecimal calcTotal = finalPrice.multiply(BigDecimal.valueOf(quantity));
    if (!calcTotal.equals(order.getRealAmount())) {
      throw new BizException("订单金额计算异常，请重试");
    }
    if (!save(order)) {
      throw new BizException("添加订单失败");
    }
    // 生成订单明细
    OrderItem orderItem = OrderItem.builder()
      .orderId(order.getId())
      .itemType(OrderItemTypeEnum.of(item.getItemType())).itemId(item.getId()).itemTitle(item.getTitle())
      .unitPrice(finalPrice).quantity(quantity)
      .amount(finalPrice.multiply(BigDecimal.valueOf(quantity)))
      .build();
    if (!orderItemService.save(orderItem)) {
      throw new BizException("添加订单明细失败");
    }
    Long finalOrderId = order.getId();
    // 注册事务提交后事件, 事务提交后, 准备发送24h后订单过期关闭的消息
    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
      @Override
      public void afterCommit() {
        delayCloseProducer.send(finalOrderId, 24, TimeUnit.HOURS);
      }
    });

    OrderVO orderVO = orderConverter.toOrderVO(order);
    return ApiResponseUtil.success("生成订单成功", orderVO);
  }

  @Override
  @NeedLogin
  @TraceLog(desc = "获取订单列表")
  public ApiResponse<List<OrderVO>> getOrderList(OrderPageParam param) {
    Long userId = requestScopeData.getUserId();
    // @豆包：1. 分页参数合法性校验（避免无效分页请求）
    int pageNum = Math.max(param.getPage(), 1); // 最小页码1
    int pageSize = Math.min(param.getPageSize(), 50); // 最大页大小50，避免数据量过大
    // 确定排序字段和顺序
    String column = Optional.ofNullable(param.getOrderByColumn())
      .map(OrderColumnEnum::getColumn)
      .orElse("createtime");
    boolean isAsc = param.getIsAsc() == null || param.getIsAsc();

    Page<Order> orderPage = new Page<>(pageNum, pageSize);
    if(isAsc) {
      orderPage.addOrder(com.baomidou.mybatisplus.core.metadata.OrderItem.asc(column));
    } else {
      orderPage.addOrder(com.baomidou.mybatisplus.core.metadata.OrderItem.desc(column));
    }
    Page<Order> page = lambdaQuery()
      .eq(Objects.equals(param.getRole(), "buyer"), Order::getBuyerId, userId)
      .eq(Objects.equals(param.getRole(), "seller"), Order::getSellerId, userId)
      .eq(param.getOrderType() != null, Order::getOrderType, param.getOrderType())
      .eq(param.getStatus() != null, Order::getStatus, param.getStatus())
      .between(param.getDateFrom() != null && param.getDateTo() != null, Order::getCreatetime,
        param.getDateFrom(), param.getDateTo()
      ).page(orderPage);
    List<OrderVO> orderVOList = page.getRecords().stream().map(orderConverter::toOrderVO).toList();
    Pagination pagination = new Pagination(pageNum, pageSize, page.getTotal());
    return ApiResponseUtil.success("获取订单列表成功", orderVOList, pagination);
  }

  @Override
  @NeedLogin
  @TraceLog(desc = "获取订单详情")
  public ApiResponse<OrderVO> orderDetailById(Long orderId) {
    Long userId = requestScopeData.getUserId();
    Order order = lambdaQuery().eq(Order::getId, orderId)
      .oneOpt().orElseThrow(() -> new BizException("订单不存在"));

    if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
      throw new BizException("用户没有查看该商品的查看权限");
    }
    return ApiResponseUtil.success("获取订单详情成功", orderConverter.toOrderVO(order));
  }

  @Override
  @NeedLogin
  @TraceLog(desc = "取消订单")
  @Transactional(rollbackFor = Exception.class)
  public ApiResponse<EmptyVO> cancelOrder(Long orderId) {
    Long userId = requestScopeData.getUserId();
    // 通用：存在+权限
    Order order = getAndVerifyOrder(orderId, userId);
    // 状态机：只能取消“待付款”
    if (!OrderStatusEnum.WAIT_PAY.equals(order.getStatus())) {
      throw new BizException("当前状态不允许取消");
    }
    // 回滚库存（乐观锁）
    OrderItem orderItem = orderItemService.lambdaQuery()
      .eq(OrderItem::getOrderId, orderId)
      .one();
    if(orderItem == null) {
      throw new BizException("未找到对应的商品");
    }
    itemService.lambdaUpdate()
      .setSql("stock = stock + " + getTotalQuantity(orderId))
      .eq(Item::getId, orderItem.getItemId())
      .update();
    // 改订单
    order.setStatus(OrderStatusEnum.CANCELLED);
    order.setCloseTime(new Date());
    order.setCloseReason(CloseReasonEnum.USER_CANCEL);
    updateById(order);

    // 事务提交后, 删除创建订单时的幂等键(允许用户重新下单)
    Long itemId = orderItem.getId();
    Long negotiationId = order.getNegotiationId();
    String key = RedisKey.createOrder(userId, itemId, negotiationId);
    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
      @Override
      public void afterCommit() {
        redisTemplate.delete(key); // 删除创建订单的键, 禁止污染
      }
    });

    return ApiResponseUtil.success("订单已取消");
  }

  @Override
  @NeedLogin
  @TraceLog(desc = "确认完成")
  @Transactional(rollbackFor = Exception.class)
  public ApiResponse<EmptyVO> completionOrder(Long orderId) {
    Long userId = requestScopeData.getUserId();
    Order order = getAndVerifyOrder(orderId, userId);

    // 仅“已付款”可点完成
    if (!OrderStatusEnum.PAID.equals(order.getStatus())) {
      throw new BizException("当前状态不允许确认完成");
    }

    // 双向均可，幂等
    order.setStatus(OrderStatusEnum.FINISHED);
    order.setFinishTime(new Date());
    if (!updateById(order)) {
      throw new BizException("订单状态更改失败");
    }

    // 异步：触发双方评价提醒、积分、分账等（可选）?
    EventPublisherUtil.publishEvent(new OrderFinishedEvent(this, orderId));
    return ApiResponseUtil.success("订单已完成");
  }

  @Override
  @TraceLog(desc = "支付订单24小时后自动确认完成")
  @Transactional(rollbackFor = Exception.class)
  public void autoComplete(Long id) {
    this.completionOrder(id);
  }

  @Override
  @NeedLogin
  @TraceLog(desc = "获取交付信息")
  public ApiResponse<DeliveryVO> getDeliverOrder(Long orderId) {
    // 仅买家/卖家可见
    Order order = getAndVerifyOrder(orderId, requestScopeData.getUserId());


    DeliveryVO vo = new DeliveryVO();
    vo.setDeliveryType(order.getDeliveryType());
    vo.setDeliveryRemark(order.getDeliveryRemark());

    // 扩展字段：取件码、二维码（示例写死，后续可从 delivery_log 表读）
    if (DeliveryTypeEnum.DORM_DELIVER.equals(order.getDeliveryType())) {
      vo.setPickupCode("7392");   // 也可存 delivery_log 表
      vo.setQrUrl("https://cdn.xxx/qr/" + order.getId() + ".png");
    }
    return ApiResponseUtil.success("获取交付信息成功", vo);
  }

  @Override
  @NeedLogin
  @TraceLog(desc = "获取订单评价")
  public ApiResponse<ReviewVO> getReviewOrder(Long orderId) {
    // 数据权限：必须是买家或卖家
    getAndVerifyOrder(orderId, requestScopeData.getUserId());

    Review review = reviewService.lambdaQuery()
      .eq(Review::getOrderId, orderId)
      .one();

    return ApiResponseUtil.success("获取订单评价成功", reviewConverter.toReviewVO(review));
  }

  @Override
  @NeedLogin
  @TraceLog(desc = "支付成功之后驱动订单状态")
  @Transactional(rollbackFor = Exception.class)
  public void paySuccess(String orderNo) {
    Order order = lambdaQuery()
      .eq(Order::getOrderNo, orderNo)
      .one();
    if (order == null) {
      return;
    }

    boolean ok = lambdaUpdate()
      .eq(Order::getId, order.getId())
      .eq(Order::getStatus, OrderStatusEnum.WAIT_PAY.getCode())
      .set(Order::getStatus, OrderStatusEnum.PAID.getCode())
      .set(Order::getPayTime, LocalDateTime.now())
      .set(Order::getUpdatetime, LocalDateTime.now())
      .update();
    if (!ok) {
      log.warn("订单状态更新失败，可能并发: {}", orderNo);
      throw new BizException("订单状态更新失败");
    }
    log.info("订单已支付: {}", orderNo);
  }

  /**
   *
   * @param orderId 订单id
   * @param userId 用户id
   * @return 订单数据
   */
  private Order getAndVerifyOrder(Long orderId, Long userId) {
    Order order = getById(orderId);
    if (order == null) {
      throw new BizException("订单不存在");
    }
    // 数据权限：必须是买家或卖家
    if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
      throw new BizException("用户无权限");
    }
    return order;
  }

  /**
   * 获取订单数量
   *
   * @param orderId 订单编号
   * @return 商品数量
   */
  private Integer getTotalQuantity(Long orderId) {
    return orderItemService.lambdaQuery()
      .eq(OrderItem::getOrderId, orderId)
      .list()
      .stream()
      .mapToInt(OrderItem::getQuantity)
      .sum();
  }
}
