package com.tcosfish.xianyu.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tcosfish.xianyu.model.dto.product.ProductDto;
import com.tcosfish.xianyu.model.dto.product.ProductQueryParam;
import com.tcosfish.xianyu.model.dto.product.ProductSearchParam;
import com.tcosfish.xianyu.model.entity.Item;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tcosfish.xianyu.model.vo.product.ProductVO;
import org.apache.ibatis.annotations.Param;

/**
* @author Administrator
* @description 针对表【item(统一商品/服务实体)】的数据库操作Mapper
* @createDate 2025-09-10 14:17:59
* @Entity com.tcosfish.xianyu.model.entity.Item
*/
public interface ItemMapper extends BaseMapper<Item> {
  IPage<ProductDto> selectProductDtoPage(@Param("page") Page<ProductVO> page, @Param("q") ProductQueryParam queryParam);

  ProductDto selectProductDtoById(@Param("productId") Long productId);

  IPage<ProductDto> search(@Param("page") Page<ProductVO> page, @Param("q") ProductSearchParam searchParam);
}




