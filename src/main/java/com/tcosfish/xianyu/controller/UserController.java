package com.tcosfish.xianyu.controller;

import com.tcosfish.xianyu.model.base.ApiResponse;
import com.tcosfish.xianyu.model.base.EmptyVO;
import com.tcosfish.xianyu.model.dto.user.*;
import com.tcosfish.xianyu.model.entity.User;
import com.tcosfish.xianyu.model.vo.user.*;
import com.tcosfish.xianyu.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author tcosfish
 */
@Tag(name = "用户管理", description = "用户相关操作")
@RestController
@RequestMapping("/users")
public class UserController {

  private final UserServiceImpl userService;

  public UserController(UserServiceImpl userServiceImpl) {
    this.userService = userServiceImpl;
  }

  @PostMapping("")
  @Operation(summary = "注册", description = "用户注册")
  public ApiResponse<RegisterUserVO> register(
    @RequestBody
    @Valid
    @Schema(name = "loginUserDto", description = "注册参数")
    RegisterUserDto registerUserDto
  ) {
    return userService.register(registerUserDto);
  }

  @PostMapping("/login")
  @Operation(summary = "登录", description = "用户登录")
  public ApiResponse<LoginUserVO> login(
    @RequestBody
    @Valid
    @Schema(name = "loginUserDto", description = "登录参数")
    LoginUserDto loginUserDto
  ) {
    return userService.login(loginUserDto);
  }

  @PostMapping("/logout")
  @Operation(summary = "登出", description = "退出登录")
  public ApiResponse<EmptyVO> logout() {
    return userService.logout();
  }

  @GetMapping("/sendCaptcha")
  @Operation(summary = "发送验证码", description = "发送验证码")
  public ApiResponse<RegisterUserVO> sendCaptcha(
    @NotBlank
    @Email
    @RequestParam("email")
    String email
  ) {
    return userService.sendCaptcha(email);
  }

  @GetMapping("/{userId}")
  @Operation(summary = "查看用户公开信息", description = "只涉及用户热表数据")
  public ApiResponse<UserVO> getUserInfo(
    @PathVariable
    @Pattern(regexp = "\\d+", message = "ID 格式错误")
    String userId
  ) {
    long id = Long.parseLong(userId);   // 已确保全是数字
    return userService.getUserInfo(id);
  }

  @PutMapping("/{userId}")
  @Operation(summary = "更新账号级字段", description = "只涉及用户热表数据")
  public ApiResponse<UserVO> updateUserInfo(
    @Min(value = 1, message = "用户ID必须为正整数") @PathVariable Long userId,
    @Valid @RequestBody UserDto userDto
  ) {
    // 冷表数据可更新, 热表数据不应可更新
    return userService.updateUserInfo(userId, userDto);
  }

  @PostMapping("/whoami")
  @Operation(summary = "自动登录服务")
  public ApiResponse<LoginUserVO> whoami() {
    return userService.whoami();
  }

  @PostMapping("/refresh")
  @Operation(summary = "刷新token")
  public ApiResponse<EmptyVO> refreshToken() {
    return userService.refresh("");
  }

  @DeleteMapping("/{userId}")
  @Operation(summary = "注销账号", description = "假删除")
  public ApiResponse<EmptyVO> deleteUser(
    @PathVariable @Pattern(regexp = "\\d+", message = "ID 格式错误") String userId
  ) {
    return userService.deleteUser(Long.parseLong(userId));
  }

  @GetMapping("/{userId}/profile")
  @Operation(summary = "查看校园档案", description = "根据用户id获取用户详细信息")
  public ApiResponse<UserProfileVO> getUserProfile(
    @PathVariable
    @Pattern(regexp = "\\d+", message = "ID 格式错误")
    String userId
  ) {
    long id = Long.parseLong(userId);   // 已确保全是数字
    return userService.getUserProfile(id);
  }

  @PutMapping("/{userId}/profile")
  @Operation(summary = "完善/修改档案")
  public ApiResponse<UserProfileVO> updateUserProfile(
    @Valid @RequestBody UpdateUserDto updateUserDto
  ) {
    // 冷表数据可更新, 热表数据不应可更新
    return userService.updateUserProfile(updateUserDto);
  }

  @PostMapping("/profile/avatar")
  @Operation(summary = "上传用户头像", description = "上传图片后返回一个静态资源连接")
  // 前端需要在上传用户图片后, 将返回的静态资源链接放到对应的User中
  public ApiResponse<AvatarVO> uploadAvatar(@RequestParam("file") MultipartFile file) {
    return userService.uploadAvatar(file);
  }

  @GetMapping("/admin/list")
  @Operation(summary = "管理员获取所有用户")
  public ApiResponse<List<User>> adminUserList(
    @Valid @RequestBody UserQueryParam queryParam
  ) {
    return userService.getUserList(queryParam);
  }

  @DeleteMapping("/admin/{userId}")
  @Operation(summary = "管理员根据userId删除用户")
  public ApiResponse<EmptyVO> adminDeleteUser(
    @PathVariable @Pattern(regexp = "\\d+", message = "ID 格式错误") String userId
  ) {
    return userService.deleteUser(Long.parseLong(userId));
  }
}
