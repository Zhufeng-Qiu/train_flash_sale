package com.zephyr.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zephyr.train.business.domain.TrainCarriage;
import com.zephyr.train.business.domain.TrainCarriageExample;
import com.zephyr.train.business.enums.SeatColEnum;
import com.zephyr.train.business.mapper.TrainCarriageMapper;
import com.zephyr.train.business.req.TrainCarriageQueryReq;
import com.zephyr.train.business.req.TrainCarriageSaveReq;
import com.zephyr.train.business.resp.TrainCarriageQueryResp;
import com.zephyr.train.common.exception.BusinessException;
import com.zephyr.train.common.exception.BusinessExceptionEnum;
import com.zephyr.train.common.resp.PageResp;
import com.zephyr.train.common.util.SnowUtil;
import jakarta.annotation.Resource;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TrainCarriageService {

  private static final Logger LOG = LoggerFactory.getLogger(TrainCarriageService.class);

  @Resource
  private TrainCarriageMapper trainCarriageMapper;

  public void save(TrainCarriageSaveReq req) {
    DateTime now = DateTime.now();

    // Calculate cols and total seat number automatically
    List<SeatColEnum> seatColEnums = SeatColEnum.getColsByType(req.getSeatType());
    req.setColCount(seatColEnums.size());
    req.setSeatCount(req.getColCount() * req.getRowCount());

    TrainCarriage trainCarriage = BeanUtil.copyProperties(req, TrainCarriage.class);

    if (ObjectUtil.isNull(trainCarriage.getId())) {

      // Check whether unique key exists before saving
      TrainCarriage trainCarriageDB = selectByUnique(req.getTrainCode(), req.getIndex());
      if (ObjectUtil.isNotEmpty(trainCarriageDB)) {
        throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_CARRIAGE_INDEX_UNIQUE_ERROR);
      }

      trainCarriage.setId(SnowUtil.getSnowflakeNextId());
      trainCarriage.setCreateTime(now);
      trainCarriage.setUpdateTime(now);
      trainCarriageMapper.insert(trainCarriage);
    } else {
      trainCarriage.setUpdateTime(now);
      trainCarriageMapper.updateByPrimaryKey(trainCarriage);
    }
  }

  public PageResp<TrainCarriageQueryResp> queryList(TrainCarriageQueryReq req) {
    TrainCarriageExample trainCarriageExample = new TrainCarriageExample();
    trainCarriageExample.setOrderByClause("train_code asc, `index` asc");
    TrainCarriageExample.Criteria criteria = trainCarriageExample.createCriteria();
    if (ObjectUtil.isNotEmpty(req.getTrainCode())) {
      criteria.andTrainCodeEqualTo(req.getTrainCode());
    }

    LOG.info("Query page: {}", req.getPage());
    LOG.info("Query page size: {}", req.getSize());
    PageHelper.startPage(req.getPage(), req.getSize());
    List<TrainCarriage> trainCarriageList = trainCarriageMapper.selectByExample(trainCarriageExample);

    PageInfo<TrainCarriage> pageInfo = new PageInfo<>(trainCarriageList);
    LOG.info("Total number of records: {}", pageInfo.getTotal());
    LOG.info("Total number of pages: {}", pageInfo.getPages());

    List<TrainCarriageQueryResp> list = BeanUtil.copyToList(trainCarriageList, TrainCarriageQueryResp.class);
    PageResp<TrainCarriageQueryResp> pageResp = new PageResp<>();
    pageResp.setTotal(pageInfo.getTotal());
    pageResp.setList(list);
    return pageResp;
  }

  public void delete(Long id) {
    trainCarriageMapper.deleteByPrimaryKey(id);
  }

  public List<TrainCarriage> selectByTrainCode(String trainCode) {
    TrainCarriageExample trainCarriageExample = new TrainCarriageExample();
    trainCarriageExample.setOrderByClause("`index` asc");
    TrainCarriageExample.Criteria criteria = trainCarriageExample.createCriteria();
    criteria.andTrainCodeEqualTo(trainCode);
    return trainCarriageMapper.selectByExample(trainCarriageExample);
  }

  private TrainCarriage selectByUnique(String trainCode, Integer index) {
    TrainCarriageExample trainCarriageExample = new TrainCarriageExample();
    trainCarriageExample.createCriteria()
        .andTrainCodeEqualTo(trainCode)
        .andIndexEqualTo(index);
    List<TrainCarriage> list = trainCarriageMapper.selectByExample(trainCarriageExample);
    if (CollUtil.isNotEmpty(list)) {
      return list.get(0);
    } else {
      return null;
    }
  }
}
