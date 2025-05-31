package com.zephyr.train.common.controller;

import cn.hutool.core.util.StrUtil;
import com.zephyr.train.common.exception.BusinessException;
import com.zephyr.train.common.resp.CommonResp;
import io.seata.core.context.RootContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Global exception handler, data pre-process
 */
@ControllerAdvice
public class ControllerExceptionHandler {
  Logger LOG = LoggerFactory.getLogger(ControllerAdvice.class);

  @ExceptionHandler(value = Exception.class)
  @ResponseBody
  public CommonResp handlerException(Exception e) throws Exception {
    LOG.info("Seata global event ID: {}", RootContext.getXID());
    // 如果是在一次全局事务里出异常了，就不要包装返回值，将异常抛给调用方，让调用方回滚事务
    if (StrUtil.isNotBlank(RootContext.getXID())) {
      throw e;
    }
    CommonResp commonResp = new CommonResp();
    LOG.error("System exception: ", e);
    commonResp.setSuccess(false);
    commonResp.setMessage("System error occurred. Please contact admin");
    return commonResp;
  }

  @ExceptionHandler(value = BusinessException.class)
  @ResponseBody
  public CommonResp handlerException(BusinessException e) throws Exception {
    CommonResp commonResp = new CommonResp();
    LOG.error("Business exception: {}", e.getE().getDesc());
    commonResp.setSuccess(false);
//    commonResp.setMessage("System error occurred. Please contact admin");
    commonResp.setMessage(e.getE().getDesc());
    return commonResp;
  }

  @ExceptionHandler(value = BindException.class)
  @ResponseBody
  public CommonResp handlerException(BindException e) throws Exception {
    CommonResp commonResp = new CommonResp();
    LOG.error("Validation exception: {}", e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    commonResp.setSuccess(false);
//    commonResp.setMessage("System error occurred. Please contact admin");
    commonResp.setMessage(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    return commonResp;
  }
}
