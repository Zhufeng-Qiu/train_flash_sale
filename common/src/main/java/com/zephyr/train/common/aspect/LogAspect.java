package com.zephyr.train.common.aspect;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.spring.PropertyPreFilters;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@Aspect
@Component
public class LogAspect {
  public LogAspect() {
    System.out.println("Common LogAspect");
  }

  private final static Logger LOG = LoggerFactory.getLogger(LogAspect.class);

  /**
   * Define the pointcut
   */
  @Pointcut("execution(public * com.zephyr..*Controller.*(..))")
  public void controllerPointcut() {
  }

  @Before("controllerPointcut()")
  public void doBefore(JoinPoint joinPoint) {

    // Add log ID
    MDC.put("LOG_ID", System.currentTimeMillis() + RandomUtil.randomString(3));

    // Start to print the request log
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = attributes.getRequest();
    Signature signature = joinPoint.getSignature();
    String name = signature.getName();

    // Print the request info
    LOG.info("------------- Start -------------");
    LOG.info("Request address: {} {}", request.getRequestURL().toString(), request.getMethod());
    LOG.info("Class and method: {}.{}", signature.getDeclaringTypeName(), name);
    LOG.info("Remote address: {}", request.getRemoteAddr());

    // Print the request arguments
    Object[] args = joinPoint.getArgs();
    // LOG.info("Request arguments: {}", JSONObject.toJSONString(args));

    // Exclude special types of arguments, such as file types.
    Object[] arguments = new Object[args.length];
    for (int i = 0; i < args.length; i++) {
      if (args[i] instanceof ServletRequest
          || args[i] instanceof ServletResponse
          || args[i] instanceof MultipartFile) {
        continue;
      }
      arguments[i] = args[i];
    }
    // Exclude fields that are sensitive or too long, such as ID numbers, phone numbers, email addresses, and passwords.
    String[] excludeProperties = {"mobile"};
    PropertyPreFilters filters = new PropertyPreFilters();
    PropertyPreFilters.MySimplePropertyPreFilter excludefilter = filters.addFilter();
    excludefilter.addExcludes(excludeProperties);
    LOG.info("Request arguments: {}", JSONObject.toJSONString(arguments, excludefilter));
  }

  @Around("controllerPointcut()")
  public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    long startTime = System.currentTimeMillis();
    Object result = proceedingJoinPoint.proceed();
    // Exclude fields that are sensitive or too long, such as ID card numbers, phone numbers, email addresses, and passwords.
    String[] excludeProperties = {"mobile"};
    PropertyPreFilters filters = new PropertyPreFilters();
    PropertyPreFilters.MySimplePropertyPreFilter excludefilter = filters.addFilter();
    excludefilter.addExcludes(excludeProperties);
    LOG.info("Response result: {}", JSONObject.toJSONString(result, excludefilter));
    LOG.info("------------- Finished Duration: {} ms -------------", System.currentTimeMillis() - startTime);
    return result;
  }

}
