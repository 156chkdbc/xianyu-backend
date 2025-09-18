package com.tcosfish.xianyu.service;

import com.tcosfish.xianyu.model.base.ApiResponse;
import com.tcosfish.xianyu.model.base.EmptyVO;
import com.tcosfish.xianyu.model.dto.user.*;
import com.tcosfish.xianyu.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tcosfish.xianyu.model.vo.user.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
* @author Administrator
* @description 针对表【user(用户-热数据)】的数据库操作Service
* @createDate 2025-09-08 11:00:37
*/
public interface UserService extends IService<User> {
  ApiResponse<LoginUserVO> login(LoginUserDto loginUserDto);

  ApiResponse<RegisterUserVO> register(RegisterUserDto registerUserDto);

  ApiResponse<RegisterUserVO> sendCaptcha(String email);

  ApiResponse<List<User>> getUserList(UserQueryParam queryParam);

  ApiResponse<UserProfileVO> getUserProfile(Long userId);

  ApiResponse<UserProfileVO> updateUserProfile(UpdateUserDto updateUserDto);

  ApiResponse<LoginUserVO> whoami();

  ApiResponse<AvatarVO> uploadAvatar(MultipartFile file);

  ApiResponse<EmptyVO> deleteUser(Long userId);

  ApiResponse<EmptyVO> logout();

  ApiResponse<EmptyVO> refresh(String refreshToken);

  ApiResponse<UserVO> getUserInfo(long id);

  ApiResponse<UserVO> updateUserInfo(Long userId, UserDto userDto);
}
