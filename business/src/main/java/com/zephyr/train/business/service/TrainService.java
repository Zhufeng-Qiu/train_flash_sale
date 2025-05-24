package com.zephyr.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zephyr.train.business.domain.Train;
import com.zephyr.train.business.domain.TrainExample;
import com.zephyr.train.business.mapper.TrainMapper;
import com.zephyr.train.business.req.TrainQueryReq;
import com.zephyr.train.business.req.TrainSaveReq;
import com.zephyr.train.business.resp.TrainQueryResp;
import com.zephyr.train.common.exception.BusinessException;
import com.zephyr.train.common.exception.BusinessExceptionEnum;
import com.zephyr.train.common.resp.PageResp;
import com.zephyr.train.common.util.SnowUtil;
import jakarta.annotation.Resource;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrainService {

  private static final Logger LOG = LoggerFactory.getLogger(TrainService.class);

  @Resource
  private TrainMapper trainMapper;

  public void save(TrainSaveReq req) {
    DateTime now = DateTime.now();
    Train train = BeanUtil.copyProperties(req, Train.class);
    if (ObjectUtil.isNull(train.getId())) {

      // Check whether unique key exists before saving
      Train trainDB = selectByUnique(req.getCode());
      if (ObjectUtil.isNotEmpty(trainDB)) {
        throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_CODE_UNIQUE_ERROR);
      }

      train.setId(SnowUtil.getSnowflakeNextId());
      train.setCreateTime(now);
      train.setUpdateTime(now);
      trainMapper.insert(train);
    } else {
      train.setUpdateTime(now);
      trainMapper.updateByPrimaryKey(train);
    }
  }

  public PageResp<TrainQueryResp> queryList(TrainQueryReq req) {
    TrainExample trainExample = new TrainExample();
    trainExample.setOrderByClause("id desc");
    TrainExample.Criteria criteria = trainExample.createCriteria();

    LOG.info("Query page: {}", req.getPage());
    LOG.info("Query page size: {}", req.getSize());
    PageHelper.startPage(req.getPage(), req.getSize());
    List<Train> trainList = trainMapper.selectByExample(trainExample);

    PageInfo<Train> pageInfo = new PageInfo<>(trainList);
    LOG.info("Total number of records: {}", pageInfo.getTotal());
    LOG.info("Total number of pages: {}", pageInfo.getPages());

    List<TrainQueryResp> list = BeanUtil.copyToList(trainList, TrainQueryResp.class);
    PageResp<TrainQueryResp> pageResp = new PageResp<>();
    pageResp.setTotal(pageInfo.getTotal());
    pageResp.setList(list);
    return pageResp;
  }

  public void delete(Long id) {
    trainMapper.deleteByPrimaryKey(id);
  }

  @Transactional
  public List<TrainQueryResp> queryAll() {
    List<Train> trainList = selectAll();
//     LOG.info("Query again");
//     trainList = selectAll();
    return BeanUtil.copyToList(trainList, TrainQueryResp.class);
  }

  public List<Train> selectAll() {
    TrainExample trainExample = new TrainExample();
    trainExample.setOrderByClause("code asc");
    return trainMapper.selectByExample(trainExample);
  }

  private Train selectByUnique(String code) {
    TrainExample trainExample = new TrainExample();
    trainExample.createCriteria()
        .andCodeEqualTo(code);
    List<Train> list = trainMapper.selectByExample(trainExample);
    if (CollUtil.isNotEmpty(list)) {
      return list.get(0);
    } else {
      return null;
    }
  }
}
