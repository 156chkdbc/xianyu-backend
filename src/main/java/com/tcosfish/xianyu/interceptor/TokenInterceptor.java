package com.tcosfish.xianyu.interceptor;

import com.tcosfish.xianyu.model.enums.RedisKey;
import com.tcosfish.xianyu.scope.RequestScopeData;
import com.tcosfish.xianyu.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author tcosfish
 * @apiNote 拦截请求, 处理 token 填充 RequestScopeData
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {
  private final JwtUtil jwtUtil;
  private final RequestScopeData scope;
  private final StringRedisTemplate redisTemplate;

  @Autowired
  public TokenInterceptor(RequestScopeData requestScopeData, JwtUtil jwtUtil, StringRedisTemplate stringRedisTemplate) {
    this.scope = requestScopeData;
    this.jwtUtil = jwtUtil;
    this.redisTemplate = stringRedisTemplate;
  }

  @Override
  public boolean preHandle(
    HttpServletRequest request,
    HttpServletResponse response,
    Object handler
  ) throws Exception {
    String header = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (header == null || !header.startsWith("Bearer ")) {
      setAnonymous();
      return true;
    }

    String token = header.substring(7);
    if (!jwtUtil.validateToken(token)) {
      setAnonymous();
      return true;
    }

    String jti = jwtUtil.getJtiFromToken(token);
    Long userId = jwtUtil.getUserIdFromToken(token);

    // 黑名单, redis中是否存在对应记录
    String blackKey = RedisKey.blacklist(jti, userId);
    if (Boolean.TRUE.equals(redisTemplate.hasKey(blackKey))) {
      setAnonymous();
      return true;
    }

    // 白名单（可选，刷新时会用）
    String whiteKey = RedisKey.tokenJti(jti);
    String uid = redisTemplate.opsForValue().get(whiteKey);
    if (!userId.toString().equals(uid)) {   // 找不到也算失效
      setAnonymous();
      return true;
    }

    scope.setUserId(userId);
    scope.setJti(jti);
    scope.setToken(token);
    scope.setLogin(true);
    scope.setExpiration(jwtUtil.getExpiration(token));
    return true;
  }

  private void setAnonymous() {
    scope.setLogin(false);
    scope.setUserId(null);
    scope.setJti(null);
    scope.setToken(null);
    scope.setExpiration(null);
  }
}
