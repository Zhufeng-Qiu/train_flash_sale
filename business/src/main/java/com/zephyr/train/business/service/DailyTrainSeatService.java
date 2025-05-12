package com.zephyr.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zephyr.train.common.resp.PageResp;
import com.zephyr.train.common.util.SnowUtil;
import com.zephyr.train.business.domain.DailyTrainSeat;
import com.zephyr.train.business.domain.DailyTrainSeatExample;
import com.zephyr.train.business.mapper.DailyTrainSeatMapper;
import com.zephyr.train.business.req.DailyTrainSeatQueryReq;
import com.zephyr.train.business.req.DailyTrainSeatSaveReq;
import com.zephyr.train.business.resp.DailyTrainSeatQueryResp;
import jakarta.annotation.Resource;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DailyTrainSeatService {

  private static final Logger LOG = LoggerFactory.getLogger(DailyTrainSeatService.class);

  @Resource
  private DailyTrainSeatMapper dailyTrainSeatMapper;

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
    dailyTrainSeatExample.setOrderByClause("train_code asc, carriage_index asc, carriage_seat_index asc");
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
}
