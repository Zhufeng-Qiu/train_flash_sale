package com.zephyr.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zephyr.train.common.resp.PageResp;
import com.zephyr.train.common.util.SnowUtil;
import com.zephyr.train.business.domain.TrainCarriage;
import com.zephyr.train.business.domain.TrainCarriageExample;
import com.zephyr.train.business.mapper.TrainCarriageMapper;
import com.zephyr.train.business.req.TrainCarriageQueryReq;
import com.zephyr.train.business.req.TrainCarriageSaveReq;
import com.zephyr.train.business.resp.TrainCarriageQueryResp;
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
    TrainCarriage trainCarriage = BeanUtil.copyProperties(req, TrainCarriage.class);
    if (ObjectUtil.isNull(trainCarriage.getId())) {
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
}
