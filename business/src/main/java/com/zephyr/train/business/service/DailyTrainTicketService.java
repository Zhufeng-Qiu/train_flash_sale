package com.zephyr.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zephyr.train.business.domain.DailyTrainTicket;
import com.zephyr.train.business.domain.DailyTrainTicketExample;
import com.zephyr.train.business.domain.TrainStation;
import com.zephyr.train.business.mapper.DailyTrainTicketMapper;
import com.zephyr.train.business.req.DailyTrainTicketQueryReq;
import com.zephyr.train.business.req.DailyTrainTicketSaveReq;
import com.zephyr.train.business.resp.DailyTrainTicketQueryResp;
import com.zephyr.train.common.resp.PageResp;
import com.zephyr.train.common.util.SnowUtil;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DailyTrainTicketService {

  private static final Logger LOG = LoggerFactory.getLogger(DailyTrainTicketService.class);

  @Resource
  private DailyTrainTicketMapper dailyTrainTicketMapper;

  @Resource
  private TrainStationService trainStationService;

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

  public PageResp<DailyTrainTicketQueryResp> queryList(DailyTrainTicketQueryReq req) {
    DailyTrainTicketExample dailyTrainTicketExample = new DailyTrainTicketExample();
    dailyTrainTicketExample.setOrderByClause("id desc");
    DailyTrainTicketExample.Criteria criteria = dailyTrainTicketExample.createCriteria();

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
  public void genDaily(Date date, String trainCode) {
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
      for (int j = (i + 1); j < stationList.size(); j++) {
        // Get arrival station
        TrainStation trainStationEnd = stationList.get(j);

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
        dailyTrainTicket.setYdz(0);
        dailyTrainTicket.setYdzPrice(BigDecimal.ZERO);
        dailyTrainTicket.setEdz(0);
        dailyTrainTicket.setEdzPrice(BigDecimal.ZERO);
        dailyTrainTicket.setRw(0);
        dailyTrainTicket.setRwPrice(BigDecimal.ZERO);
        dailyTrainTicket.setYw(0);
        dailyTrainTicket.setYwPrice(BigDecimal.ZERO);
        dailyTrainTicket.setCreateTime(now);
        dailyTrainTicket.setUpdateTime(now);
        dailyTrainTicketMapper.insert(dailyTrainTicket);
      }
    }

    LOG.info("Generate remaining ticket info of train[{}] for date[{}] completed", trainCode, DateUtil.formatDate(date));
  }
}
