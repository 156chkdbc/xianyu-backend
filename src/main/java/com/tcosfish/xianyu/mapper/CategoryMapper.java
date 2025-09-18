package com.tcosfish.xianyu.mapper;

import com.tcosfish.xianyu.model.entity.Category;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author Administrator
* @description 针对表【category(类目)】的数据库操作Mapper
* @createDate 2025-09-10 20:11:06
* @Entity com.tcosfish.xianyu.model.entity.Category
*/
public interface CategoryMapper extends BaseMapper<Category> {
  /**
   * 递归获取以 parentId 为根的子树（包含自身）
   * MySQL 8 及以上
   * MySQL 8 的递归公用表表达式（Recursive CTE），作用一句话：
   * 把 parentId 作为根节点，一次性把它的所有后代（子、孙、曾孙…）全部捞出来，并按层级排序。
   */
  @Select("""
        WITH RECURSIVE cte AS (
            SELECT * FROM category WHERE id = #{parentId}
            UNION ALL
            SELECT t.* FROM category t INNER JOIN cte ON t.parent_id = cte.id
        )
        SELECT * FROM cte ORDER BY level
        """)
  List<Category> selectChildrenRecursive(@Param("parentId") Long parentId);
}




