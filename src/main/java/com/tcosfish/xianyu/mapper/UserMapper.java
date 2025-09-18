package com.tcosfish.xianyu.mapper;

import com.tcosfish.xianyu.model.dto.user.UserProfileDTO;
import com.tcosfish.xianyu.model.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
* @author Administrator
* @description 针对表【user(用户-热数据)】的数据库操作Mapper
* @createDate 2025-09-08 11:00:37
* @Entity com.tcosfish.xianyu.model.entity.User
*/
public interface UserMapper extends BaseMapper<User> {
  @Select({
    """
      SELECT
      \t*\s
      FROM
      \tUSER u
      \tLEFT JOIN `user_profile` up ON u.id = up.user_id\s
      WHERE
      \tu.id = #{userId}""",  // MySQL JOIN语法
  })
    // 注意：返回值建议用DTO（如UserProfileDTO），避免字段冲突
  UserProfileDTO selectUserProfile(@Param("userId") long userId);
}




