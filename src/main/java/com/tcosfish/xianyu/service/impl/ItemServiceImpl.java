package com.tcosfish.xianyu.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcosfish.xianyu.model.dto.product.ProductDto;
import com.tcosfish.xianyu.model.dto.product.ProductQueryParam;
import com.tcosfish.xianyu.model.dto.product.ProductSearchParam;
import com.tcosfish.xianyu.model.entity.Item;
import com.tcosfish.xianyu.mapper.ItemMapper;
import com.tcosfish.xianyu.model.vo.product.ProductVO;
import com.tcosfish.xianyu.service.ItemService;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【item(统一商品/服务实体)】的数据库操作Service实现
* @createDate 2025-09-10 14:17:59
*/
@Service
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item>
    implements ItemService {
  @Override
  public IPage<ProductDto> pageProductDto(ProductQueryParam queryParam) {
    // 1. 创建分页对象（泛型用 VO，MP 插件自动回填总数）
    Page<ProductVO> page = new Page<>(queryParam.getPage(), queryParam.getPageSize());

    // 2. 调用自定义 Mapper（baseMapper 就是 ItemMapper）
    return baseMapper.selectProductDtoPage(page, queryParam);
  }

  @Override
  public ProductDto selectProduct(Long productId) {
    return baseMapper.selectProductDtoById(productId);
  }

  @Override
  public IPage<ProductDto> pageProductDto(ProductSearchParam searchParam) {
    Page<ProductVO> page = new Page<>(searchParam.getPage(), searchParam.getPageSize());
    return baseMapper.search(page, searchParam);
  }
}




