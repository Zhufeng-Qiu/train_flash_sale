package com.zephyr.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zephyr.train.business.domain.ConfirmOrder;
import com.zephyr.train.business.domain.ConfirmOrderExample;
import com.zephyr.train.business.domain.DailyTrainCarriage;
import com.zephyr.train.business.domain.DailyTrainSeat;
import com.zephyr.train.business.domain.DailyTrainTicket;
import com.zephyr.train.business.enums.ConfirmOrderStatusEnum;
import com.zephyr.train.business.enums.SeatColEnum;
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
import java.util.ArrayList;
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

  @Resource
  private DailyTrainCarriageService dailyTrainCarriageService;

  @Resource
  private DailyTrainSeatService dailyTrainSeatService;

  @Resource
  private AfterConfirmOrderService afterConfirmOrderService;

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
    List<ConfirmOrderTicketReq> tickets = req.getTickets();

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
    confirmOrder.setTickets(JSON.toJSONString(tickets));
    confirmOrderMapper.insert(confirmOrder);

    // Retrieve the remaining-ticket record to get the actual inventory
    DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(date, trainCode, start, end);
    LOG.info("Retrieve remaining tickets: {}", dailyTrainTicket);

    // Decrement the remaining-ticket count and verify availability
    reduceTickets(req, dailyTrainTicket);

    // Final seat list
    List<DailyTrainSeat> finalSeatList = new ArrayList<>();
    // Calculate offsets relative to the first seat
    // For example, if the selected seats are C1 and D2 (ACDF), the offsets are: [0, 5]
    // For example, if the selected seats are A1, B1, and C1 (ABCDF), the offsets are: [0, 1, 2]
    ConfirmOrderTicketReq ticketReq0 = tickets.get(0);
    if(StrUtil.isNotBlank(ticketReq0.getSeat())) {
      LOG.info("This order requires seat selection");
      // Determine which columns are included in the seat type for this selection, to calculate each selected seat’s offset relative to the first seat
      List<SeatColEnum> colEnumList = SeatColEnum.getColsByType(ticketReq0.getSeatTypeCode());
      LOG.info("Seat Columns included in seat type for this order: {}", colEnumList);

      // Build a reference seat list matching the two-row seat-selection layout on the front end, e.g. referSeatList = {A1, C1, D1, F1, A2, C2, D2, F2}
      List<String> referSeatList = new ArrayList<>();
      for (int i = 1; i <= 2; i++) {
        for (SeatColEnum seatColEnum : colEnumList) {
          referSeatList.add(seatColEnum.getCode() + i);
        }
      }
      LOG.info("Reference two-row seat list: {}", referSeatList);

      List<Integer> offsetList = new ArrayList<>();
      // Absolute offset value, i.e., the position within the reference seat list
      List<Integer> aboluteOffsetList = new ArrayList<>();
      for (ConfirmOrderTicketReq ticketReq : tickets) {
        int index = referSeatList.indexOf(ticketReq.getSeat());
        aboluteOffsetList.add(index);
      }
      LOG.info("Absolute offset value for all seats: {}", aboluteOffsetList);
      for (Integer index : aboluteOffsetList) {
        int offset = index - aboluteOffsetList.get(0);
        offsetList.add(offset);
      }
      LOG.info("Calculate the relative offset values of all seats relative to the first seat: {}", offsetList);

      getSeat(
          finalSeatList,
          date,
          trainCode,
          ticketReq0.getSeatTypeCode(),
          ticketReq0.getSeat().split("")[0], // A1 -> A
          offsetList,
          dailyTrainTicket.getStartIndex(),
          dailyTrainTicket.getEndIndex()
      );

    } else {
      LOG.info("This order does not require seat selection");

      for (ConfirmOrderTicketReq ticketReq : tickets) {
        getSeat(
            finalSeatList,
            date,
            trainCode,
            ticketReq.getSeatTypeCode(),
            null,
            null,
            dailyTrainTicket.getStartIndex(),
            dailyTrainTicket.getEndIndex()
        );
      }
    }

    LOG.info("Final seat list: {}", finalSeatList);

    // After seats are selected, process the transaction:
    // - Update seat table sell status
    // - Update remaining tickets in the ticket-detail table
    // - Add a purchase record for the member
    // - Update the confirmation order status to "success"
    afterConfirmOrderService.afterDoConfirm(finalSeatList);


  }

  private void getSeat(List<DailyTrainSeat> finalSeatList, Date date, String trainCode, String seatType, String column, List<Integer> offsetList, Integer startIndex, Integer endIndex) {
    List<DailyTrainSeat> getSeatList = new ArrayList<>();
    List<DailyTrainCarriage> carriageList = dailyTrainCarriageService.selectBySeatType(date, trainCode, seatType);
    LOG.info("Find {} matching carriages", carriageList.size());

    // Retrieve seat for each carriage
    for (DailyTrainCarriage dailyTrainCarriage : carriageList) {
      LOG.info("Start to select seats from carriage {}", dailyTrainCarriage.getIndex());
      getSeatList = new ArrayList<>();
      List<DailyTrainSeat> seatList = dailyTrainSeatService.selectByCarriage(date, trainCode, dailyTrainCarriage.getIndex());
      LOG.info("Seat number of carriage {} : {}", dailyTrainCarriage.getIndex(), seatList.size());

      for (int i = 0; i < seatList.size(); i++) {
        DailyTrainSeat dailyTrainSeat = seatList.get(i);
        Integer seatIndex = dailyTrainSeat.getCarriageSeatIndex();
        String col = dailyTrainSeat.getCol();

        // Check if current seat has not been selected
        boolean alreadyChooseFlag = false;
        for (DailyTrainSeat finalSeat : finalSeatList){
          if (finalSeat.getId().equals(dailyTrainSeat.getId())) {
            alreadyChooseFlag = true;
            break;
          }
        }
        if (alreadyChooseFlag) {
          LOG.info("Seat {} has been selected, cannot be selected repeatedly, continue to check next seat", seatIndex);
          continue;
        }

        // Check column, if not null, then check column value
        if (StrUtil.isBlank(column)) {
          LOG.info("No Seat selection");
        } else {
          if (!column.equals(col)) {
            LOG.info("The column value of Seat {} does not match, continue to next seat, current column: {}, target column: {}", seatIndex, col, column);
            continue;
          }
        }

        boolean isChoose = calSell(dailyTrainSeat, startIndex, endIndex);
        if (isChoose) {
          LOG.info("Select seat");
          getSeatList.add(dailyTrainSeat);
        } else {
          continue;
        }

        // Select the remaining seats based on offset
        boolean isGetAllOffsetSeat = true;
        if (CollUtil.isNotEmpty(offsetList)) {
          LOG.info("Offset:{}, check if the seats in offset can be selected", offsetList);
          // Start from index 1, index 0 is the current selected seat
          for (int j = 1; j < offsetList.size(); j++) {
            Integer offset = offsetList.get(j);
            // Seat index starts from 1
            // int nextIndex = seatIndex + offset - 1;
            int nextIndex = i + offset;

            // Selected seats must be in the same carriage
            if (nextIndex >= seatList.size()) {
              LOG.info("Seat {} cannot be selected, index after offset is out of this carriage", nextIndex);
              isGetAllOffsetSeat = false;
              break;
            }

            DailyTrainSeat nextDailyTrainSeat = seatList.get(nextIndex);
            boolean isChooseNext = calSell(nextDailyTrainSeat, startIndex, endIndex);
            if (isChooseNext) {
              LOG.info("Seat {} is selected", nextDailyTrainSeat.getCarriageSeatIndex());
              getSeatList.add(nextDailyTrainSeat);
            } else {
              LOG.info("Seat {} cannot be selected", nextDailyTrainSeat.getCarriageSeatIndex());
              isGetAllOffsetSeat = false;
              break;
            }
          }
        }
        if (!isGetAllOffsetSeat) {
          getSeatList = new ArrayList<>();
          continue;
        }

        // Store selected seats
        finalSeatList.addAll(getSeatList);
        return;
      }
    }
  }

  /**
   * Check seat is sellable in given station range
   * For example: sell=10001，0-1 has sold, 4-5 has sold
   * All 0 means sellable in this range; as long as there is a 1, it is not sellable
   *
   * For the sell value after purchased, origin value is 10001, station range is 1-4
   * get sale status for current purchase 01110, then bitwise-AND it with origin value, the sell value becomes 11111
   */
  private boolean calSell(DailyTrainSeat dailyTrainSeat, Integer startIndex, Integer endIndex) {
    // 10001, 00001
    String sell = dailyTrainSeat.getSell();
    //  000, 000
    String sellPart = sell.substring(startIndex, endIndex);
    if (Integer.parseInt(sellPart) > 0) {
      LOG.info("Seat {} in the station range {}~{} has been sold, cannot be selected", dailyTrainSeat.getCarriageSeatIndex(), startIndex, endIndex);
      return false;
    } else {
      LOG.info("Seat {} in the station range {}~{} has not been sold, can be selected", dailyTrainSeat.getCarriageSeatIndex(), startIndex, endIndex);
      //  111, 111
      String curSell = sellPart.replace('0', '1');
      // 0111, 0111
      curSell = StrUtil.fillBefore(curSell, '0', endIndex);
      // 01110, 01110
      curSell = StrUtil.fillAfter(curSell, '0', sell.length());

      // bitwise-AND
      // 11111, 01111
      int newSellInt = NumberUtil.binaryToInt(curSell) | NumberUtil.binaryToInt(sell);
      //  11111,  1111
      String newSell = NumberUtil.getBinaryStr(newSellInt);
      // 11111, 01111
      newSell = StrUtil.fillBefore(newSell, '0', sell.length());
      LOG.info("Seat {} has been selected, origin sell value: {}, station range: {}~{}, current sell value: {}, final sell value: {}"
          , dailyTrainSeat.getCarriageSeatIndex(), sell, startIndex, endIndex, curSell, newSell);
      dailyTrainSeat.setSell(newSell);
      return true;

    }
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
