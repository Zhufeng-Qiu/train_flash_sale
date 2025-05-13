package com.zephyr.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zephyr.train.business.domain.DailyTrain;
import com.zephyr.train.business.domain.DailyTrainExample;
import com.zephyr.train.business.domain.Train;
import com.zephyr.train.business.mapper.DailyTrainMapper;
import com.zephyr.train.business.req.DailyTrainQueryReq;
import com.zephyr.train.business.req.DailyTrainSaveReq;
import com.zephyr.train.business.resp.DailyTrainQueryResp;
import com.zephyr.train.common.resp.PageResp;
import com.zephyr.train.common.util.SnowUtil;
import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DailyTrainService {

  private static final Logger LOG = LoggerFactory.getLogger(DailyTrainService.class);

  @Resource
  private DailyTrainMapper dailyTrainMapper;

  @Resource
  private TrainService trainService;

  @Resource
  private DailyTrainStationService dailyTrainStationService;

  @Resource
  private DailyTrainCarriageService dailyTrainCarriageService;

  @Resource
  private DailyTrainSeatService dailyTrainSeatService;

  public void save(DailyTrainSaveReq req) {
    DateTime now = DateTime.now();
    DailyTrain dailyTrain = BeanUtil.copyProperties(req, DailyTrain.class);
    if (ObjectUtil.isNull(dailyTrain.getId())) {
      dailyTrain.setId(SnowUtil.getSnowflakeNextId());
      dailyTrain.setCreateTime(now);
      dailyTrain.setUpdateTime(now);
      dailyTrainMapper.insert(dailyTrain);
    } else {
      dailyTrain.setUpdateTime(now);
      dailyTrainMapper.updateByPrimaryKey(dailyTrain);
    }
  }

  public PageResp<DailyTrainQueryResp> queryList(DailyTrainQueryReq req) {
    DailyTrainExample dailyTrainExample = new DailyTrainExample();
    dailyTrainExample.setOrderByClause("date desc, code asc");
    DailyTrainExample.Criteria criteria = dailyTrainExample.createCriteria();
    if (ObjectUtil.isNotNull(req.getDate())) {
      criteria.andDateEqualTo(req.getDate());
    }
    if (ObjectUtil.isNotEmpty(req.getCode())) {
      criteria.andCodeEqualTo(req.getCode());
    }

    LOG.info("Query page: {}", req.getPage());
    LOG.info("Query page size: {}", req.getSize());
    PageHelper.startPage(req.getPage(), req.getSize());
    List<DailyTrain> dailyTrainList = dailyTrainMapper.selectByExample(dailyTrainExample);

    PageInfo<DailyTrain> pageInfo = new PageInfo<>(dailyTrainList);
    LOG.info("Total number of records: {}", pageInfo.getTotal());
    LOG.info("Total number of pages: {}", pageInfo.getPages());

    List<DailyTrainQueryResp> list = BeanUtil.copyToList(dailyTrainList, DailyTrainQueryResp.class);
    PageResp<DailyTrainQueryResp> pageResp = new PageResp<>();
    pageResp.setTotal(pageInfo.getTotal());
    pageResp.setList(list);
    return pageResp;
  }

  public void delete(Long id) {
    dailyTrainMapper.deleteByPrimaryKey(id);
  }

  /**
   * Generate train info for specific date including train, station, carriage and seat
   * @param date
   */
  public void genDaily(Date date) {
    List<Train> trainList = trainService.selectAll();
    if (CollUtil.isEmpty(trainList)) {
      LOG.info("No train basic data found, task terminates");
      return;
    }

    for (Train train : trainList) {
      genDailyTrain(date, train);
    }
  }

  public void genDailyTrain(Date date, Train train) {
    LOG.info("Start to generate info of train[{}] for date[{}]", train.getCode(), DateUtil.formatDate(date));

    // Delete existing data for current train
    DailyTrainExample dailyTrainExample = new DailyTrainExample();
    dailyTrainExample.createCriteria()
        .andDateEqualTo(date)
        .andCodeEqualTo(train.getCode());
    dailyTrainMapper.deleteByExample(dailyTrainExample);

    // Generating info for current train
    DateTime now = DateTime.now();
    DailyTrain dailyTrain = BeanUtil.copyProperties(train, DailyTrain.class);
    dailyTrain.setId(SnowUtil.getSnowflakeNextId());
    dailyTrain.setCreateTime(now);
    dailyTrain.setUpdateTime(now);
    dailyTrain.setDate(date);
    dailyTrainMapper.insert(dailyTrain);

    // Generate station info for current train
    dailyTrainStationService.genDaily(date, train.getCode());

    // Generate carriage info for current train
    dailyTrainCarriageService.genDaily(date, train.getCode());

    // Generate seat info for current train
    dailyTrainSeatService.genDaily(date, train.getCode());


    LOG.info("Generate info of train[{}] for date[{}] completed", train.getCode(), DateUtil.formatDate(date));
  }
}
