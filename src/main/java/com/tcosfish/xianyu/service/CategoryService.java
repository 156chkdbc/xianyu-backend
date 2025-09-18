package com.tcosfish.xianyu.service;

import com.tcosfish.xianyu.model.base.ApiResponse;
import com.tcosfish.xianyu.model.base.EmptyVO;
import com.tcosfish.xianyu.model.dto.category.CreateCategoryDto;
import com.tcosfish.xianyu.model.dto.category.UpdateCategoryDto;
import com.tcosfish.xianyu.model.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tcosfish.xianyu.model.vo.category.CategoryFlatVO;
import com.tcosfish.xianyu.model.vo.category.CategoryVO;
import com.tcosfish.xianyu.model.vo.category.CreateCategoryVO;

import java.util.List;

/**
* @author Administrator
* @description 针对表【category(类目)】的数据库操作Service
* @createDate 2025-09-10 20:11:06
*/
public interface CategoryService extends IService<Category> {

  ApiResponse<List<CategoryVO>> categoryList();

  ApiResponse<List<CategoryFlatVO>> childCategoryListFlat(Long categoryId);

  ApiResponse<List<CategoryVO>> childCategoryListTree(Long categoryId);

  ApiResponse<CreateCategoryVO> createCategory(CreateCategoryDto createCategoryDto);

  ApiResponse<EmptyVO> updateCategory(Long categoryId, UpdateCategoryDto updateCategoryDto);

  ApiResponse<EmptyVO> deleteCategory(Long categoryId);
}
