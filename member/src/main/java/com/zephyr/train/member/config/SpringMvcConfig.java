package com.zephyr.train.member.config;

import com.zephyr.train.common.interceptor.LogInterceptor;
import com.zephyr.train.common.interceptor.MemberInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

  @Resource
  MemberInterceptor memberInterceptor;

  @Resource
  LogInterceptor logInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(logInterceptor);

    registry.addInterceptor(memberInterceptor)
        .addPathPatterns("/**")
        .excludePathPatterns(
            "/member/hello",
            "/member/member/send-code",
            "/member/member/login"
        );
  }
}