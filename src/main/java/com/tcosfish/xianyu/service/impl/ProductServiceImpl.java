package com.tcosfish.xianyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tcosfish.xianyu.annotation.NeedLogin;
import com.tcosfish.xianyu.annotation.TraceLog;
import com.tcosfish.xianyu.cache.HotProductCache;
import com.tcosfish.xianyu.converter.ImageConverter;
import com.tcosfish.xianyu.converter.NegotiateConverter;
import com.tcosfish.xianyu.exception.BizException;
import com.tcosfish.xianyu.model.base.ApiResponse;
import com.tcosfish.xianyu.model.base.EmptyVO;
import com.tcosfish.xianyu.model.base.Pagination;
import com.tcosfish.xianyu.model.dto.product.*;
import com.tcosfish.xianyu.model.entity.*;
import com.tcosfish.xianyu.model.enums.NegotiationStatus;
import com.tcosfish.xianyu.model.vo.product.*;
import com.tcosfish.xianyu.scope.RequestScopeData;
import com.tcosfish.xianyu.service.ProductService;
import com.tcosfish.xianyu.utils.ApiResponseUtil;
import com.tcosfish.xianyu.utils.DesensitizeUtil;
import com.tcosfish.xianyu.utils.ProductConvertUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author tcosfish
 * @description 商品服务层
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
  private final ItemServiceImpl itemService;
  private final RequestScopeData requestScopeData;
  private final CategoryServiceImpl categoryService;
  private final FileServiceImpl fileService;
  private final ItemImagesServiceImpl itemImagesService;
  private final NegotiateConverter negotiateConverter;
  private final NegotiationServiceImpl negotiationService;
  private final UserServiceImpl userService;
  private final HotProductCache cache;
  private final ImageConverter imageConverter;

  @Override
  public ApiResponse<List<ProductVO>> getProductList(ProductQueryParam queryParam) {
    // ✅ 只依赖 itemService，不再出现 itemMapper
    IPage<ProductDto> page = itemService.pageProductDto(queryParam);

    Pagination pagination = new Pagination(queryParam.getPage(), queryParam.getPageSize(), page.getTotal());

    List<ProductVO> list = page.getRecords().stream().map(ProductConvertUtil::toCard).toList();
    return ApiResponseUtil.success("获取商品列表", list, pagination);
  }

  @Override
  @NeedLogin
  public ApiResponse<CreateProductVO> addProduct(CreateProductParam createProductParam) {
    Long userId = requestScopeData.getUserId();

    // 检验分类ID
    Category foundCategory = categoryService.lambdaQuery().eq(Category::getId, createProductParam.getCategoryId()).one();
    if (foundCategory == null) {
      return ApiResponseUtil.error("没有对应的商品分类");
    }

    Item item = new Item();
    item.setSellerId(userId);
    BeanUtils.copyProperties(createProductParam, item);
    if (!itemService.save(item)) {
      return ApiResponseUtil.error("商品新增失败");
    }

    CreateProductVO createProductVO = new CreateProductVO();
    createProductVO.setId(item.getId());
    return ApiResponseUtil.success("商品新增成功", createProductVO);
  }

  @Override
  @NeedLogin
  public ApiResponse<EmptyVO> updateProduct(Long productId, UpdateProductParam updateProductParam) {
    Item item = itemService.lambdaQuery().eq(Item::getId, productId).one();

    if (item == null) {
      return ApiResponseUtil.error("商品ID格式错误");
    }

    BeanUtils.copyProperties(updateProductParam, item);
    if (!itemService.lambdaUpdate().eq(Item::getId, productId).update(item)) {
      return ApiResponseUtil.error("商品修改失败");
    }
    return ApiResponseUtil.success("商品修改成功");
  }

  @Override
  @NeedLogin
  public ApiResponse<EmptyVO> deleteProduct(Long productId) {
    Long userId = requestScopeData.getUserId();

    Item item = itemService.lambdaQuery().eq(Item::getSellerId, userId).eq(Item::getId, productId).one();

    if (item == null) {
      return ApiResponseUtil.error("不存在 productId 对应的商品");
    }

    if (!itemService.removeById(productId)) {
      return ApiResponseUtil.error("删除失败");
    }

    return ApiResponseUtil.success("删除成功");
  }

  @Override
  @NeedLogin
  public ApiResponse<ProductVO> getProductInfo(Long productId) {
    ProductDto productDto = itemService.selectProduct(productId);

    if (productDto == null) {
      return ApiResponseUtil.error("没有找到指定ID的商品");
    }

    ProductVO productVO = ProductConvertUtil.toCard(productDto);
    BeanUtils.copyProperties(productDto, productVO);

    return ApiResponseUtil.success("成功获取商品详细信息", productVO);
  }

  @Override
  @NeedLogin
  public ApiResponse<List<ProductVO>> getMyProduct(ProductQueryParam productQueryParam) {
    Long userId = requestScopeData.getUserId();
    productQueryParam.setSellerId(userId); // 进行 userId覆盖
    return getProductList(productQueryParam);
  }

  @Override
  @NeedLogin
  @TraceLog(desc = "修改商品状态")
  public ApiResponse<EmptyVO> updateProductType(Long productId, Integer status) {
    Long userId = requestScopeData.getUserId();

    Item orig = itemService.lambdaQuery()
      .select(Item::getId, Item::getItemType)   // 只查两列，覆盖索引
      .eq(Item::getId, productId)
      .eq(Item::getSellerId, userId)
      .one();
    if (orig == null) {
      return ApiResponseUtil.error("指定ID的商品并不存在");
    }
    if (Objects.equals(orig.getItemType(), status)) { // 4. 幂等
      return ApiResponseUtil.success("商品状态无变化");
    }

    boolean isOk = itemService.lambdaUpdate()
      .eq(Item::getSellerId, userId)
      .eq(Item::getId, productId)
      .set(Item::getStatus, status)
      .update();

    if (!isOk) {
      return ApiResponseUtil.error("更新失败, 请稍后重试");
    }

    return ApiResponseUtil.success("商品" + ItemStatusEnum.of(status).getDesc() + "成功");
  }

  @Override
  @NeedLogin
  @TraceLog(desc = "搜索商品")
  public ApiResponse<List<ProductVO>> search(ProductSearchParam searchParam) {
    // ✅ 只依赖 itemService，不再出现 itemMapper
    IPage<ProductDto> page = itemService.pageProductDto(searchParam);

    Pagination pagination = new Pagination(searchParam.getPage(), searchParam.getPageSize(), page.getTotal());

    List<ProductVO> list = page.getRecords().stream().map(ProductConvertUtil::toCard).toList();
    return ApiResponseUtil.success("商品搜索成功", list, pagination);
  }

  @Override
  @NeedLogin
  @TraceLog(desc = "上传图片")
  @Transactional(rollbackFor = Exception.class)
  public ApiResponse<ImageVO> uploadProductImg(Long productId, MultipartFile file) {
    /* 1. 并发安全：数据库自增序号，不再 count() */
    Integer maxIdx = itemImagesService.lambdaQuery()
      .eq(ItemImages::getItemId, productId)
      .orderByDesc(ItemImages::getIdx)
      .last("LIMIT 1")
      .oneOpt()                       // MP3.5+ 防止 NPE
      .map(ItemImages::getIdx)
      .orElse(-1);

    /* 2. 封面判断 */
    boolean existsMaster = itemImagesService.lambdaQuery()
      .eq(ItemImages::getItemId, productId)
      .eq(ItemImages::getIsMaster, 1)
      .exists();

    String url = fileService.uploadImage(file);
    ItemImages itemImages = new ItemImages();
    itemImages.setItemId(productId);
    itemImages.setUrl(url);
    itemImages.setIdx(maxIdx + 1);                         // 设置图片顺序
    itemImages.setIsMaster(existsMaster ? 0 : 1);          // 将上传的首张图片作为首页
    if (!itemImagesService.save(itemImages)) {
      throw new BizException("商品图片添加失败");
    }

    ImageVO imageVO = imageConverter.toImageVO(itemImages);
    return ApiResponseUtil.success("商品图片上传成功", imageVO);
  }

  @Override
  @NeedLogin
  @TraceLog(desc = "用户获取商品图片列表")
  public ApiResponse<List<ImageVO>> getProductImages(Long productId) {
    List<ItemImages> list = itemImagesService.lambdaQuery()
      .eq(ItemImages::getItemId, productId)
      .orderByAsc(ItemImages::getIdx)
      .list();
    List<ImageVO> imageVOList = list.stream()
      .map(i -> new ImageVO(i.getId(), i.getUrl(), i.getIsMaster() == 1))
      .collect(Collectors.toList());

    return ApiResponseUtil.success("商品图片列表如下", imageVOList);
  }

  @Override
  @NeedLogin
  @TraceLog(desc = "用户删除单张图片")
  @Transactional(rollbackFor = Exception.class)
  public ApiResponse<EmptyVO> deleteProductImage(Long productId, Long imagesId) {
    ItemImages foundImage = itemImagesService.lambdaQuery()
      .eq(ItemImages::getId, imagesId)
      .eq(ItemImages::getItemId, productId)
      .oneOpt()
      .orElseThrow(() -> new BizException("图片不存在"));
    // 删除图片
    if (!itemImagesService.lambdaUpdate().eq(ItemImages::getItemId, productId).eq(ItemImages::getId, imagesId).remove()) {
      throw new BizException("商品图片删除失败");
    }
    // 原先图片删除之后的连锁反应, isMaster, idx
    Integer changeIdx = foundImage.getIdx();
    boolean isMaster = foundImage.getIsMaster() == 1;

    // 相继图片的处理
    if (!itemImagesService.lambdaUpdate()
      .eq(ItemImages::getItemId, productId)
      .gt(ItemImages::getIdx, changeIdx)
      .setSql("idx = idx - 1")
      .update()) {
      throw new BizException("图片索引更新失败");
    }
    if (isMaster) {
      if (!itemImagesService.lambdaUpdate()
        .eq(ItemImages::getItemId, productId)
        .eq(ItemImages::getIdx, 0)
        .set(ItemImages::getIsMaster, 1)
        .update()) {
        throw new BizException("首页更新失败");
      }
    }

    return ApiResponseUtil.success("商品图片删除成功");
  }

  @Override
  @NeedLogin
  @TraceLog(desc = "买家发起议价")
  @Transactional(rollbackFor = Exception.class)
  public ApiResponse<CreateNegotiateVO> initiateBargain(Long productId, CreateNegotiateParam createNegotiateParam) {
    /* 0. 落库校验 */
    Long userId = requestScopeData.getUserId();
    Item item = itemService.lambdaQuery()
      .eq(Item::getId, productId)
      .select(Item::getId, Item::getSellerId, Item::getPrice, Item::getNegotiable)
      .oneOpt().orElseThrow(() -> new BizException("商品不存在"));
    if (item.getNegotiable() == 0) {
      throw new BizException("该商品不支持议价");
    }

    /* 1. 幂等防重, 每个人针对一种商品只能发送一条等待回复的议价 */
    boolean exists = negotiationService.lambdaQuery()
      .eq(Negotiation::getItemId, item.getId())
      .eq(Negotiation::getBuyerId, userId)
      .eq(Negotiation::getStatus, NegotiationStatus.PENDING)
      .exists();
    if (exists) {
      throw new BizException("您已有一条待回应的议价，请勿重复提交");
    }

    /* 2. 价格校验 */
    BigDecimal price = createNegotiateParam.getPrice();
    BigDecimal maxAllow = item.getPrice().multiply(BigDecimal.valueOf(0.95));
    if (price.compareTo(BigDecimal.valueOf(0.01)) < 0 ||
      price.compareTo(maxAllow) > 0) {
      throw new BizException("出价必须在 0.01 元至商品单价 95% 之间");
    }

    /* 3. 轮次校验 */
    Long count = negotiationService.lambdaQuery()
      .eq(Negotiation::getItemId, item.getId())
      .eq(Negotiation::getBuyerId, userId)
      .orderByAsc(Negotiation::getId)
      .select(Negotiation::getRound)
      .count();
    if(count >= 3) {
      throw new BizException("您在该商品处已超过议价次数");
    }

    /* 4. 落库存储 */
    Negotiation negotiation = negotiateConverter.toEntity(createNegotiateParam);
    negotiation.setItemId(productId);
    negotiation.setBuyerId(userId);
    negotiation.setSellerId(item.getSellerId());
    negotiation.setStatus(NegotiationStatus.PENDING);
    negotiation.setRound((int) (count + 1));
    negotiation.setExpireAt(LocalDateTime.now().plusHours(24));

    // 甚至可以考虑冻结买家对应的余额 wallService.freeze(userId, price)
    if (!negotiationService.save(negotiation)) {
      throw new BizException("发起议价失败");
    }
    CreateNegotiateVO createNegotiateVO = negotiateConverter.toVo(negotiation);
    return ApiResponseUtil.success("发起议价成功", createNegotiateVO);
  }

  @Override
  @NeedLogin
  @TraceLog(desc = "查看指定商品的议价列表")
  public ApiResponse<List<ProductNegotiateVO>> getBargainList(Long productId, ProductNegotiateParam param) {
    if (param.getPageSize() > 50) {
      param.setPageSize(50);
    }

    // 构造条件 + 排序
    Long userId = requestScopeData.getUserId();
    LambdaQueryWrapper<Negotiation> wrapper = new LambdaQueryWrapper<Negotiation>()
      .eq(Negotiation::getItemId, productId)
      .eq(Negotiation::getSellerId, userId)
      .eq(param.getStatus() != null, Negotiation::getStatus, param.getStatus())
      .orderByDesc(Negotiation::getCreatetime);   // 最新在前

    Page<Negotiation> page = new Page<>(param.getPage(), param.getPageSize());
    Page<Negotiation> negotiationPage = negotiationService.page(page, wrapper);

    // 1. 一次性查用户
    Set<Long> buyerIds = negotiationPage.getRecords()
      .stream().map(Negotiation::getBuyerId).collect(Collectors.toSet());
    Map<Long, User> userMap = buyerIds.isEmpty() ? Map.of()
      : userService.lambdaQuery().in(User::getId, buyerIds)
      .select(User::getId, User::getUsername, User::getPhone)
      .list().stream().collect(Collectors.toMap(User::getId, u -> u));
    // 2. 拼接转化为vo类型
    List<ProductNegotiateVO> list = negotiationPage.getRecords()
      .stream().map(n -> {
        ProductNegotiateVO vo = negotiateConverter.toProductNegotiateVO(n);
        User u = userMap.get(n.getBuyerId());
        vo.setBuyerNick(u == null ? "同学 ***" : DesensitizeUtil.desensitize(u.getUsername(), u.getPhone()));
        return vo;
      })
      .toList();
    // 3. 可以考虑加缓存
    Pagination pagination = new Pagination(param.getPage(), param.getPageSize(), negotiationPage.getTotal());
    return ApiResponseUtil.success("获取议价列表成功", list, pagination);
  }

  @Override
  @NeedLogin
  @TraceLog(desc = "处理议价")
  @Transactional(rollbackFor = Exception.class)
  public ApiResponse<NegotiateStatusVO> handleBargain(Long productId, Long bargainId, NegotiateStatusParam param) {
    // 1. 权限验证 + 读取记录并加行锁, 防止并发
    Long userId = requestScopeData.getUserId();
    Negotiation n = negotiationService.lambdaQuery()
      .eq(Negotiation::getSellerId, userId)
      .eq(Negotiation::getItemId, productId)
      .eq(Negotiation::getId, bargainId)
      .eq(Negotiation::getStatus, NegotiationStatus.PENDING)
      .oneOpt()
      .orElseThrow(() -> new BizException("议价不存在或是已处理"));
    // 管理员身份验证 isAdmin

    NegotiateStatusVO nVO = new NegotiateStatusVO();
    nVO.setNegotiationId(bargainId);
    // 选中分支, 传参: 1同意, 2拒绝, 跟数据库状态设置不同
    if (param.getStatus() == 2) {
      // 2.1 拒绝议价
      n.setStatus(NegotiationStatus.REJECTED);
      nVO.setStatus(NegotiationStatus.REJECTED);
      negotiationService.updateById(n);
    } else {
      // 2.2 同意议价
      // 生成商品订单, 目前先随机生成
      nVO.setOrderNo(UUID.randomUUID().toString());
      // 保存状态
      n.setStatus(NegotiationStatus.ACCEPTED);
      nVO.setStatus(NegotiationStatus.ACCEPTED);
      negotiationService.updateById(n);
      // 3.其他议价设置失效
      negotiationService.lambdaUpdate()
        .eq(Negotiation::getItemId, n.getItemId())
        .eq(Negotiation::getStatus, NegotiationStatus.PENDING)
        .ne(Negotiation::getId, bargainId)
        .set(Negotiation::getStatus, NegotiationStatus.EXPIRED)
        .update();
    }

    return ApiResponseUtil.success("议价状态已更新", nVO);
  }

  @Override
  @TraceLog(desc = "获取热度榜")
  public ApiResponse<List<HotProductVO>> productPopularityHeatMap(int top) {
    List<Long> topIds = cache.top(top);
    if (topIds.isEmpty()) {
      return ApiResponseUtil.success("获取热度榜成功", List.of());
    }
    // 根据Ids, 批量获取具体数据
    List<Item> list = itemService.lambdaQuery()
      .in(Item::getId, topIds)
      .list();
    // 保持榜单顺序
    Map<Long, Item> map = list.stream().collect(Collectors.toMap(Item::getId, p -> p));
    List<HotProductVO> hotProductVOList = topIds.stream()
      .map(map::get)
      .filter(Objects::nonNull)
      .map(p -> {
        Long views = cache.getViewCount(p.getId());
        return new HotProductVO(p.getId(), p.getTitle(), p.getPrice(), views.intValue());
      })
      .toList();

    return ApiResponseUtil.success("获取热度榜成功", hotProductVOList);
  }

  @Override
  @TraceLog(desc = "单个商品实时浏览量")
  public ApiResponse<ViewVO> realTimeViews(Long productId) {
    if (!itemService.lambdaQuery().eq(Item::getId, productId).exists()) {
      return ApiResponseUtil.error("ID对应的商品不存在");
    }

    Long view = cache.getViewCount(productId);
    ViewVO viewVO = new ViewVO(productId, view);
    return ApiResponseUtil.success("获取单商品实施浏览量成功", viewVO);
  }

  @Override
  @NeedLogin
  @TraceLog(desc = "批量上下架")
  public ApiResponse<EmptyVO> batchShelf() {
    // todo
    return null;
  }

  @Override
  @NeedLogin
  @TraceLog(desc = "批量删除")
  public ApiResponse<EmptyVO> batchDelete() {
    // todo
    return null;
  }
}
