package com.zephyr.train.business.config;


import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@SpringBootApplication
@ComponentScan("com.zephyr")
@MapperScan("com.zephyr.train.*.mapper")
@EnableFeignClients("com.zephyr.train.business.feign")
@EnableCaching
public class BusinessApplication {

  private static final Logger LOG = LoggerFactory.getLogger(BusinessApplication.class);

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(BusinessApplication.class);
    Environment env = app.run(args).getEnvironment();
    LOG.info("Launch successfully!");
    LOG.info("Test Address: \thttp://127.0.0.1:{}{}/hello", env.getProperty("server.port"), env.getProperty("server.servlet.context-path"));
  }
}
