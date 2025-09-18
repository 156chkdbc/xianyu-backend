package com.tcosfish.xianyu.scope;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * @author tcosfish
 * @apiNote 用于存放当前请求生命周期内的全局数据
 * Spring 容器会为 每一个 HTTP 请求 创建一个新的 Bean 实例，并且该实例 仅在该请求内有效，请求结束后，该 Bean 就会被销毁。
 */
@Component
@RequestScope
@Data
public class RequestScopeData {
  private String token;
  private Long userId;
  // 每次生成token都全局唯一, 用来精确到是哪一个token
  private String jti;
  private Long expiration;
  private boolean isLogin;
  // private boolean isAdmin;
}

