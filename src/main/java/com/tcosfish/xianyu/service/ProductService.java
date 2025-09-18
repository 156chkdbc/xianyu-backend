package com.tcosfish.xianyu.service;

import com.tcosfish.xianyu.model.base.ApiResponse;
import com.tcosfish.xianyu.model.base.EmptyVO;
import com.tcosfish.xianyu.model.dto.product.*;
import com.tcosfish.xianyu.model.vo.product.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author tcosfish
 */
public interface ProductService {
  ApiResponse<List<ProductVO>> getProductList(ProductQueryParam productQueryParam);

  ApiResponse<CreateProductVO> addProduct(CreateProductParam createProductParam);

  ApiResponse<EmptyVO> updateProduct(Long productId, UpdateProductParam updateProductParam);

  ApiResponse<EmptyVO> deleteProduct(Long productId);

  ApiResponse<ProductVO> getProductInfo(Long productId);

  ApiResponse<List<ProductVO>> getMyProduct(ProductQueryParam productQueryParam);

  ApiResponse<EmptyVO> updateProductType(Long productId, Integer status);

  ApiResponse<List<ProductVO>> search(ProductSearchParam productSearchParam);

  ApiResponse<ImageVO> uploadProductImg(Long productId, MultipartFile file);

  ApiResponse<List<ImageVO>> getProductImages(Long productId);

  ApiResponse<EmptyVO> deleteProductImage(Long productId, Long imagesId);

  ApiResponse<CreateNegotiateVO> initiateBargain(Long productId, CreateNegotiateParam createNegotiateParam);

  ApiResponse<List<ProductNegotiateVO>> getBargainList(Long productId, ProductNegotiateParam param);

  ApiResponse<NegotiateStatusVO> handleBargain(Long productId, Long bargainId, NegotiateStatusParam param);

  ApiResponse<List<HotProductVO>> productPopularityHeatMap(int top);

  ApiResponse<ViewVO> realTimeViews(Long productId);

  ApiResponse<EmptyVO> batchShelf();

  ApiResponse<EmptyVO> batchDelete();
}
