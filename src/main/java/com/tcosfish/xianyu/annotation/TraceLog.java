package com.tcosfish.xianyu.annotation;

import java.lang.annotation.*;

/**
 * @author tcosfish
 * @apiNote 配合 TraceLogAspect 使用
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TraceLog {
  /** 业务描述 */
  String desc() default "";
  /** 是否记录返回值 */
  boolean includeReturn() default true;
  /** 是否记录入参 */
  boolean includeParams() default true;
}
