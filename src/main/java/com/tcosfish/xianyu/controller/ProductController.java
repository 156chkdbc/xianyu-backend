package com.tcosfish.xianyu.controller;

import com.tcosfish.xianyu.model.base.ApiResponse;
import com.tcosfish.xianyu.model.base.EmptyVO;
import com.tcosfish.xianyu.model.dto.product.*;
import com.tcosfish.xianyu.model.vo.product.*;
import com.tcosfish.xianyu.service.impl.ProductServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author tcosfish
 */
@Tag(name = "商品管理", description = "商品相关接口")
@RestController
@RequestMapping("/products")
public class ProductController {

  private final ProductServiceImpl productService;

  public ProductController(ProductServiceImpl productServiceImpl) {
    this.productService = productServiceImpl;
  }

  @GetMapping("")
  @Operation(summary = "获取商品列表", description = "默认商品瀑布流（分页、通用筛选）")
  public ApiResponse<List<ProductVO>> getProductList(
    @Valid @ModelAttribute ProductQueryParam productQueryParam
  ) {
    return productService.getProductList(productQueryParam);
  }

  // todo 热搜, 新旧区间还未实现
  @GetMapping("/search")
  @Operation(summary = "关键词搜索（高亮、热搜、ES）", description = "关键词 + 分类 + 价格区间 + 新旧程度 + 排序 + 分页")
  public ApiResponse<List<ProductVO>> searchProducts(
    @Valid @RequestBody ProductSearchParam productSearchParam
  ) {
    return productService.search(productSearchParam);
  }

  @GetMapping("/{productId}")
  @Operation(summary = "获取单个商品信息")
  public ApiResponse<ProductVO> getProductInfo(
    @Min(value = 1, message = "productId 必须为正整数") @PathVariable Long productId
  ) {
    return productService.getProductInfo(productId);
  }

  @PostMapping("")
  @Operation(summary = "发布商品")
  public ApiResponse<CreateProductVO> addProduct(
    @Valid @RequestBody CreateProductParam createProductParam) {
    return productService.addProduct(createProductParam);
  }

  @PutMapping("/{productId}")
  @Operation(summary = "全量修改商品（卖家本人）")
  public ApiResponse<EmptyVO> updateProduct(
    @Min(value = 1, message = "productId 必须为正整数") @PathVariable Long productId,
    @Valid @RequestBody UpdateProductParam updateProductParam
  ) {
    return productService.updateProduct(productId, updateProductParam);
  }

  @PatchMapping("/{productId}/{status}")
  @Operation(summary = "商品上架/下架", description = "商品状态修改, 1上架/2已售/3下架")
  public ApiResponse<EmptyVO> updateProductType(
    @Min(value = 1, message = "productId 必须是正整数") @PathVariable Long productId,
    @Min(value = 1, message = "status 必须在1~3之间") @Max(value = 3, message = "status 必须在1~3之间") @PathVariable Integer status
  ) {
    return productService.updateProductType(productId, status);
  }

  @DeleteMapping("/{productId}")
  @Operation(summary = "删除商品")
  public ApiResponse<EmptyVO> deleteProduct(
    @Min(value = 1, message = "productId 必须为正整数") @PathVariable Long productId
  ) {
    return productService.deleteProduct(productId);
  }

  @PostMapping("/my")
  @Operation(summary = "我的商品（卖家中心）", description = "获取登录用户的商品数据")
  public ApiResponse<List<ProductVO>> getMyProduct(
    @Valid @RequestBody ProductQueryParam productQueryParam
  ) {
    return productService.getMyProduct(productQueryParam);
  }

  /** =================== 商品图片相关 =======================  */

  @PostMapping("/{productId}/images")
  @Operation(summary = "上传图片(封面+详情图)")
  public ApiResponse<ImageVO> uploadProductImg(
    @Min(value = 1, message = "productId必须是正整数") @PathVariable Long productId,
    @RequestParam("file") MultipartFile file
  ) {
    return productService.uploadProductImg(productId, file);
  }

  @GetMapping("/{productId}/images")
  @Operation(summary = "获取商品图片列表")
  public ApiResponse<List<ImageVO>> getProductImages(
    @Min(value = 1, message = "productId必须是正整数") @PathVariable Long productId
  ) {
    return productService.getProductImages(productId);
  }

  @DeleteMapping("/{productId}/images/{imagesId}")
  @Operation(summary = "删除单张图片")
  public ApiResponse<EmptyVO> deleteProductImage(
    @Min(value = 1, message = "productId必须是正整数") @PathVariable Long productId,
    @Min(value = 1, message = "imageId必须是正整数") @PathVariable Long imagesId
  ) {
    return productService.deleteProductImage(productId, imagesId);
  }

  /** =================== 商品议价相关 =======================  */

  @PostMapping("/{productId}/bargain")
  @Operation(summary = "发起议价", description = "买家")
  public ApiResponse<CreateNegotiateVO> initiateBargain(
    @Min(value = 1, message = "productId必须是正整数") @PathVariable Long productId,
    @Valid @RequestBody CreateNegotiateParam createNegotiateParam
    ) {
    return productService.initiateBargain(productId, createNegotiateParam);
  }

  @GetMapping("/{productId}/bargain")
  @Operation(summary = "查看议价列表", description = "卖家查看自己商品的/买家查看以及已提出的")
  public ApiResponse<List<ProductNegotiateVO>> getBargainList(
    @Min(value = 1, message = "productId必须是正整数") @PathVariable Long productId,
    @Valid @ModelAttribute ProductNegotiateParam param
  ) {
    return productService.getBargainList(productId, param);
  }

  @PatchMapping("/{productId}/bargain/{bargainId}")
  @Operation(summary = "接受/拒绝议价", description = "卖家")
  public ApiResponse<NegotiateStatusVO> handleBargain(
    @Min(value = 1, message = "productId必须是正整数") @PathVariable Long productId,
    @Min(value = 1, message = "bargainId必须是正整数") @PathVariable Long bargainId,
    @Valid @RequestBody NegotiateStatusParam param
  ) {
    return productService.handleBargain(productId, bargainId, param);
  }

  /** 与屏蔽议价骚扰要求不符, 买家可以等待 24h失效之后再次提出 */
  @DeleteMapping("/{productId}/bargain")
  @Operation(summary = "删除指定的议价", description = "买家")
  public ApiResponse<EmptyVO> deleteBargain(
    @Min(value = 1, message = "productId必须是正整数") @PathVariable Long productId
  ) {
    System.out.println(productId);
    return null;
  }

  /** =================== 相关数据检索 =======================  */

  @GetMapping("/hots")
  @Operation(summary = "实时热度榜（Redis）")
  public ApiResponse<List<HotProductVO>> productPopularityHeatMap(
    @RequestParam(defaultValue = "20") int top
  ) {
    return productService.productPopularityHeatMap(top);
  }

  @GetMapping("/views/{productId}")
  @Operation(summary = "实时浏览量")
  public ApiResponse<ViewVO> realTimeViews(
    @Min(value = 1, message = "") @PathVariable Long productId
  ) {
    return productService.realTimeViews(productId);
  }

  /** =================== 卖家批量操作 =======================  */

  @PatchMapping("/batch/shelf")
  @Operation(summary = "批量上下架（卖家）")
  public ApiResponse<EmptyVO> batchShelf(
  ) {
    return productService.batchShelf();
  }

  @DeleteMapping("/batch")
  @Operation(summary = "批量删除（卖家）")
  public ApiResponse<EmptyVO> batchSaleable(
  ) {
    return productService.batchDelete();
  }
}
