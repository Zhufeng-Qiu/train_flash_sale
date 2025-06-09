package com.zephyr.train.business.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.zephyr.train.business.enums.RedisKeyPreEnum;
import com.zephyr.train.business.mapper.ConfirmOrderMapper;
import com.zephyr.train.business.req.ConfirmOrderDoReq;
import com.zephyr.train.common.context.LoginMemberContext;
import com.zephyr.train.common.exception.BusinessException;
import com.zephyr.train.common.exception.BusinessExceptionEnum;
import jakarta.annotation.Resource;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class BeforeConfirmOrderService {

  private static final Logger LOG = LoggerFactory.getLogger(BeforeConfirmOrderService.class);

  @Resource
  private ConfirmOrderMapper confirmOrderMapper;

  @Resource
  private DailyTrainTicketService dailyTrainTicketService;

  @Resource
  private DailyTrainCarriageService dailyTrainCarriageService;

  @Resource
  private DailyTrainSeatService dailyTrainSeatService;

  @Resource
  private AfterConfirmOrderService afterConfirmOrderService;

  @Autowired
  private StringRedisTemplate redisTemplate;

  @Autowired
  private SkTokenService skTokenService;

  @SentinelResource(value = "beforeDoConfirm", blockHandler = "beforeDoConfirmBlock")
  public void beforeDoConfirm(ConfirmOrderDoReq req) {

    // Check remaining token
    boolean validSkToken = skTokenService.validSkToken(req.getDate(), req.getTrainCode(), LoginMemberContext.getId());
    if (validSkToken) {
      LOG.info("Validation of remaining token passed");
    } else {
      LOG.info("Validation of remaining token failed");
      throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_SK_TOKEN_FAIL);
    }

    // Purchase ticket lock
    String lockKey = RedisKeyPreEnum.CONFIRM_ORDER + "-" + DateUtil.formatDate(req.getDate()) + "-" + req.getTrainCode();
    // setIfAbsent corresponds to redis's setnx
    Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(lockKey, lockKey, 10, TimeUnit.SECONDS);
    if (Boolean.TRUE.equals(setIfAbsent)) {
      LOG.info("Congratulation, got the lock! lockKey：{}", lockKey);
    } else {
      // It just means the lock was not acquired, and do not know if tickets are sold out, so the prompt is "please try again shortly."
      LOG.info("Unfortunately, failed to acquire the lock! lockKey：{}", lockKey);
      throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_LOCK_FAIL);
    }

    // Ready to purchase ticket: TODO: send MQ, wait for purchasing ticket
    LOG.info("Ready to send MQ, wait for purchasing ticket");

  }

  /**
   * Flow limiting method, including all the parameters of rate limiting and BlockException
   * @param req
   * @param e
   */
  public void beforeDoConfirmBlock(ConfirmOrderDoReq req, BlockException e) {
    LOG.info("Ticket purchase requests are being flow limited: {}", req);
    throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION);
  }
}
