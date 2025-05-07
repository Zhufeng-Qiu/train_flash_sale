package com.zephyr.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zephyr.train.common.resp.PageResp;
import com.zephyr.train.common.util.SnowUtil;
import com.zephyr.train.business.domain.TrainStation;
import com.zephyr.train.business.domain.TrainStationExample;
import com.zephyr.train.business.mapper.TrainStationMapper;
import com.zephyr.train.business.req.TrainStationQueryReq;
import com.zephyr.train.business.req.TrainStationSaveReq;
import com.zephyr.train.business.resp.TrainStationQueryResp;
import jakarta.annotation.Resource;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TrainStationService {

  private static final Logger LOG = LoggerFactory.getLogger(TrainStationService.class);

  @Resource
  private TrainStationMapper trainStationMapper;

  public void save(TrainStationSaveReq req) {
    DateTime now = DateTime.now();
    TrainStation trainStation = BeanUtil.copyProperties(req, TrainStation.class);
    if (ObjectUtil.isNull(trainStation.getId())) {
      trainStation.setId(SnowUtil.getSnowflakeNextId());
      trainStation.setCreateTime(now);
      trainStation.setUpdateTime(now);
      trainStationMapper.insert(trainStation);
    } else {
      trainStation.setUpdateTime(now);
      trainStationMapper.updateByPrimaryKey(trainStation);
    }
  }

  public PageResp<TrainStationQueryResp> queryList(TrainStationQueryReq req) {
    TrainStationExample trainStationExample = new TrainStationExample();
    trainStationExample.setOrderByClause("id desc");
    TrainStationExample.Criteria criteria = trainStationExample.createCriteria();

    LOG.info("Query page: {}", req.getPage());
    LOG.info("Query page size: {}", req.getSize());
    PageHelper.startPage(req.getPage(), req.getSize());
    List<TrainStation> trainStationList = trainStationMapper.selectByExample(trainStationExample);

    PageInfo<TrainStation> pageInfo = new PageInfo<>(trainStationList);
    LOG.info("Total number of records: {}", pageInfo.getTotal());
    LOG.info("Total number of pages: {}", pageInfo.getPages());

    List<TrainStationQueryResp> list = BeanUtil.copyToList(trainStationList, TrainStationQueryResp.class);
    PageResp<TrainStationQueryResp> pageResp = new PageResp<>();
    pageResp.setTotal(pageInfo.getTotal());
    pageResp.setList(list);
    return pageResp;
  }

  public void delete(Long id) {
    trainStationMapper.deleteByPrimaryKey(id);
  }
}
