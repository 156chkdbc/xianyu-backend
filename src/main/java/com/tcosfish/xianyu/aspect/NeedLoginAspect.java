package com.tcosfish.xianyu.aspect;

import com.tcosfish.xianyu.annotation.NeedLogin;
import com.tcosfish.xianyu.scope.RequestScopeData;
import com.tcosfish.xianyu.utils.ApiResponseUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author tcosfish
 * @apiNote NeedLogin周围的切面逻辑, 切面相当于洋葱的每一层皮, 将流程细分成不同的处理环节
 */
@Aspect
@Component
public class NeedLoginAspect {
  private final RequestScopeData requestScopeData;

  @Autowired
  public NeedLoginAspect(RequestScopeData requestScopeData) {
    this.requestScopeData = requestScopeData;
  }

  // 拦截所有被 @needLogin 注解标记的方法，并在方法执行前后执行你定义的环绕逻辑。
  @Around("@annotation(needLogin)")
  public Object around(ProceedingJoinPoint joinPoint, NeedLogin needLogin) throws Throwable {

    if(!requestScopeData.isLogin()) {
      return ApiResponseUtil.error("用户未登录, 或token已过期");
    }

    if(requestScopeData.getUserId() == null) {
      return ApiResponseUtil.error("用户ID异常");
    }

    return joinPoint.proceed();
  }
}
