package com.zephyr.train.business.service;

import com.zephyr.train.business.domain.ConfirmOrder;
import com.zephyr.train.business.domain.DailyTrainSeat;
import com.zephyr.train.business.domain.DailyTrainTicket;
import com.zephyr.train.business.enums.ConfirmOrderStatusEnum;
import com.zephyr.train.business.feign.MemberFeign;
import com.zephyr.train.business.mapper.ConfirmOrderMapper;
import com.zephyr.train.business.mapper.DailyTrainSeatMapper;
import com.zephyr.train.business.mapper.cust.DailyTrainTicketMapperCust;
import com.zephyr.train.business.req.ConfirmOrderTicketReq;
import com.zephyr.train.common.context.LoginMemberContext;
import com.zephyr.train.common.req.MemberTicketReq;
import com.zephyr.train.common.resp.CommonResp;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AfterConfirmOrderService {

  private static final Logger LOG = LoggerFactory.getLogger(AfterConfirmOrderService.class);

  @Resource
  private DailyTrainSeatMapper dailyTrainSeatMapper;

  @Resource
  private DailyTrainTicketMapperCust dailyTrainTicketMapperCust;

  @Resource
  private MemberFeign memberFeign;

  @Resource
  private ConfirmOrderMapper confirmOrderMapper;

  /**
   * After seats are selected, process the transaction:
   * - Update seat table sell status
   * - Update remaining tickets in the ticket-detail table
   * - Add a purchase record for the member
   * - Update the confirmation order status to "success"
   */
//  @Transactional
  @GlobalTransactional
  public void afterDoConfirm(DailyTrainTicket dailyTrainTicket, List<DailyTrainSeat> finalSeatList, List<ConfirmOrderTicketReq> tickets, ConfirmOrder confirmOrder) throws Exception {
    LOG.info("Seata global event ID: {}", RootContext.getXID());
    for (int j = 0; j < finalSeatList.size(); j++) {
      DailyTrainSeat dailyTrainSeat = finalSeatList.get(j);
      DailyTrainSeat seatForUpdate = new DailyTrainSeat();
      seatForUpdate.setId(dailyTrainSeat.getId());
      seatForUpdate.setSell(dailyTrainSeat.getSell());
      seatForUpdate.setUpdateTime(new Date());
      dailyTrainSeatMapper.updateByPrimaryKeySelective(seatForUpdate);

      // Calculate which stations' remaining tickets will be affected when the ticket of this station is sold
      // Affected remaining tickets: intervals that had no tickets sold before this selection and overlap with the purchase interval
      // Assume there are 10 stations; this purchase covers stations 4 through 7
      // Origin sell value:  001000001
      // Current sell value: 000011100
      // Final sell value:   001011101
      // Affect：            XXX11111X (find heading and tailing 1)
      // station interval: [start, end)
      // Integer startIndex = 4;
      // Integer endIndex = 7;
      // Integer minStartIndex = startIndex - the last 0 when searching backwards;
      // Integer maxStartIndex = endIndex - 1;
      // Integer minEndIndex = startIndex + 1;
      // Integer maxEndIndex = endIndex + the last 0 when searching forwards;
      Integer startIndex = dailyTrainTicket.getStartIndex();
      Integer endIndex = dailyTrainTicket.getEndIndex();
      char[] chars = seatForUpdate.getSell().toCharArray();
      Integer maxStartIndex = endIndex - 1;
      Integer minEndIndex = startIndex + 1;
      Integer minStartIndex = 0;
      for (int i = startIndex - 1; i >= 0; i--) {
        char aChar = chars[i];
        if (aChar == '1') {
          minStartIndex = i + 1;
          break;
        }
      }
      LOG.info("Affected departure station interval: " + minStartIndex + "-" + maxStartIndex);

      Integer maxEndIndex = seatForUpdate.getSell().length();
      for (int i = endIndex; i < seatForUpdate.getSell().length(); i++) {
        char aChar = chars[i];
        if (aChar == '1') {
          maxEndIndex = i;
          break;
        }
      }
      LOG.info("Affected arrival station interval: " + minEndIndex + "-" + maxEndIndex);

      dailyTrainTicketMapperCust.updateCountBySell(
          dailyTrainSeat.getDate(),
          dailyTrainSeat.getTrainCode(),
          dailyTrainSeat.getSeatType(),
          minStartIndex,
          maxStartIndex,
          minEndIndex,
          maxEndIndex
      );

      // Invoke member ticket interface, add the ticket for member
      MemberTicketReq memberTicketReq = new MemberTicketReq();
      memberTicketReq.setMemberId(LoginMemberContext.getId());
      memberTicketReq.setPassengerId(tickets.get(j).getPassengerId());
      memberTicketReq.setPassengerName(tickets.get(j).getPassengerName());
      memberTicketReq.setTrainDate(dailyTrainTicket.getDate());
      memberTicketReq.setTrainCode(dailyTrainTicket.getTrainCode());
      memberTicketReq.setCarriageIndex(dailyTrainSeat.getCarriageIndex());
      memberTicketReq.setSeatRow(dailyTrainSeat.getRow());
      memberTicketReq.setSeatCol(dailyTrainSeat.getCol());
      memberTicketReq.setStartStation(dailyTrainTicket.getStart());
      memberTicketReq.setStartPinyin(dailyTrainTicket.getStartPinyin());
      memberTicketReq.setStartTime(dailyTrainTicket.getStartTime());
      memberTicketReq.setEndStation(dailyTrainTicket.getEnd());
      memberTicketReq.setEndPinyin(dailyTrainTicket.getEndPinyin());
      memberTicketReq.setEndTime(dailyTrainTicket.getEndTime());
      memberTicketReq.setSeatType(dailyTrainSeat.getSeatType());
      CommonResp<Object> commonResp = memberFeign.save(memberTicketReq);
      LOG.info("Invoke member interface, return: {}", commonResp);

      // Update order status as success
      ConfirmOrder confirmOrderForUpdate = new ConfirmOrder();
      confirmOrderForUpdate.setId(confirmOrder.getId());
      confirmOrderForUpdate.setUpdateTime(new Date());
      confirmOrderForUpdate.setStatus(ConfirmOrderStatusEnum.SUCCESS.getCode());
      confirmOrderMapper.updateByPrimaryKeySelective(confirmOrderForUpdate);

      // Mock exception
//       Thread.sleep(10000);
//      if (1 == 1) {
//        throw new Exception("Mock exception");
//      }
    }
  }
}
