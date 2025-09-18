package com.tcosfish.xianyu.model.dto.user;

import lombok.Data;

/**
 * @author tcosfish
 */
@Data
public class UserProfileDTO {
  private Long id;
  private String username;
  private String passwordHash;
  private String phone;
  private String email;
  private Integer status;

  // 来自user_profile表的字段
  private String avatar;
  private String bio;
  private Integer gender;
  private String school;
  private String college;
  private String grade;
  private String studentNo;
}
