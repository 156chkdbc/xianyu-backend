package com.tcosfish.xianyu.controller;

import com.tcosfish.xianyu.model.base.ApiResponse;
import com.tcosfish.xianyu.model.base.EmptyVO;
import com.tcosfish.xianyu.model.dto.category.CreateCategoryDto;
import com.tcosfish.xianyu.model.dto.category.UpdateCategoryDto;
import com.tcosfish.xianyu.model.vo.category.CategoryFlatVO;
import com.tcosfish.xianyu.model.vo.category.CategoryVO;
import com.tcosfish.xianyu.model.vo.category.CreateCategoryVO;
import com.tcosfish.xianyu.service.impl.CategoryServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author tcosfish
 */
@Tag(name="分类管理", description = "分类相关接口")
@RestController
@RequestMapping("/categories")
public class CategoryController {

  private final CategoryServiceImpl categoryService;

  public CategoryController(CategoryServiceImpl categoryServiceImpl) {
    this.categoryService = categoryServiceImpl;
  }

  @GetMapping("")
  @Operation(summary = "获取分类列表", description = "可指定类别级别")
  public ApiResponse<List<CategoryVO>> getCategories() {
    return categoryService.categoryList();
  }

  @GetMapping(value = "/{categoryId}/children", params = "recursive=false")
  @Operation(summary = "获取子类别", description = "只返回直接子节点（平铺列表）")
  public ApiResponse<List<CategoryFlatVO>> getChildCategoriesFlat(
    @Min(value = 1, message = "类别ID必须是正整数") @PathVariable Long categoryId
  ) {
    return categoryService.childCategoryListFlat(categoryId);
  }


  @GetMapping(value = "/{categoryId}/children", params = "recursive=true")
  @Operation(summary = "获取子类别", description = "递归返回，带 children 嵌套")
  public ApiResponse<List<CategoryVO>> getChildCategoriesTree(
    @Min(value = 1, message = "类别ID必须是正整数") @PathVariable Long categoryId
  ) {
    return categoryService.childCategoryListTree(categoryId);
  }

  @PostMapping("")
  @Operation(summary = "后台新建类别")
  public ApiResponse<CreateCategoryVO> createCategory(
    @Valid @RequestBody CreateCategoryDto createCategoryDto
  ) {
    return categoryService.createCategory(createCategoryDto);
  }

  @PutMapping("/{categoryId}")
  @Operation(summary = "后台修改类别名称")
  public ApiResponse<EmptyVO> updateCategory(
    @PathVariable Long categoryId,
    @RequestBody UpdateCategoryDto updateCategoryDto
  ) {
    return categoryService.updateCategory(categoryId, updateCategoryDto);
  }

  @DeleteMapping("/{categoryId}")
  @Operation(summary = "后台删除类别(假删除)")
  public ApiResponse<EmptyVO> deleterCategory(
    @Min(value = 1, message = "类别ID 必须是正整数") @PathVariable Long categoryId
  ) {
    return categoryService.deleteCategory(categoryId);
  }
}
