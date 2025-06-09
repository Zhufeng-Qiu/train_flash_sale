package com.zephyr.train.business.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.zephyr.train.business.req.ConfirmOrderDoReq;
import com.zephyr.train.business.service.BeforeConfirmOrderService;
import com.zephyr.train.common.exception.BusinessExceptionEnum;
import com.zephyr.train.common.resp.CommonResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/confirm-order")
public class ConfirmOrderController {

  private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderController.class);

  @Resource
  private BeforeConfirmOrderService beforeConfirmOrderService;

  @Autowired
  private StringRedisTemplate redisTemplate;

  @Value("${spring.profiles.active}")
  private String env;

  // Do not use interface path as resource name; otherwise, after flow limiting, the fallback method will not be invoked.
  @SentinelResource(value = "confirmOrderDo", blockHandler = "doConfirmBlock")
  @PostMapping("/do")
  public CommonResp<Object> doConfirm(@Valid @RequestBody ConfirmOrderDoReq req) {
    if (!env.equals("dev")) {
      // CAPTCHA validation
      String imageCodeToken = req.getImageCodeToken();
      String imageCode = req.getImageCode();
      String imageCodeRedis = redisTemplate.opsForValue().get(imageCodeToken);
      LOG.info("Get CAPTCHA from redis: {}", imageCodeRedis);
      if (ObjectUtils.isEmpty(imageCodeRedis)) {
        return new CommonResp<>(false, "CAPTCHA expired", null);
      }
      // Ignore case when validating CAPTCHA to improve the user experience — for example, characters like O/o, V/v, and W/w are easily confused.
      if (!imageCodeRedis.equalsIgnoreCase(imageCode)) {
        return new CommonResp<>(false, "CAPTCHA does not match", null);
      } else {
        // Remove CAPTCHA after validation
        redisTemplate.delete(imageCodeToken);
      }
    }

    beforeConfirmOrderService.beforeDoConfirm(req);
    return new CommonResp<>();
  }

  /**
   * Rate limiting method, including all the parameters of rate limiting and BlockException
   * @param req
   * @param e
   */
  public CommonResp<Object> doConfirmBlock(ConfirmOrderDoReq req, BlockException e) {
    LOG.info("ConfirmOrderController购票请求被限流：{}", req);
    // throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION);
    CommonResp<Object> commonResp = new CommonResp<>();
    commonResp.setSuccess(false);
    commonResp.setMessage(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION.getDesc());
    return commonResp;

  }
}
