package com.tcosfish.xianyu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcosfish.xianyu.annotation.NeedLogin;
import com.tcosfish.xianyu.annotation.TraceLog;
import com.tcosfish.xianyu.converter.CategoryConverter;
import com.tcosfish.xianyu.exception.BizException;
import com.tcosfish.xianyu.model.base.ApiResponse;
import com.tcosfish.xianyu.model.base.EmptyVO;
import com.tcosfish.xianyu.model.dto.category.CreateCategoryDto;
import com.tcosfish.xianyu.model.dto.category.UpdateCategoryDto;
import com.tcosfish.xianyu.model.entity.Category;
import com.tcosfish.xianyu.model.vo.category.CategoryFlatVO;
import com.tcosfish.xianyu.model.vo.category.CategoryVO;
import com.tcosfish.xianyu.model.vo.category.CreateCategoryVO;
import com.tcosfish.xianyu.service.CategoryService;
import com.tcosfish.xianyu.mapper.CategoryMapper;
import com.tcosfish.xianyu.utils.ApiResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【category(类目)】的数据库操作Service实现
* @createDate 2025-09-10 20:11:06
*/
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService{

  public static final Long ROOT_ID = 0L;

  private final CategoryConverter converter;

  @Override
  @TraceLog(desc = "获取分类列表")
  public ApiResponse<List<CategoryVO>> categoryList() {
    List<Category> list = list();
    return ApiResponseUtil.success("获取分类列表成功", buildTree(list, ROOT_ID));
  }

  @Override
  @TraceLog(desc = "获取子类别, 平铺")
  public ApiResponse<List<CategoryFlatVO>> childCategoryListFlat(Long categoryId) {
    checkExists(categoryId);
    List<CategoryFlatVO> categoryFlatVOList = lambdaQuery().eq(Category::getParentId, categoryId)
      .list()
      .stream()
      .map(converter::toFlatVO)
      .collect(Collectors.toList());

    return ApiResponseUtil.success("获取指定ID的分类成功", categoryFlatVOList);
  }

  @Override
  @TraceLog(desc = "获取子类别, 树状")
  public ApiResponse<List<CategoryVO>> childCategoryListTree(Long categoryId) {
    checkExists(categoryId);
    List<Category> subTree = baseMapper.selectChildrenRecursive(categoryId);
    List<CategoryVO> categoryVOList = buildTree(subTree, categoryId);
    return ApiResponseUtil.success("获取指定ID的分类成功", categoryVOList);
  }

  @Override
  @NeedLogin
  @TraceLog(desc = "新增类别")
  @Transactional(rollbackFor = Exception.class)
  public ApiResponse<CreateCategoryVO> createCategory(CreateCategoryDto createCategoryDto) {
    Category parent = getById(createCategoryDto.getParentId());
    if (parent == null) {
      throw new BizException("父类别不存在");
    }

    Category category = converter.toEntity(createCategoryDto);
    category.setLevel(parent.getLevel() + 1);
    if (!save(category)) {
      throw new BizException("类别新增失败");
    }

    CreateCategoryVO createCategoryVO = new CreateCategoryVO();
    createCategoryVO.setCategoryId(category.getId());
    return ApiResponseUtil.success("类别新增成功", createCategoryVO);
  }

  @Override
  @NeedLogin
  @TraceLog(desc = "更新类别")
  @Transactional(rollbackFor = Exception.class)
  public ApiResponse<EmptyVO> updateCategory(Long categoryId, UpdateCategoryDto updateCategoryDto) {
    checkExists(categoryId);
    checkExists(updateCategoryDto.getParentId(), "指定的父类别不存在");

    Category category = converter.toEntity(updateCategoryDto);
    if (!lambdaUpdate().eq(Category::getId, categoryId).update(category)) {
      return ApiResponseUtil.error("类别修改失败");
    }

    return ApiResponseUtil.success("类别修改成功");
  }

  @Override
  @NeedLogin
  @TraceLog(desc = "删除类别")
  @Transactional(rollbackFor = Exception.class)
  public ApiResponse<EmptyVO> deleteCategory(Long categoryId) {
    checkExists(categoryId);

    if (!removeById(categoryId)) {
      throw new BizException("删除失败，可能已被占用");
    }

    return ApiResponseUtil.success("删除类别成功");
  }

  /* ===================== 私有工具 ===================== */

  private void checkExists(Long id) {
    if (!lambdaQuery().eq(Category::getId, id).exists()) {
      throw new BizException("指定类别不存在");
    }
  }

  private void checkExists(Long id, String message) {
    if (!lambdaQuery().eq(Category::getId, id).exists()) {
      throw new BizException(message);
    }
  }

  /**
   * 通用建树（O(n)）
   */
  private List<CategoryVO> buildTree(List<Category> list, Long parentId) {
    Map<Long, List<Category>> grouped = list.stream()
      .collect(Collectors.groupingBy(Category::getParentId));
    return build(grouped, parentId);
  }

  private List<CategoryVO> build(Map<Long, List<Category>> grouped, Long pid) {
    return grouped.getOrDefault(pid, Collections.emptyList())
      .stream()
      .map(c -> {
        CategoryVO vo = converter.toTreeVO(c);
        vo.setChildren(build(grouped, c.getId()));
        return vo;
      })
      .collect(Collectors.toList());
  }
}
