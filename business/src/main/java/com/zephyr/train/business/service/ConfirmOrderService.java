package com.zephyr.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zephyr.train.business.domain.ConfirmOrder;
import com.zephyr.train.business.domain.ConfirmOrderExample;
import com.zephyr.train.business.domain.DailyTrainTicket;
import com.zephyr.train.business.enums.ConfirmOrderStatusEnum;
import com.zephyr.train.business.enums.SeatTypeEnum;
import com.zephyr.train.business.mapper.ConfirmOrderMapper;
import com.zephyr.train.business.req.ConfirmOrderDoReq;
import com.zephyr.train.business.req.ConfirmOrderQueryReq;
import com.zephyr.train.business.req.ConfirmOrderTicketReq;
import com.zephyr.train.business.resp.ConfirmOrderQueryResp;
import com.zephyr.train.common.context.LoginMemberContext;
import com.zephyr.train.common.exception.BusinessException;
import com.zephyr.train.common.exception.BusinessExceptionEnum;
import com.zephyr.train.common.resp.PageResp;
import com.zephyr.train.common.util.SnowUtil;
import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ConfirmOrderService {

  private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderService.class);

  @Resource
  private ConfirmOrderMapper confirmOrderMapper;

  @Resource
  private DailyTrainTicketService dailyTrainTicketService;

  public void save(ConfirmOrderDoReq req) {
    DateTime now = DateTime.now();
    ConfirmOrder confirmOrder = BeanUtil.copyProperties(req, ConfirmOrder.class);
    if (ObjectUtil.isNull(confirmOrder.getId())) {
      confirmOrder.setId(SnowUtil.getSnowflakeNextId());
      confirmOrder.setCreateTime(now);
      confirmOrder.setUpdateTime(now);
      confirmOrderMapper.insert(confirmOrder);
    } else {
      confirmOrder.setUpdateTime(now);
      confirmOrderMapper.updateByPrimaryKey(confirmOrder);
    }
  }

  public PageResp<ConfirmOrderQueryResp> queryList(ConfirmOrderQueryReq req) {
    ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
    confirmOrderExample.setOrderByClause("id desc");
    ConfirmOrderExample.Criteria criteria = confirmOrderExample.createCriteria();

    LOG.info("Query page: {}", req.getPage());
    LOG.info("Query page size: {}", req.getSize());
    PageHelper.startPage(req.getPage(), req.getSize());
    List<ConfirmOrder> confirmOrderList = confirmOrderMapper.selectByExample(confirmOrderExample);

    PageInfo<ConfirmOrder> pageInfo = new PageInfo<>(confirmOrderList);
    LOG.info("Total number of records: {}", pageInfo.getTotal());
    LOG.info("Total number of pages: {}", pageInfo.getPages());

    List<ConfirmOrderQueryResp> list = BeanUtil.copyToList(confirmOrderList, ConfirmOrderQueryResp.class);
    PageResp<ConfirmOrderQueryResp> pageResp = new PageResp<>();
    pageResp.setTotal(pageInfo.getTotal());
    pageResp.setList(list);
    return pageResp;
  }

  public void delete(Long id) {
    confirmOrderMapper.deleteByPrimaryKey(id);
  }

  public void doConfirm(ConfirmOrderDoReq req) {
    // Business data validation omitted, e.g.: verifying train existence, ticket availability, train within valid period, tickets.length > 0, and preventing the same passenger from buying on the same train twice TO-DO

    Date date = req.getDate();
    String trainCode = req.getTrainCode();
    String start = req.getStart();
    String end = req.getEnd();

    // Save the confirmation order record with initial status
    DateTime now = DateTime.now();
    ConfirmOrder confirmOrder = new ConfirmOrder();
    confirmOrder.setId(SnowUtil.getSnowflakeNextId());
    confirmOrder.setCreateTime(now);
    confirmOrder.setUpdateTime(now);
    confirmOrder.setMemberId(LoginMemberContext.getId());
    confirmOrder.setDate(date);
    confirmOrder.setTrainCode(trainCode);
    confirmOrder.setStart(start);
    confirmOrder.setEnd(end);
    confirmOrder.setDailyTrainTicketId(req.getDailyTrainTicketId());
    confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
    confirmOrder.setTickets(JSON.toJSONString(req.getTickets()));
    confirmOrderMapper.insert(confirmOrder);

    // Retrieve the remaining-ticket record to get the actual inventory
    DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(date, trainCode, start, end);
    LOG.info("Retrieve remaining tickets: {}", dailyTrainTicket);

    // Decrement the remaining-ticket count and verify availability
    reduceTickets(req, dailyTrainTicket);

    // Seat selection
    // - Fetch seat data one carriage at a time
    // - Select seats that meet the criteria; if a carriage doesn’t suffice, move to the next one (all selected seats must be in the same carriage)

    // After seats are selected, process the transaction:
    // - Update seat table sell status
    // - Update remaining tickets in the ticket-detail table
    // - Add a purchase record for the member
    // - Update the confirmation order status to "success"
  }

  private static void reduceTickets(ConfirmOrderDoReq req, DailyTrainTicket dailyTrainTicket) {
    for (ConfirmOrderTicketReq ticketReq : req.getTickets()) {
      String seatTypeCode = ticketReq.getSeatTypeCode();
      SeatTypeEnum seatTypeEnum = EnumUtil.getBy(SeatTypeEnum::getCode, seatTypeCode);
      switch (seatTypeEnum) {
        case YDZ -> {
          int countLeft = dailyTrainTicket.getYdz() - 1;
          if (countLeft < 0) {
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
          }
          dailyTrainTicket.setYdz(countLeft);
        }
        case EDZ -> {
          int countLeft = dailyTrainTicket.getEdz() - 1;
          if (countLeft < 0) {
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
          }
          dailyTrainTicket.setEdz(countLeft);
        }
        case RW -> {
          int countLeft = dailyTrainTicket.getRw() - 1;
          if (countLeft < 0) {
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
          }
          dailyTrainTicket.setRw(countLeft);
        }
        case YW -> {
          int countLeft = dailyTrainTicket.getYw() - 1;
          if (countLeft < 0) {
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
          }
          dailyTrainTicket.setYw(countLeft);
        }
      }
    }
  }
}
