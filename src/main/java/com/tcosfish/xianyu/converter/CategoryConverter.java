package com.tcosfish.xianyu.converter;

import com.tcosfish.xianyu.model.dto.category.CreateCategoryDto;
import com.tcosfish.xianyu.model.dto.category.UpdateCategoryDto;
import com.tcosfish.xianyu.model.entity.Category;
import com.tcosfish.xianyu.model.vo.category.CategoryFlatVO;
import com.tcosfish.xianyu.model.vo.category.CategoryVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author tcosfish
 * @description MapStruct 在编译期生成实现类并注册为 Spring Bean
 */
@Mapper(componentModel = "spring")
public interface CategoryConverter {
  CategoryFlatVO toFlatVO(Category entity);

  /*  children 手工装配，不在这里映射 -> vo.setChildren(build(grouped, c.getId()))  */
  CategoryVO toTreeVO(Category entity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "level", ignore = true)
  Category toEntity(CreateCategoryDto dto);

  @Mapping(target = "level", ignore = true)
  Category toEntity(UpdateCategoryDto dto);
}
