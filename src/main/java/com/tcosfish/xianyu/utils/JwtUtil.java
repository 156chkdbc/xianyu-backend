package com.tcosfish.xianyu.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author tcosfish
 * @apiNote token相关类
 */
@Component
public class JwtUtil {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration}")
  private Long expiration; // 秒

  /** 统一生成 SecretKey（缓存避免重复计算） */
  private SecretKey getSecretKey() {
    // 保证 secret 长度 ≥ 64 字节（HS512）
    if (secret.length() < 64) {
      throw new IllegalArgumentException("jwt.secret 必须 ≥ 64 字节");
    }
    return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  /** 生成 token */
  public String generateToken(Long userId) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", userId);   // 只存 userId → 能鉴权，但无法细粒度撤销
    String jti = UUID.randomUUID().toString();   // 全局唯一, 用它当key可以精确到是哪一张token, 避免该用其他端的登录也被登出
    claims.put("jti", jti);         // 额外存 jti → 既能鉴权，又能单点/单设备/多端差异化踢人。

    return Jwts.builder()
      .setId(jti)                                         // ③ 标准字段
      .setClaims(claims)
      .setIssuedAt(new Date())
      .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
      .signWith(getSecretKey(), SignatureAlgorithm.HS512)
      .compact();
  }

  public String getJtiFromToken(String token) {
    return parseClaims(token).getId();
  }

  /** 解析 userId */
  public Long getUserIdFromToken(String token) {
    Claims claims = parseClaims(token);
    return Long.valueOf(claims.get("userId").toString());
  }

  /** 新增：获取 token 过期时间 */
  public Long getExpiration(String token) {
    Date exp = parseClaims(token)
      .getExpiration();
    return TimeUnit.MILLISECONDS.toSeconds(exp.getTime() - System.currentTimeMillis());
  }

  /** 验证 token */
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder()
        .setSigningKey(getSecretKey())
        .build()
        .parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  /** 从claims中取得存放的所有消息 */
  private Claims parseClaims(String token) {
    return Jwts.parserBuilder().setSigningKey(getSecretKey()).build()
      .parseClaimsJws(token).getBody();
  }

  /** 从header中获取 token */
  public Optional<String> parseJwt(HttpServletRequest request) {
    return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
      .filter(h -> h.startsWith("Bearer "))
      .map(h -> h.substring(7));
  }
}