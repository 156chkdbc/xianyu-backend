package com.tcosfish.xianyu.model.vo.user;

import lombok.Data;

/**
 * @author tcosfish
 */
@Data
public class UserProfileVO {
  private Long id;
  private String username;
  private String phone;
  private String email;

  // 来自user_profile表的字段
  private String avatar;
  private String bio;
  private Integer gender;
  private String school;
  private String college;
  private String grade;
  private String studentNo;
}
