package com.zephyr.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zephyr.train.common.resp.PageResp;
import com.zephyr.train.common.util.SnowUtil;
import com.zephyr.train.business.domain.DailyTrainCarriage;
import com.zephyr.train.business.domain.DailyTrainCarriageExample;
import com.zephyr.train.business.mapper.DailyTrainCarriageMapper;
import com.zephyr.train.business.req.DailyTrainCarriageQueryReq;
import com.zephyr.train.business.req.DailyTrainCarriageSaveReq;
import com.zephyr.train.business.resp.DailyTrainCarriageQueryResp;
import jakarta.annotation.Resource;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DailyTrainCarriageService {

  private static final Logger LOG = LoggerFactory.getLogger(DailyTrainCarriageService.class);

  @Resource
  private DailyTrainCarriageMapper dailyTrainCarriageMapper;

  public void save(DailyTrainCarriageSaveReq req) {
    DateTime now = DateTime.now();
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
    dailyTrainCarriageExample.setOrderByClause("id desc");
    DailyTrainCarriageExample.Criteria criteria = dailyTrainCarriageExample.createCriteria();

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
}
