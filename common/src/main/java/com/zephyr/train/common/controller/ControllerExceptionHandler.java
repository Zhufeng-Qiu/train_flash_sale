package com.zephyr.train.common.controller;

import com.zephyr.train.common.exception.BusinessException;
import com.zephyr.train.common.resp.CommonResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Global exception handler, data pre-process
 */
@ControllerAdvice
public class ControllerExceptionHandler {
  Logger log = LoggerFactory.getLogger(ControllerAdvice.class);

  @ExceptionHandler(value = Exception.class)
  @ResponseBody
  public CommonResp handlerException(Exception e) throws Exception {
    CommonResp commonResp = new CommonResp();
    log.error("System exception: ", e);
    commonResp.setSuccess(false);
    commonResp.setMessage("System error occurred. Please contact admin");
    return commonResp;
  }

  @ExceptionHandler(value = BusinessException.class)
  @ResponseBody
  public CommonResp handlerException(BusinessException e) throws Exception {
    CommonResp commonResp = new CommonResp();
    log.error("Business exception: {}", e.getE().getDesc());
    commonResp.setSuccess(false);
//    commonResp.setMessage("System error occurred. Please contact admin");
    commonResp.setMessage(e.getE().getDesc());
    return commonResp;
  }
}
