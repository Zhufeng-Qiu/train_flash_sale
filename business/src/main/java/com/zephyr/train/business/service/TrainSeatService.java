package com.zephyr.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zephyr.train.business.domain.TrainCarriage;
import com.zephyr.train.business.enums.SeatColEnum;
import com.zephyr.train.common.resp.PageResp;
import com.zephyr.train.common.util.SnowUtil;
import com.zephyr.train.business.domain.TrainSeat;
import com.zephyr.train.business.domain.TrainSeatExample;
import com.zephyr.train.business.mapper.TrainSeatMapper;
import com.zephyr.train.business.req.TrainSeatQueryReq;
import com.zephyr.train.business.req.TrainSeatSaveReq;
import com.zephyr.train.business.resp.TrainSeatQueryResp;
import jakarta.annotation.Resource;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrainSeatService {

  private static final Logger LOG = LoggerFactory.getLogger(TrainSeatService.class);

  @Resource
  private TrainSeatMapper trainSeatMapper;

  @Resource
  private TrainCarriageService trainCarriageService;


  public void save(TrainSeatSaveReq req) {
    DateTime now = DateTime.now();
    TrainSeat trainSeat = BeanUtil.copyProperties(req, TrainSeat.class);
    if (ObjectUtil.isNull(trainSeat.getId())) {
      trainSeat.setId(SnowUtil.getSnowflakeNextId());
      trainSeat.setCreateTime(now);
      trainSeat.setUpdateTime(now);
      trainSeatMapper.insert(trainSeat);
    } else {
      trainSeat.setUpdateTime(now);
      trainSeatMapper.updateByPrimaryKey(trainSeat);
    }
  }

  public PageResp<TrainSeatQueryResp> queryList(TrainSeatQueryReq req) {
    TrainSeatExample trainSeatExample = new TrainSeatExample();
    trainSeatExample.setOrderByClause("train_code asc, carriage_index asc, carriage_seat_index asc");
    TrainSeatExample.Criteria criteria = trainSeatExample.createCriteria();
    if (ObjectUtil.isNotEmpty(req.getTrainCode())) {
      criteria.andTrainCodeEqualTo(req.getTrainCode());
    }

    LOG.info("Query page: {}", req.getPage());
    LOG.info("Query page size: {}", req.getSize());
    PageHelper.startPage(req.getPage(), req.getSize());
    List<TrainSeat> trainSeatList = trainSeatMapper.selectByExample(trainSeatExample);

    PageInfo<TrainSeat> pageInfo = new PageInfo<>(trainSeatList);
    LOG.info("Total number of records: {}", pageInfo.getTotal());
    LOG.info("Total number of pages: {}", pageInfo.getPages());

    List<TrainSeatQueryResp> list = BeanUtil.copyToList(trainSeatList, TrainSeatQueryResp.class);
    PageResp<TrainSeatQueryResp> pageResp = new PageResp<>();
    pageResp.setTotal(pageInfo.getTotal());
    pageResp.setList(list);
    return pageResp;
  }

  public void delete(Long id) {
    trainSeatMapper.deleteByPrimaryKey(id);
  }

  @Transactional
  public void genTrainSeat(String trainCode) {
    DateTime now = DateTime.now();

    // Clean all seat records
    TrainSeatExample trainSeatExample = new TrainSeatExample();
    TrainSeatExample.Criteria criteria = trainSeatExample.createCriteria();
    criteria.andTrainCodeEqualTo(trainCode);
    trainSeatMapper.deleteByExample(trainSeatExample);

    // Search all carriage info for current train code
    List<TrainCarriage> carriageList = trainCarriageService.selectByTrainCode(trainCode);
    LOG.info("Carriage number of current train code：{}", carriageList.size());

    // Generate seats recursively
    for (TrainCarriage trainCarriage : carriageList) {
      // Get carriage info: [rows], [seat type]
      Integer rowCount = trainCarriage.getRowCount();
      String seatType = trainCarriage.getSeatType();
      int seatIndex = 1;

      // Get the [columns] based on seat type of carriage. For example, if the seat type of carriage is first class seat, columnList={ACDF}
      List<SeatColEnum> colEnumList = SeatColEnum.getColsByType(seatType);
      LOG.info("Get the columns based on seat type of carriage：{}", colEnumList);

      // Recurse rows
      for (int row = 1; row <= rowCount; row++) {
        // Recurse columns
        for (SeatColEnum seatColEnum : colEnumList) {
          // Construct seat data and save to database
          TrainSeat trainSeat = new TrainSeat();
          trainSeat.setId(SnowUtil.getSnowflakeNextId());
          trainSeat.setTrainCode(trainCode);
          trainSeat.setCarriageIndex(trainCarriage.getIndex());
          trainSeat.setRow(StrUtil.fillBefore(String.valueOf(row), '0', 2));
          trainSeat.setCol(seatColEnum.getCode());
          trainSeat.setSeatType(seatType);
          trainSeat.setCarriageSeatIndex(seatIndex++);
          trainSeat.setCreateTime(now);
          trainSeat.setUpdateTime(now);
          trainSeatMapper.insert(trainSeat);
        }
      }
    }
  }
}
