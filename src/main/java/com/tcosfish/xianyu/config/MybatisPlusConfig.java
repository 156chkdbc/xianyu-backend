package com.tcosfish.xianyu.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author tcosfish
 */
@Configuration
public class MybatisPlusConfig {
  @Bean
  public MybatisPlusInterceptor interceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    // 分页
    interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
    // 乐观锁
    interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
    // 防全表 delete/update
    interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
    return interceptor;
  }
}
