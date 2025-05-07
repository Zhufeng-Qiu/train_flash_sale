package com.zephyr.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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

@Service
public class TrainSeatService {

  private static final Logger LOG = LoggerFactory.getLogger(TrainSeatService.class);

  @Resource
  private TrainSeatMapper trainSeatMapper;

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
    trainSeatExample.setOrderByClause("id desc");
    TrainSeatExample.Criteria criteria = trainSeatExample.createCriteria();

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
}
