package com.zephyr.train.business.service;

import com.zephyr.train.business.domain.DailyTrainSeat;
import com.zephyr.train.business.mapper.DailyTrainSeatMapper;
import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AfterConfirmOrderService {

  private static final Logger LOG = LoggerFactory.getLogger(AfterConfirmOrderService.class);

  @Resource
  private DailyTrainSeatMapper dailyTrainSeatMapper;

  /**
   * After seats are selected, process the transaction:
   * - Update seat table sell status
   * - Update remaining tickets in the ticket-detail table
   * - Add a purchase record for the member
   * - Update the confirmation order status to "success"
   */
  @Transactional
  public void afterDoConfirm(List<DailyTrainSeat> finalSeatList) {
    for (DailyTrainSeat dailyTrainSeat : finalSeatList) {
      DailyTrainSeat seatForUpdate = new DailyTrainSeat();
      seatForUpdate.setId(dailyTrainSeat.getId());
      seatForUpdate.setSell(dailyTrainSeat.getSell());
      seatForUpdate.setUpdateTime(new Date());
      dailyTrainSeatMapper.updateByPrimaryKeySelective(seatForUpdate);
    }
  }
}
