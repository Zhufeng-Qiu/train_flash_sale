package com.zephyr.train.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@SpringBootApplication
@ComponentScan("com.zephyr")
public class GatewayApplication {

  private static final Logger LOG = LoggerFactory.getLogger(GatewayApplication.class);

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(GatewayApplication.class);
    Environment env = app.run(args).getEnvironment();
    LOG.info("Launch successfully!");
    LOG.info("Gateway Address: \thttp://127.0.0.1:{}/hello", env.getProperty("server.port"));
  }
}
