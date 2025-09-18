package com.tcosfish.xianyu.config;

import com.tcosfish.xianyu.mapper.PermissionMapper;
import com.tcosfish.xianyu.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * @author tcosfish
 * @apiNote Spring Security 是一个强大的安全框架，用于保护基于 Spring 的应用程序。
 * 它提供了认证、授权、防止常见的安全攻击等功能
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final StringRedisTemplate redisTemplate;
  private final JwtUtil jwtUtil;
  private final PermissionMapper permissionMapper;

  // security 设置了filter就一定会被调用吗?
//  @Bean
//  public JwtAuthenticationTokenFilter jwtFilter() {
//    return new JwtAuthenticationTokenFilter(redisTemplate, jwtUtil, permissionMapper); // 传参
//  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      // 并非所有都需要验证
//      .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class)
      .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 配置 CORS
      .csrf(AbstractHttpConfigurer::disable) // 禁用 CSRF 保护
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/**").permitAll() // 允许所有用户访问 /api/** 路径
        // 由于 context-path 已经是 /api，这里写 /** 即可匹配 /api/** 的全部子路径
        .anyRequest().authenticated() // 其他请求需要认证
      )
      .formLogin(AbstractHttpConfigurer::disable) // 禁用表单登录
      .httpBasic(AbstractHttpConfigurer::disable); // 禁用 HTTP Basic 认证
    return http.build();
  }

  // 在这里使用了内置的 CorsConfigurationSource来完成 CORS规则的设置
  // 不过，通常情况下，会将 CORS交由 WebMvcConfigurer来完成
  // Security 负责决定是否拦截端口，而 MvcConfig 负责决定跨域如何放行
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("http://localhost:5173")); // 允许的前端域名
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
