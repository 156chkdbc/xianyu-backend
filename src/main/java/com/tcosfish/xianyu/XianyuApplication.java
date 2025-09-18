package com.tcosfish.xianyu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author tcosfish
 */
@SpringBootApplication
@MapperScan("com.tcosfish.xianyu.mapper") // Mybatis
@EnableScheduling
@EnableAsync
public class XianyuApplication {
  public static void main(String[] args) {
    SpringApplication.run(XianyuApplication.class, args);
  }

}
