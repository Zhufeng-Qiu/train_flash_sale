package com.zephyr.train.gateway.config;

import com.zephyr.train.gateway.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class LoginMemberFilter implements Ordered, GlobalFilter {

  private static final Logger LOG = LoggerFactory.getLogger(LoginMemberFilter.class);

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    String path = exchange.getRequest().getURI().getPath();

    // Exclude requests that do not need to be intercepted
    if (path.contains("/admin")
        || path.contains("/redis")
        || path.contains("/hello")
        || path.contains("/member/member/login")
        || path.contains("/member/member/send-code")
        || path.contains("/business/kaptcha")) {
      LOG.info("No need to login verification: {}", path);
      return chain.filter(exchange);
    } else {
      LOG.info("Need to login verification: {}", path);
    }
    // 获取header的token参数
    String token = exchange.getRequest().getHeaders().getFirst("token");
    LOG.info("Start to member login verification，token：{}", token);
    if (token == null || token.isEmpty()) {
      LOG.info( "token is null, request is intercepted" );
      exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
      return exchange.getResponse().setComplete();
    }

    // 校验token是否有效，包括token是否被改过，是否过期
    boolean validate = JwtUtil.validate(token);
    if (validate) {
      LOG.info("token is valid，approve the request");
      return chain.filter(exchange);
    } else {
      LOG.warn( "token is invalid, request is intercepted" );
      exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
      return exchange.getResponse().setComplete();
    }

  }

  /**
   * Order setting: less value, higher priority
   *
   * @return
   */
  @Override
  public int getOrder() {
    return 0;
  }
}