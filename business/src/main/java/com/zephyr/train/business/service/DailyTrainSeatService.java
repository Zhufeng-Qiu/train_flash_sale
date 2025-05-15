package com.zephyr.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zephyr.train.business.domain.DailyTrainSeat;
import com.zephyr.train.business.domain.DailyTrainSeatExample;
import com.zephyr.train.business.domain.TrainSeat;
import com.zephyr.train.business.domain.TrainStation;
import com.zephyr.train.business.mapper.DailyTrainSeatMapper;
import com.zephyr.train.business.req.DailyTrainSeatQueryReq;
import com.zephyr.train.business.req.DailyTrainSeatSaveReq;
import com.zephyr.train.business.resp.DailyTrainSeatQueryResp;
import com.zephyr.train.common.resp.PageResp;
import com.zephyr.train.common.util.SnowUtil;
import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DailyTrainSeatService {

  private static final Logger LOG = LoggerFactory.getLogger(DailyTrainSeatService.class);

  @Resource
  private DailyTrainSeatMapper dailyTrainSeatMapper;

  @Resource
  private TrainSeatService trainSeatService;

  @Resource
  private TrainStationService trainStationService;


  public void save(DailyTrainSeatSaveReq req) {
    DateTime now = DateTime.now();
    DailyTrainSeat dailyTrainSeat = BeanUtil.copyProperties(req, DailyTrainSeat.class);
    if (ObjectUtil.isNull(dailyTrainSeat.getId())) {
      dailyTrainSeat.setId(SnowUtil.getSnowflakeNextId());
      dailyTrainSeat.setCreateTime(now);
      dailyTrainSeat.setUpdateTime(now);
      dailyTrainSeatMapper.insert(dailyTrainSeat);
    } else {
      dailyTrainSeat.setUpdateTime(now);
      dailyTrainSeatMapper.updateByPrimaryKey(dailyTrainSeat);
    }
  }

  public PageResp<DailyTrainSeatQueryResp> queryList(DailyTrainSeatQueryReq req) {
    DailyTrainSeatExample dailyTrainSeatExample = new DailyTrainSeatExample();
    dailyTrainSeatExample.setOrderByClause("date desc, train_code asc, carriage_index asc, carriage_seat_index asc");
    DailyTrainSeatExample.Criteria criteria = dailyTrainSeatExample.createCriteria();
    if (ObjectUtil.isNotNull(req.getDate())) {
      criteria.andDateEqualTo(req.getDate());
    }
    if (ObjectUtil.isNotEmpty(req.getTrainCode())) {
      criteria.andTrainCodeEqualTo(req.getTrainCode());
    }

    LOG.info("Query page: {}", req.getPage());
    LOG.info("Query page size: {}", req.getSize());
    PageHelper.startPage(req.getPage(), req.getSize());
    List<DailyTrainSeat> dailyTrainSeatList = dailyTrainSeatMapper.selectByExample(dailyTrainSeatExample);

    PageInfo<DailyTrainSeat> pageInfo = new PageInfo<>(dailyTrainSeatList);
    LOG.info("Total number of records: {}", pageInfo.getTotal());
    LOG.info("Total number of pages: {}", pageInfo.getPages());

    List<DailyTrainSeatQueryResp> list = BeanUtil.copyToList(dailyTrainSeatList, DailyTrainSeatQueryResp.class);
    PageResp<DailyTrainSeatQueryResp> pageResp = new PageResp<>();
    pageResp.setTotal(pageInfo.getTotal());
    pageResp.setList(list);
    return pageResp;
  }

  public void delete(Long id) {
    dailyTrainSeatMapper.deleteByPrimaryKey(id);
  }

  @Transactional
  public void genDaily(Date date, String trainCode) {
    LOG.info("Start to generate seat info of train[{}] for date[{}]", trainCode, DateUtil.formatDate(date));

    // Delete seat info for specific train and date
    DailyTrainSeatExample dailyTrainSeatExample = new DailyTrainSeatExample();
    dailyTrainSeatExample.createCriteria()
        .andDateEqualTo(date)
        .andTrainCodeEqualTo(trainCode);
    dailyTrainSeatMapper.deleteByExample(dailyTrainSeatExample);

    List<TrainStation> stationList = trainStationService.selectByTrainCode(trainCode);
    String sell = StrUtil.fillBefore("", '0', stationList.size() - 1);

    // Query seat info for specific train
    List<TrainSeat> seatList = trainSeatService.selectByTrainCode(trainCode);
    if (CollUtil.isEmpty(seatList)) {
      LOG.info("No seat basic data of this train found, generation of seat info terminates");
      return;
    }

    for (TrainSeat trainSeat : seatList) {
      DateTime now = DateTime.now();
      DailyTrainSeat dailyTrainSeat = BeanUtil.copyProperties(trainSeat, DailyTrainSeat.class);
      dailyTrainSeat.setId(SnowUtil.getSnowflakeNextId());
      dailyTrainSeat.setCreateTime(now);
      dailyTrainSeat.setUpdateTime(now);
      dailyTrainSeat.setDate(date);
      dailyTrainSeat.setSell(sell);
      dailyTrainSeatMapper.insert(dailyTrainSeat);
    }

    LOG.info("Generate seat info of train[{}] for date[{}] completed", trainCode, DateUtil.formatDate(date));
  }

  public int countSeat(Date date, String trainCode, String seatType) {
    DailyTrainSeatExample example = new DailyTrainSeatExample();
    example.createCriteria()
        .andDateEqualTo(date)
        .andTrainCodeEqualTo(trainCode)
        .andSeatTypeEqualTo(seatType);
    long l = dailyTrainSeatMapper.countByExample(example);
    if (l == 0L) {
      return -1;
    }
    return (int) l;
  }
}
