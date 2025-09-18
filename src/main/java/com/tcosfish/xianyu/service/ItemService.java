package com.tcosfish.xianyu.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tcosfish.xianyu.model.dto.product.ProductDto;
import com.tcosfish.xianyu.model.dto.product.ProductQueryParam;
import com.tcosfish.xianyu.model.dto.product.ProductSearchParam;
import com.tcosfish.xianyu.model.entity.Item;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【item(统一商品/服务实体)】的数据库操作Service
* @createDate 2025-09-10 14:17:59
*/
public interface ItemService extends IService<Item> {
  /**
   * 商品卡片分页（含卖家信息）
   */
  IPage<ProductDto> pageProductDto(ProductQueryParam queryParam);

  ProductDto selectProduct(Long productId);

  IPage<ProductDto> pageProductDto(ProductSearchParam searchParam);
}
