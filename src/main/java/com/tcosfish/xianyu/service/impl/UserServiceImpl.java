package com.tcosfish.xianyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcosfish.xianyu.annotation.NeedLogin;
import com.tcosfish.xianyu.annotation.TraceLog;
import com.tcosfish.xianyu.exception.BizException;
import com.tcosfish.xianyu.model.base.ApiResponse;
import com.tcosfish.xianyu.model.base.EmptyVO;
import com.tcosfish.xianyu.model.base.Pagination;
import com.tcosfish.xianyu.model.dto.user.*;
import com.tcosfish.xianyu.model.entity.User;
import com.tcosfish.xianyu.model.entity.UserProfile;
import com.tcosfish.xianyu.model.entity.UserRole;
import com.tcosfish.xianyu.model.enums.RedisKey;
import com.tcosfish.xianyu.model.vo.user.*;
import com.tcosfish.xianyu.scope.RequestScopeData;
import com.tcosfish.xianyu.service.UserService;
import com.tcosfish.xianyu.mapper.UserMapper;
import com.tcosfish.xianyu.utils.ApiResponseUtil;
import com.tcosfish.xianyu.utils.JwtUtil;
import com.tcosfish.xianyu.utils.StudentNoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 * @description 针对表【user(用户-热数据)】的数据库操作Service实现
 * @createDate 2025-09-08 11:00:37
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
  implements UserService {

  private final JwtUtil jwtUtil;
  private final PasswordEncoder passwordEncoder;
  private final EmailServiceImpl emailService;
  private final UserRoleServiceImpl userRoleService;
  private final UserProfileServiceImpl userProfileService;
  private final UserMapper userMapper;
  private final RequestScopeData requestScopeData;
  private final FileServiceImpl fileService;
  private final StringRedisTemplate redisTemplate;

  @Override
  public ApiResponse<LoginUserVO> login(LoginUserDto loginUserDto) {
    User user = lambdaQuery()
      .eq(User::getEmail, loginUserDto.getEmail())
      .or().eq(User::getUsername, loginUserDto.getUsername())
      .one();
    // 记录登录失败次数
    if (user == null || !passwordEncoder.matches(loginUserDto.getPassword(), user.getPasswordHash())) {
      // 记录失败次数, 超过5次, 可以直接抛出登录频繁, 稍后再试
      String key = "login_fail:uid" + (user == null ? "null" : user.getId());
      String failNum = redisTemplate.opsForValue().get(key);
      if (failNum != null && Integer.parseInt(failNum) > 5) {
        return ApiResponseUtil.error("登录频繁, 请15分钟后再试");
      }
      redisTemplate.opsForValue().increment(key, 1);
      redisTemplate.expire(key, 15, TimeUnit.MINUTES);
      return ApiResponseUtil.error("账号或密码错误");
    }
    // 失败次数清零
    redisTemplate.delete("login_fail:uid:" + user.getId());
    // 剔除旧设备
    String oldToken = redisTemplate.opsForValue().get(RedisKey.online(user.getId()));
    if (oldToken != null) {
      long remain = jwtUtil.getExpiration(oldToken);
      String oldJti = jwtUtil.getJtiFromToken(oldToken);
      redisTemplate.opsForValue().set(RedisKey.blacklist(oldJti, user.getId()), "1", remain, TimeUnit.SECONDS);
    }
    // 生成新 token
    String newToken = jwtUtil.generateToken(user.getId());
    String newJti = jwtUtil.getJtiFromToken(newToken);
    long remain = jwtUtil.getExpiration(newToken);
    // 写白名单 + 在线记录
    redisTemplate.opsForValue().set(RedisKey.tokenJti(newJti), user.getId().toString(), remain, TimeUnit.SECONDS);
    redisTemplate.opsForValue().set(RedisKey.online(user.getId()), newToken, remain, TimeUnit.SECONDS);
    // 生成 refresh = 随机串，有效期 7 天
    String refreshToken = UUID.randomUUID().toString();
    redisTemplate.opsForValue().set(
      RedisKey.refresh(refreshToken),      // refresh:XXX -> uid
      user.getId().toString(),
      7 * 24 * 3600,
      TimeUnit.SECONDS);

    LoginUserVO userVO = new LoginUserVO();
    BeanUtils.copyProperties(user, userVO);
    TokenVO tokenVO = TokenVO.builder()
      .accessToken(newToken)
      .tokenType("Bearer")
      .refreshToken(refreshToken)
      .expireAt(Instant.now().getEpochSecond() + remain)
      .expiresIn(remain)
      .build();
    // 为什么jti也要传递给前端呢
    return ApiResponseUtil.success("登录成功", userVO, tokenVO);
  }

  @Override
  @Transactional // 操作多条sql语句涉及到非查询操作则需要进行事务处理
  public ApiResponse<RegisterUserVO> register(RegisterUserDto userDto) {
    // 唯一性验证
    if (lambdaQuery().eq(User::getUsername, userDto.getUsername()).count() != 0 ||
      lambdaQuery().eq(User::getPhone, userDto.getPhone()).count() != 0
    ) {
      return ApiResponseUtil.error("注册失败, 用户已存在");
    }
    // 额外验证, 若填写邮箱则需要验证, 该邮箱是否本人使用
    if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {
      if (lambdaQuery().eq(User::getEmail, userDto.getEmail()).count() != 0) {
        return ApiResponseUtil.error("注册失败, 邮箱已注册");
      }
      if (userDto.getVerifyCode() == null || userDto.getVerifyCode().isEmpty()) {
        return ApiResponseUtil.error("请提供验证码");
      }
      // 验证邮箱验证码
      if (!emailService.checkVerificationCode(userDto.getEmail(), userDto.getVerifyCode())) {
        return ApiResponseUtil.error("验证码无效或已过期");
      }
    }

    // 新增用户
    User user = new User();
    BeanUtils.copyProperties(userDto, user);
    user.setPasswordHash(passwordEncoder.encode(userDto.getPassword()));
    if (!save(user)) {
      throw new BizException("注册失败");
    }
    // 用户新增时, 自动分配角色: 普通用户
    UserRole userRole = new UserRole();
    userRole.setUserId(user.getId());
    userRole.setRoleId(1L);
    if (!userRoleService.save(userRole)) {
      throw new BizException("注册失败");
    }
    // 用户新增时, 创建详细资料表: user_profile, 学号使用了模拟生成
    UserProfile userProfile = new UserProfile();
    userProfile.setUserId(user.getId());
    userProfile.setStudentNo(StudentNoUtil.generator(user.getId()));
    if (!userProfileService.save(userProfile)) {
      throw new BizException("注册失败");
    }

    RegisterUserVO registerUserVO = new RegisterUserVO();
    BeanUtils.copyProperties(user, registerUserVO);

    return ApiResponseUtil.success("注册成功", registerUserVO);
  }

  @Override
  public ApiResponse<RegisterUserVO> sendCaptcha(String email) {
    String verifyCode = emailService.sendVerificationCode(email);
    System.out.println("生成的验证码是: " + verifyCode);
    return ApiResponseUtil.success("验证码已发送...");
  }

  @Override
  @NeedLogin
  public ApiResponse<UserProfileVO> getUserProfile(Long userId) {
    // 需要调用复杂sql语句
    UserProfileDTO userProfileDTO = userMapper.selectUserProfile(userId);

    if (userProfileDTO == null) {
      return ApiResponseUtil.error("获取详细信息失败, 不存在该用户");
    }

    UserProfileVO userProfileVO = new UserProfileVO();
    BeanUtils.copyProperties(userProfileDTO, userProfileVO);
    return ApiResponseUtil.success("用户详细信息", userProfileVO);
  }

  @Override
  @NeedLogin
  public ApiResponse<UserProfileVO> updateUserProfile(UpdateUserDto updateUserDto) {
    // 默认的登录状态, 需要记录请求过程中获取的 userId
    Long userId = requestScopeData.getUserId();
    UserProfile foundUserProfile = userProfileService.lambdaQuery().eq(UserProfile::getUserId, userId).one();

    if (foundUserProfile == null) {
      return ApiResponseUtil.error("用户更新失败, 用户不存在");
    }

    // 在原来的基础上进行覆盖
    BeanUtils.copyProperties(updateUserDto, foundUserProfile);
    if (!userProfileService.lambdaUpdate().eq(UserProfile::getUserId, userId).update(foundUserProfile)) {
      return ApiResponseUtil.error("用户更新失败, 执行修改时");
    }

    return ApiResponseUtil.success("用户更新成功");
  }

  @Override
  @NeedLogin
  //  只读信息，不再发token
  public ApiResponse<LoginUserVO> whoami() {
    Long userId = requestScopeData.getUserId();
    User user = getById(userId);
    if (user == null) {
      return ApiResponseUtil.error("用户不存在");
    }
    LoginUserVO loginUserVO = new LoginUserVO();
    BeanUtils.copyProperties(user, loginUserVO);
    return ApiResponseUtil.success("登录成功", loginUserVO);
  }

  @Override
  @NeedLogin
  @TraceLog(desc = "用户单独更新头像")
  public ApiResponse<AvatarVO> uploadAvatar(MultipartFile file) {
    try {
      String url = fileService.uploadImage(file);
      AvatarVO avatarVO = new AvatarVO();
      avatarVO.setUrl(url);
      return ApiResponseUtil.success("上传成功", avatarVO);
    } catch (Exception e) {
      return ApiResponseUtil.error(e.getMessage());
    }
  }

  @Override
  @NeedLogin
  @TraceLog(desc = "用户登出")
  public ApiResponse<EmptyVO> logout() {
    Long userId = requestScopeData.getUserId();
    String jti = requestScopeData.getJti();
    long remain = requestScopeData.getExpiration();

    // 加入黑名单
    redisTemplate.opsForValue().set(
      RedisKey.blacklist(jti, userId), "1", remain, TimeUnit.SECONDS);

    // 清白名单 & 在线
    redisTemplate.delete(RedisKey.tokenJti(jti));
    redisTemplate.delete(RedisKey.online(userId));

    return ApiResponseUtil.success("用户登出成功");
  }

  @Override
  @TraceLog(desc = "刷新token")
  public ApiResponse<EmptyVO> refresh(String refreshToken) {
    /* 1. 用 refreshToken 反查 uid */
    String uid = redisTemplate.opsForValue().get(RedisKey.refresh(refreshToken));
    if (uid == null) {
      return ApiResponseUtil.error("refreshToken 不存在或已过期");
    }
    Long userId = Long.valueOf(uid);

    /* 2. 把当前 accessToken 拉黑（前端必须带 Authorization） */
    String oldAccessToken = requestScopeData.getToken();
    String oldJti = jwtUtil.getJtiFromToken(oldAccessToken);
    long remain = jwtUtil.getExpiration(oldAccessToken);
    redisTemplate.opsForValue().set(
      RedisKey.blacklist(oldJti, userId), "1", remain, TimeUnit.SECONDS);

    /* 3. 生成新双 token */
    String newAccessToken = jwtUtil.generateToken(userId);
    String newJti = jwtUtil.getJtiFromToken(newAccessToken);
    String newRefreshToken = UUID.randomUUID().toString();
    long newAccRemain = jwtUtil.getExpiration(newAccessToken);
    long newRefRemain = 7 * 24 * 3600;

    /* 4. 写 Redis */
    redisTemplate.opsForValue().set(RedisKey.tokenJti(newJti), uid, newAccRemain, TimeUnit.SECONDS);
    redisTemplate.opsForValue().set(RedisKey.online(userId), newAccessToken, newAccRemain, TimeUnit.SECONDS);
    redisTemplate.opsForValue().set(RedisKey.refresh(newRefreshToken), uid, newRefRemain, TimeUnit.SECONDS);
    // 老 refreshToken 一次性失效
    redisTemplate.delete(RedisKey.refresh(refreshToken));

    /* 5. 返回 */
    TokenVO tokenVO = TokenVO.builder()
      .accessToken(newAccessToken)
      .refreshToken(newRefreshToken)
      .expireAt(Instant.now().getEpochSecond() + newAccRemain)
      .expiresIn(newAccRemain)
      .build();
    return ApiResponseUtil.success("token已刷新", null, tokenVO);
  }

  @Override
  @NeedLogin
  @TraceLog(desc = "获取用户热表消息")
  public ApiResponse<UserVO> getUserInfo(long id) {
    User user = lambdaQuery().eq(User::getId, id).one();
    if (user == null) {
      return ApiResponseUtil.error("用户不存在");
    }

    UserVO userVO = new UserVO();
    BeanUtils.copyProperties(user, userVO);
    return ApiResponseUtil.success("获取用户信息成功", userVO);
  }

  @Override
  @NeedLogin
  @TraceLog(desc = "更新用户热表信息")
  public ApiResponse<UserVO> updateUserInfo(Long userId, UserDto userDto) {
    if (lambdaQuery().eq(User::getId, userId).count() == 0) {
      return ApiResponseUtil.error("用户不存在");
    }

    User user = new User();
    BeanUtils.copyProperties(userDto, user);
    if (!lambdaUpdate().eq(User::getId, userId).update(user)) {
      return ApiResponseUtil.error("用户数据更新失败");
    }

    return ApiResponseUtil.success("用户数据更新成功");
  }

  @Override
  public ApiResponse<List<User>> getUserList(UserQueryParam queryParam) {
    Page<User> page = new Page<>(queryParam.getPage(), queryParam.getPageSize());

    // 增加筛选条件, 若该值 == null, 会自动忽略
    LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper
      .eq(queryParam.getUsername() != null, User::getUsername, queryParam.getUsername())
      .eq(queryParam.getEmail() != null, User::getEmail, queryParam.getEmail())
      .eq(queryParam.getUserId() != null, User::getId, queryParam.getUserId());

    Page<User> userPage = page(page, queryWrapper);
    List<User> userList = userPage.getRecords();
    long total = userPage.getTotal();
    Pagination pagination = new Pagination(queryParam.getPage(), queryParam.getPageSize(), total);

    return ApiResponseUtil.success("用户列表", userList, pagination);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public ApiResponse<EmptyVO> deleteUser(Long userId) {
    if (!removeById(userId)) {
      throw new BizException("用户不存在");
    }

    // 1. 清子表
    userRoleService.lambdaUpdate().eq(UserRole::getUserId, userId).remove();
    userProfileService.lambdaUpdate().eq(UserProfile::getUserId, userId).remove();

    // 2. 清 Redis 所有相关 key
    String token = redisTemplate.opsForValue().get(RedisKey.online(userId));
    if (token != null) {
      String jti = jwtUtil.getJtiFromToken(token);
      redisTemplate.delete(RedisKey.tokenJti(jti));   // 白名单
      redisTemplate.delete(RedisKey.online(userId));  // 在线
      // redisTemplate.delete(RedisKey.refresh(userId)); // refresh
      // 把当前 token 也拉黑，避免“删完人立刻再访问”
      long remain = jwtUtil.getExpiration(token);
      redisTemplate.opsForValue().set(
        RedisKey.blacklist(jti, userId), "1", remain, TimeUnit.SECONDS);
    }
    return ApiResponseUtil.success("删除成功");
  }
}