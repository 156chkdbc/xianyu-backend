package com.tcosfish.xianyu.config;

import com.tcosfish.xianyu.filter.TraceIdFilter;
import com.tcosfish.xianyu.interceptor.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author tcosfish
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  @Value("${upload.path:E:/graduationProject/xianyu/uploads}")
  private String uploadPath;

  private final TokenInterceptor tokenInterceptor;

  @Autowired
  public WebMvcConfig(TokenInterceptor tokenInterceptor) {
    this.tokenInterceptor = tokenInterceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    // 添加拦截器, 除了登录, 错误, 都要进行token处理
    registry.addInterceptor(tokenInterceptor)
      .addPathPatterns("/**")
      .excludePathPatterns("/login", "/error");
  }

  // 添加一个资源处理程序
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // 静态资源映射, 将 http://localhost:8848/images/** 映射到存放上传文件的地方
    registry.addResourceHandler("/images/**")
      .addResourceLocations("file:"+uploadPath+"/");
  }

  @Bean
  public FilterRegistrationBean<TraceIdFilter> traceIdFilter() {
    // 初始化的时候使用, 注册 TraceIdFilter拦截每一个请求
    FilterRegistrationBean<TraceIdFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(new TraceIdFilter());
    registrationBean.addUrlPatterns("/*");
    return registrationBean;
  }
}
