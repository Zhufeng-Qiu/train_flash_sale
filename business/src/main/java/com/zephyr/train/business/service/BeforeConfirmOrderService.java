package com.zephyr.train.business.service;

import cn.hutool.core.date.DateTime;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.zephyr.train.business.domain.ConfirmOrder;
import com.zephyr.train.business.dto.ConfirmOrderMQDto;
import com.zephyr.train.business.enums.ConfirmOrderStatusEnum;
import com.zephyr.train.business.mapper.ConfirmOrderMapper;
import com.zephyr.train.business.req.ConfirmOrderDoReq;
import com.zephyr.train.business.req.ConfirmOrderTicketReq;
import com.zephyr.train.common.context.LoginMemberContext;
import com.zephyr.train.common.exception.BusinessException;
import com.zephyr.train.common.exception.BusinessExceptionEnum;
import com.zephyr.train.common.util.SnowUtil;
import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BeforeConfirmOrderService {

  private static final Logger LOG = LoggerFactory.getLogger(BeforeConfirmOrderService.class);

  @Autowired
  private SkTokenService skTokenService;

//  @Resource
//  public RocketMQTemplate rocketMQTemplate;

  @Resource
  private ConfirmOrderMapper confirmOrderMapper;

  @Resource
  private ConfirmOrderService confirmOrderService;

  @SentinelResource(value = "beforeDoConfirm", blockHandler = "beforeDoConfirmBlock")
  public Long beforeDoConfirm(ConfirmOrderDoReq req) {
    req.setMemberId(LoginMemberContext.getId());

    // Check remaining token
    boolean validSkToken = skTokenService.validSkToken(req.getDate(), req.getTrainCode(), LoginMemberContext.getId());
    if (validSkToken) {
      LOG.info("Validation of remaining token passed");
    } else {
      LOG.info("Validation of remaining token failed");
      throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_SK_TOKEN_FAIL);
    }

    Date date = req.getDate();
    String trainCode = req.getTrainCode();
    String start = req.getStart();
    String end = req.getEnd();
    List<ConfirmOrderTicketReq> tickets = req.getTickets();

    // Save the confirmation order record with initial status
    DateTime now = DateTime.now();
    ConfirmOrder confirmOrder = new ConfirmOrder();
    confirmOrder.setId(SnowUtil.getSnowflakeNextId());
    confirmOrder.setCreateTime(now);
    confirmOrder.setUpdateTime(now);
    confirmOrder.setMemberId(req.getMemberId());
    confirmOrder.setDate(date);
    confirmOrder.setTrainCode(trainCode);
    confirmOrder.setStart(start);
    confirmOrder.setEnd(end);
    confirmOrder.setDailyTrainTicketId(req.getDailyTrainTicketId());
    confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
    confirmOrder.setTickets(JSON.toJSONString(tickets));
    confirmOrderMapper.insert(confirmOrder);

    // Send MQ to queue up for purchasing
    ConfirmOrderMQDto confirmOrderMQDto = new ConfirmOrderMQDto();
    confirmOrderMQDto.setDate(req.getDate());
    confirmOrderMQDto.setTrainCode(req.getTrainCode());
    confirmOrderMQDto.setLogId(MDC.get("LOG_ID"));
    String reqJson = JSON.toJSONString(confirmOrderMQDto);
//    LOG.info("Queue up for purchasing ticket, sending MQ starts, message: {} ", reqJson);
//    rocketMQTemplate.convertAndSend(RocketMQTopicEnum.CONFIRM_ORDER.getCode(), reqJson);
//    LOG.info("Queue up for purchasing ticket, sending MQ ends");
    confirmOrderService.doConfirm(confirmOrderMQDto);
    return confirmOrder.getId();
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
