package com.zephyr.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zephyr.train.business.domain.DailyTrain;
import com.zephyr.train.business.domain.DailyTrainTicket;
import com.zephyr.train.business.domain.DailyTrainTicketExample;
import com.zephyr.train.business.domain.TrainStation;
import com.zephyr.train.business.enums.SeatTypeEnum;
import com.zephyr.train.business.enums.TrainTypeEnum;
import com.zephyr.train.business.mapper.DailyTrainTicketMapper;
import com.zephyr.train.business.req.DailyTrainTicketQueryReq;
import com.zephyr.train.business.req.DailyTrainTicketSaveReq;
import com.zephyr.train.business.resp.DailyTrainTicketQueryResp;
import com.zephyr.train.common.resp.PageResp;
import com.zephyr.train.common.util.SnowUtil;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DailyTrainTicketService {

  private static final Logger LOG = LoggerFactory.getLogger(DailyTrainTicketService.class);

  @Resource
  private DailyTrainTicketMapper dailyTrainTicketMapper;

  @Resource
  private TrainStationService trainStationService;

  @Resource
  private DailyTrainSeatService dailyTrainSeatService;

  public void save(DailyTrainTicketSaveReq req) {
    DateTime now = DateTime.now();
    DailyTrainTicket dailyTrainTicket = BeanUtil.copyProperties(req, DailyTrainTicket.class);
    if (ObjectUtil.isNull(dailyTrainTicket.getId())) {
      dailyTrainTicket.setId(SnowUtil.getSnowflakeNextId());
      dailyTrainTicket.setCreateTime(now);
      dailyTrainTicket.setUpdateTime(now);
      dailyTrainTicketMapper.insert(dailyTrainTicket);
    } else {
      dailyTrainTicket.setUpdateTime(now);
      dailyTrainTicketMapper.updateByPrimaryKey(dailyTrainTicket);
    }
  }

  @Cacheable(value = "DailyTrainTicketService.queryList3")
  public PageResp<DailyTrainTicketQueryResp> queryList3(DailyTrainTicketQueryReq req) {
    LOG.info("Test cache penetration");
    return null;
  }

  @CachePut(value = "DailyTrainTicketService.queryList")
  public PageResp<DailyTrainTicketQueryResp> queryList2(DailyTrainTicketQueryReq req) {
    return queryList(req);
  }
//  @Cacheable(value="DailyTrainTicketService.queryList")
  public PageResp<DailyTrainTicketQueryResp> queryList(DailyTrainTicketQueryReq req) {
    // Common Cache Expire Strategy:
    // TTL: Time To Live
    // LRU: Least Recently Used
    // LFU: Least Frequently Used
    // FIFO: First In First Out
    // Random

    // Retrieving data from cache; if the database itself has no data, this leads to cache penetration.
    // if (Have data) { null []
    //     return
    // } else {
    //     Retrieve data from database
    // }

    DailyTrainTicketExample dailyTrainTicketExample = new DailyTrainTicketExample();
    dailyTrainTicketExample.setOrderByClause("id desc");
    DailyTrainTicketExample.Criteria criteria = dailyTrainTicketExample.createCriteria();
    if (ObjUtil.isNotNull(req.getDate())) {
      criteria.andDateEqualTo(req.getDate());
    }
    if (ObjUtil.isNotEmpty(req.getTrainCode())) {
      criteria.andTrainCodeEqualTo(req.getTrainCode());
    }
    if (ObjUtil.isNotEmpty(req.getStart())) {
      criteria.andStartEqualTo(req.getStart());
    }
    if (ObjUtil.isNotEmpty(req.getEnd())) {
      criteria.andEndEqualTo(req.getEnd());
    }

    LOG.info("Query page: {}", req.getPage());
    LOG.info("Query page size: {}", req.getSize());
    PageHelper.startPage(req.getPage(), req.getSize());
    List<DailyTrainTicket> dailyTrainTicketList = dailyTrainTicketMapper.selectByExample(dailyTrainTicketExample);

    PageInfo<DailyTrainTicket> pageInfo = new PageInfo<>(dailyTrainTicketList);
    LOG.info("Total number of records: {}", pageInfo.getTotal());
    LOG.info("Total number of pages: {}", pageInfo.getPages());

    List<DailyTrainTicketQueryResp> list = BeanUtil.copyToList(dailyTrainTicketList, DailyTrainTicketQueryResp.class);
    PageResp<DailyTrainTicketQueryResp> pageResp = new PageResp<>();
    pageResp.setTotal(pageInfo.getTotal());
    pageResp.setList(list);
    return pageResp;
  }

  public void delete(Long id) {
    dailyTrainTicketMapper.deleteByPrimaryKey(id);
  }

  @Transactional
  public void genDaily(DailyTrain dailyTrain, Date date, String trainCode) {
    LOG.info("Start to generate remaining ticket info of train[{}] for date[{}]", trainCode, DateUtil.formatDate(date));

    // Delete remaining ticket info for specific train and date
    DailyTrainTicketExample dailyTrainTicketExample = new DailyTrainTicketExample();
    dailyTrainTicketExample.createCriteria()
        .andDateEqualTo(date)
        .andTrainCodeEqualTo(trainCode);
    dailyTrainTicketMapper.deleteByExample(dailyTrainTicketExample);

    // Query station info for specific train
    List<TrainStation> stationList = trainStationService.selectByTrainCode(trainCode);
    if (CollUtil.isEmpty(stationList)) {
      LOG.info("No station basic data of this train found, generation of remaining ticket info terminates");
      return;
    }

    DateTime now = DateTime.now();
    for (int i = 0; i < stationList.size(); i++) {
      // Get departure station
      TrainStation trainStationStart = stationList.get(i);
      BigDecimal sumKM = BigDecimal.ZERO;
      for (int j = (i + 1); j < stationList.size(); j++) {
        // Get arrival station
        TrainStation trainStationEnd = stationList.get(j);
        sumKM = sumKM.add(trainStationEnd.getKm());

        DailyTrainTicket dailyTrainTicket = new DailyTrainTicket();
        dailyTrainTicket.setId(SnowUtil.getSnowflakeNextId());
        dailyTrainTicket.setDate(date);
        dailyTrainTicket.setTrainCode(trainCode);
        dailyTrainTicket.setStart(trainStationStart.getName());
        dailyTrainTicket.setStartPinyin(trainStationStart.getNamePinyin());
        dailyTrainTicket.setStartTime(trainStationStart.getOutTime());
        dailyTrainTicket.setStartIndex(trainStationStart.getIndex());
        dailyTrainTicket.setEnd(trainStationEnd.getName());
        dailyTrainTicket.setEndPinyin(trainStationEnd.getNamePinyin());
        dailyTrainTicket.setEndTime(trainStationEnd.getInTime());
        dailyTrainTicket.setEndIndex(trainStationEnd.getIndex());
        int ydz = dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.YDZ.getCode());
        int edz = dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.EDZ.getCode());
        int rw = dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.RW.getCode());
        int yw = dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.YW.getCode());
        // Price = sum(km) * Seat Unit Price * Train Type coefficient
        String trainType = dailyTrain.getType();
        // Calculate Train Type coefficient: TrainTypeEnum.priceRate
        BigDecimal priceRate = EnumUtil.getFieldBy(TrainTypeEnum::getPriceRate, TrainTypeEnum::getCode, trainType);
        BigDecimal ydzPrice = sumKM.multiply(SeatTypeEnum.YDZ.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal edzPrice = sumKM.multiply(SeatTypeEnum.EDZ.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal rwPrice = sumKM.multiply(SeatTypeEnum.RW.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal ywPrice = sumKM.multiply(SeatTypeEnum.YW.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
        dailyTrainTicket.setYdz(ydz);
        dailyTrainTicket.setYdzPrice(ydzPrice);
        dailyTrainTicket.setEdz(edz);
        dailyTrainTicket.setEdzPrice(edzPrice);
        dailyTrainTicket.setRw(rw);
        dailyTrainTicket.setRwPrice(rwPrice);
        dailyTrainTicket.setYw(yw);
        dailyTrainTicket.setYwPrice(ywPrice);
        dailyTrainTicket.setCreateTime(now);
        dailyTrainTicket.setUpdateTime(now);
        dailyTrainTicketMapper.insert(dailyTrainTicket);
      }
    }

    LOG.info("Generate remaining ticket info of train[{}] for date[{}] completed", trainCode, DateUtil.formatDate(date));
  }

  public DailyTrainTicket selectByUnique(Date date, String trainCode, String start, String end) {
    DailyTrainTicketExample dailyTrainTicketExample = new DailyTrainTicketExample();
    dailyTrainTicketExample.createCriteria()
        .andDateEqualTo(date)
        .andTrainCodeEqualTo(trainCode)
        .andStartEqualTo(start)
        .andEndEqualTo(end);
    List<DailyTrainTicket> list = dailyTrainTicketMapper.selectByExample(dailyTrainTicketExample);
    if (CollUtil.isNotEmpty(list)) {
      return list.get(0);
    } else {
      return null;
    }
  }
}
