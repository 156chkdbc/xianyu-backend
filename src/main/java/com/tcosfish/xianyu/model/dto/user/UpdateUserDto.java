package com.tcosfish.xianyu.model.dto.user;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author tcosfish
 */
@Data
public class UpdateUserDto {

  /**
   * 用户头像
   * 非必填，必须是有效的 URL。
   */
  @Pattern(regexp = "^(https?|ftp)://.*$", message = "头像地址必须是有效的 URL")
  private String avatar;

  /**
   * 用户简介
   * 非必填，长度在 1-128 个字符。
   */
  @Size(max = 128, message = "签名长度不能超过 128 个字符")
  private String bio;

  /**
   * 性别
   * 非必填，长度在 1-128 个字符。
   */
  @Min(value = 0, message = "性别取值无效")
  @Max(value = 2, message = "性别取值无效")
  private Integer gender;

  /**
   * 用户学校
   * 非必填，长度在 1-64 个字符。
   */
  @Size(max = 64, message = "学校名称长度不能超过 64 个字符")
  private String school;

  /**
   * 学院
   * 非必填，长度在 1-64 个字符。
   */
  @Size(max = 64, message = "学院名称长度不能超过 64 个字符")
  private String college;

  /**
   * 年级
   * 非必填，长度在 1-64 个字符。
   */
  @Size(max = 64, message = "年级长度不能超过 64 个字符")
  private String grade;
}
