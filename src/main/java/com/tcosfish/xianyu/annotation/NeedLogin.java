package com.tcosfish.xianyu.annotation;

import java.lang.annotation.*;

/**
 * @author tcosfish
 * @apiNote 配合 NeedLoginAspect使用
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NeedLogin {
}
