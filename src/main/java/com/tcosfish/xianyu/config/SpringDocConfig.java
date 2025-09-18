package com.tcosfish.xianyu.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author tcosfish
 */
@Configuration
public class SpringDocConfig {

  @Bean
  public OpenAPI customOpenApi() {
    return new OpenAPI()
      .openapi("3.1.0")          // 必须有
      .info(new Info()
        .title("我的API文档")
        .description("Spring Boot 3 应用接口文档")
        .version("v1.0.0"))
      .externalDocs(new ExternalDocumentation()
        .description("更多文档")
        .url("https://springdoc.org"));
  }
}