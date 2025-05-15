package com.zephyr.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zephyr.train.business.domain.DailyTrainStation;
import com.zephyr.train.business.domain.DailyTrainStationExample;
import com.zephyr.train.business.domain.TrainStation;
import com.zephyr.train.business.mapper.DailyTrainStationMapper;
import com.zephyr.train.business.req.DailyTrainStationQueryReq;
import com.zephyr.train.business.req.DailyTrainStationSaveReq;
import com.zephyr.train.business.resp.DailyTrainStationQueryResp;
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
public class DailyTrainStationService {

  private static final Logger LOG = LoggerFactory.getLogger(DailyTrainStationService.class);

  @Resource
  private DailyTrainStationMapper dailyTrainStationMapper;

  @Resource
  private TrainStationService trainStationService;

  public void save(DailyTrainStationSaveReq req) {
    DateTime now = DateTime.now();
    DailyTrainStation dailyTrainStation = BeanUtil.copyProperties(req, DailyTrainStation.class);
    if (ObjectUtil.isNull(dailyTrainStation.getId())) {
      dailyTrainStation.setId(SnowUtil.getSnowflakeNextId());
      dailyTrainStation.setCreateTime(now);
      dailyTrainStation.setUpdateTime(now);
      dailyTrainStationMapper.insert(dailyTrainStation);
    } else {
      dailyTrainStation.setUpdateTime(now);
      dailyTrainStationMapper.updateByPrimaryKey(dailyTrainStation);
    }
  }

  public PageResp<DailyTrainStationQueryResp> queryList(DailyTrainStationQueryReq req) {
    DailyTrainStationExample dailyTrainStationExample = new DailyTrainStationExample();
    dailyTrainStationExample.setOrderByClause("date desc, train_code asc, `index` asc");
    DailyTrainStationExample.Criteria criteria = dailyTrainStationExample.createCriteria();
    if (ObjectUtil.isNotNull(req.getDate())) {
      criteria.andDateEqualTo(req.getDate());
    }
    if (ObjectUtil.isNotEmpty(req.getTrainCode())) {
      criteria.andTrainCodeEqualTo(req.getTrainCode());
    }

    LOG.info("Query page: {}", req.getPage());
    LOG.info("Query page size: {}", req.getSize());
    PageHelper.startPage(req.getPage(), req.getSize());
    List<DailyTrainStation> dailyTrainStationList = dailyTrainStationMapper.selectByExample(dailyTrainStationExample);

    PageInfo<DailyTrainStation> pageInfo = new PageInfo<>(dailyTrainStationList);
    LOG.info("Total number of records: {}", pageInfo.getTotal());
    LOG.info("Total number of pages: {}", pageInfo.getPages());

    List<DailyTrainStationQueryResp> list = BeanUtil.copyToList(dailyTrainStationList, DailyTrainStationQueryResp.class);
    PageResp<DailyTrainStationQueryResp> pageResp = new PageResp<>();
    pageResp.setTotal(pageInfo.getTotal());
    pageResp.setList(list);
    return pageResp;
  }

  public void delete(Long id) {
    dailyTrainStationMapper.deleteByPrimaryKey(id);
  }

  @Transactional
  public void genDaily(Date date, String trainCode) {
    LOG.info("Start to generate station info of train[{}] for date[{}]", trainCode, DateUtil.formatDate(date));

    // Delete station info for specific train and date
    DailyTrainStationExample dailyTrainStationExample = new DailyTrainStationExample();
    dailyTrainStationExample.createCriteria()
        .andDateEqualTo(date)
        .andTrainCodeEqualTo(trainCode);
    dailyTrainStationMapper.deleteByExample(dailyTrainStationExample);

    // Query station info for specific train
    List<TrainStation> stationList = trainStationService.selectByTrainCode(trainCode);
    if (CollUtil.isEmpty(stationList)) {
      LOG.info("No station basic data of this train found, generation of station info terminates");
      return;
    }

    for (TrainStation trainStation : stationList) {
      DateTime now = DateTime.now();
      DailyTrainStation dailyTrainStation = BeanUtil.copyProperties(trainStation, DailyTrainStation.class);
      dailyTrainStation.setId(SnowUtil.getSnowflakeNextId());
      dailyTrainStation.setCreateTime(now);
      dailyTrainStation.setUpdateTime(now);
      dailyTrainStation.setDate(date);
      dailyTrainStationMapper.insert(dailyTrainStation);
    }

    LOG.info("Generate station info of train[{}] for date[{}] completed", trainCode, DateUtil.formatDate(date));
  }
}
