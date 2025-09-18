package com.tcosfish.xianyu.filter;

import com.alibaba.fastjson2.JSON;
import com.tcosfish.xianyu.mapper.PermissionMapper;
import com.tcosfish.xianyu.model.dto.user.PermDto;
import com.tcosfish.xianyu.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author tcosfish
 */
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

  private final StringRedisTemplate redisTemplate;
  private final JwtUtil jwtUtil;
  private final PermissionMapper permissionMapper;

  public JwtAuthenticationTokenFilter(
    StringRedisTemplate redisTemplate,
    JwtUtil jwtUtil,
    PermissionMapper permissionMapper
  ) {
    this.redisTemplate = redisTemplate;
    this.jwtUtil = jwtUtil;
    this.permissionMapper = permissionMapper;
  }

  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain chain
  ) throws ServletException, IOException {

    String jwt = jwtUtil.parseJwt(request).get();          // 从 Header 取出
    Long userId = jwtUtil.getUserIdFromToken(jwt);

    // 1. 查缓存（Redis：key = user:10001:perms）
    Object permObjs = redisTemplate.opsForValue()
      .get("user:" + userId.toString() + ":perms");

    List<PermDto> perms = Optional.ofNullable(permObjs)
      .map(String::valueOf)
      .map(s -> JSON.parseArray(s, PermDto.class))
      .orElse(null);

    if (perms == null) {
      // 2. 缓存未命中，查 mysql
      // userId -> roleId -> permissions
      // roleId = userRoleMapper.selectById(userId);
      // perms = rolePermissionMapper.selectById(roleId); // 批量查询
      // redisTemplate.opsForValue().set("user:" + userId + ":perms", perms, Duration.ofMinutes(5));
      System.out.println("未有相应记录...");
      chain.doFilter(request, response);
      return;
    }

    // 3. 转成 Spring Security 认识的 GrantedAuthority
    Collection<? extends GrantedAuthority> authorities =
      perms.stream()
        .map(p -> new SimpleGrantedAuthority(p.getCode()))
        .collect(Collectors.toList());

    // 4. 构造 Authentication 对象，塞进 SecurityContext
    UsernamePasswordAuthenticationToken auth =
      new UsernamePasswordAuthenticationToken(
        userId, null, authorities);
    SecurityContextHolder.getContext().setAuthentication(auth);

    chain.doFilter(request, response);
  }
}
