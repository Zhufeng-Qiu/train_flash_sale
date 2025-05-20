package com.zephyr.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zephyr.train.business.domain.DailyTrainCarriage;
import com.zephyr.train.business.domain.DailyTrainCarriageExample;
import com.zephyr.train.business.domain.TrainCarriage;
import com.zephyr.train.business.enums.SeatColEnum;
import com.zephyr.train.business.mapper.DailyTrainCarriageMapper;
import com.zephyr.train.business.req.DailyTrainCarriageQueryReq;
import com.zephyr.train.business.req.DailyTrainCarriageSaveReq;
import com.zephyr.train.business.resp.DailyTrainCarriageQueryResp;
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
public class DailyTrainCarriageService {

  private static final Logger LOG = LoggerFactory.getLogger(DailyTrainCarriageService.class);

  @Resource
  private DailyTrainCarriageMapper dailyTrainCarriageMapper;

  @Resource
  private TrainCarriageService trainCarriageService;


  public void save(DailyTrainCarriageSaveReq req) {
    DateTime now = DateTime.now();

    // Calculate cols and total seat number automatically
    List<SeatColEnum> seatColEnums = SeatColEnum.getColsByType(req.getSeatType());
    req.setColCount(seatColEnums.size());
    req.setSeatCount(req.getColCount() * req.getRowCount());

    DailyTrainCarriage dailyTrainCarriage = BeanUtil.copyProperties(req, DailyTrainCarriage.class);
    if (ObjectUtil.isNull(dailyTrainCarriage.getId())) {
      dailyTrainCarriage.setId(SnowUtil.getSnowflakeNextId());
      dailyTrainCarriage.setCreateTime(now);
      dailyTrainCarriage.setUpdateTime(now);
      dailyTrainCarriageMapper.insert(dailyTrainCarriage);
    } else {
      dailyTrainCarriage.setUpdateTime(now);
      dailyTrainCarriageMapper.updateByPrimaryKey(dailyTrainCarriage);
    }
  }

  public PageResp<DailyTrainCarriageQueryResp> queryList(DailyTrainCarriageQueryReq req) {
    DailyTrainCarriageExample dailyTrainCarriageExample = new DailyTrainCarriageExample();
    dailyTrainCarriageExample.setOrderByClause("date desc, train_code asc, `index` asc");
    DailyTrainCarriageExample.Criteria criteria = dailyTrainCarriageExample.createCriteria();
    if (ObjectUtil.isNotNull(req.getDate())) {
      criteria.andDateEqualTo(req.getDate());
    }
    if (ObjectUtil.isNotEmpty(req.getTrainCode())) {
      criteria.andTrainCodeEqualTo(req.getTrainCode());
    }

    LOG.info("Query page: {}", req.getPage());
    LOG.info("Query page size: {}", req.getSize());
    PageHelper.startPage(req.getPage(), req.getSize());
    List<DailyTrainCarriage> dailyTrainCarriageList = dailyTrainCarriageMapper.selectByExample(dailyTrainCarriageExample);

    PageInfo<DailyTrainCarriage> pageInfo = new PageInfo<>(dailyTrainCarriageList);
    LOG.info("Total number of records: {}", pageInfo.getTotal());
    LOG.info("Total number of pages: {}", pageInfo.getPages());

    List<DailyTrainCarriageQueryResp> list = BeanUtil.copyToList(dailyTrainCarriageList, DailyTrainCarriageQueryResp.class);
    PageResp<DailyTrainCarriageQueryResp> pageResp = new PageResp<>();
    pageResp.setTotal(pageInfo.getTotal());
    pageResp.setList(list);
    return pageResp;
  }

  public void delete(Long id) {
    dailyTrainCarriageMapper.deleteByPrimaryKey(id);
  }

  @Transactional
  public void genDaily(Date date, String trainCode) {
    LOG.info("Start to generate carriage info of train[{}] for date[{}]", trainCode, DateUtil.formatDate(date));

    // Delete carriage info for specific train and date
    DailyTrainCarriageExample dailyTrainCarriageExample = new DailyTrainCarriageExample();
    dailyTrainCarriageExample.createCriteria()
        .andDateEqualTo(date)
        .andTrainCodeEqualTo(trainCode);
    dailyTrainCarriageMapper.deleteByExample(dailyTrainCarriageExample);

    // Query carriage info for specific train
    List<TrainCarriage> carriageList = trainCarriageService.selectByTrainCode(trainCode);
    if (CollUtil.isEmpty(carriageList)) {
      LOG.info("No carriage basic data of this train found, generation of carriage info terminates");
      return;
    }

    for (TrainCarriage trainCarriage : carriageList) {
      DateTime now = DateTime.now();
      DailyTrainCarriage dailyTrainCarriage = BeanUtil.copyProperties(trainCarriage, DailyTrainCarriage.class);
      dailyTrainCarriage.setId(SnowUtil.getSnowflakeNextId());
      dailyTrainCarriage.setCreateTime(now);
      dailyTrainCarriage.setUpdateTime(now);
      dailyTrainCarriage.setDate(date);
      dailyTrainCarriageMapper.insert(dailyTrainCarriage);
    }

    LOG.info("Generate carriage info of train[{}] for date[{}] completed", trainCode, DateUtil.formatDate(date));
  }

  public List<DailyTrainCarriage> selectBySeatType (Date date, String trainCode, String seatType) {
    DailyTrainCarriageExample example = new DailyTrainCarriageExample();
    example.createCriteria()
        .andDateEqualTo(date)
        .andTrainCodeEqualTo(trainCode)
        .andSeatTypeEqualTo(seatType);
    return dailyTrainCarriageMapper.selectByExample(example);
  }
}
