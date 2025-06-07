package com.zephyr.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zephyr.train.common.resp.PageResp;
import com.zephyr.train.common.util.SnowUtil;
import com.zephyr.train.business.domain.SkToken;
import com.zephyr.train.business.domain.SkTokenExample;
import com.zephyr.train.business.mapper.SkTokenMapper;
import com.zephyr.train.business.req.SkTokenQueryReq;
import com.zephyr.train.business.req.SkTokenSaveReq;
import com.zephyr.train.business.resp.SkTokenQueryResp;
import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SkTokenService {

  private static final Logger LOG = LoggerFactory.getLogger(SkTokenService.class);

  @Resource
  private SkTokenMapper skTokenMapper;

  @Resource
  private DailyTrainSeatService dailyTrainSeatService;

  @Resource
  private DailyTrainStationService dailyTrainStationService;

  /**
   * Initialization
   */
  public void genDaily(Date date, String trainCode) {
    LOG.info("Delete token records of train[{}] for date[{}]", trainCode, DateUtil.formatDate(date));
    SkTokenExample skTokenExample = new SkTokenExample();
    skTokenExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
    skTokenMapper.deleteByExample(skTokenExample);

    DateTime now = DateTime.now();
    SkToken skToken = new SkToken();
    skToken.setDate(date);
    skToken.setTrainCode(trainCode);
    skToken.setId(SnowUtil.getSnowflakeNextId());
    skToken.setCreateTime(now);
    skToken.setUpdateTime(now);

    int seatCount = dailyTrainSeatService.countSeat(date, trainCode);
    LOG.info("Seat number of train[{}]: {}", trainCode, seatCount);

    long stationCount = dailyTrainStationService.countByTrainCode(date, trainCode);
    LOG.info("Station number of train[{}]: {}", trainCode, stationCount);

    // The ratio "3/4" value should be set according to the actual ticket‐selling case; a single train can sell at most seatCount * (stationCount – 1) tickets.
    int count = (int) (seatCount * (stationCount - 1) * 3/4);
    LOG.info("Initialized token number of train[{}]: {}", trainCode, count);
    skToken.setCount(count);

    skTokenMapper.insert(skToken);
  }

  public void save(SkTokenSaveReq req) {
    DateTime now = DateTime.now();
    SkToken skToken = BeanUtil.copyProperties(req, SkToken.class);
    if (ObjectUtil.isNull(skToken.getId())) {
      skToken.setId(SnowUtil.getSnowflakeNextId());
      skToken.setCreateTime(now);
      skToken.setUpdateTime(now);
      skTokenMapper.insert(skToken);
    } else {
      skToken.setUpdateTime(now);
      skTokenMapper.updateByPrimaryKey(skToken);
    }
  }

  public PageResp<SkTokenQueryResp> queryList(SkTokenQueryReq req) {
    SkTokenExample skTokenExample = new SkTokenExample();
    skTokenExample.setOrderByClause("id desc");
    SkTokenExample.Criteria criteria = skTokenExample.createCriteria();

    LOG.info("Query page: {}", req.getPage());
    LOG.info("Query page size: {}", req.getSize());
    PageHelper.startPage(req.getPage(), req.getSize());
    List<SkToken> skTokenList = skTokenMapper.selectByExample(skTokenExample);

    PageInfo<SkToken> pageInfo = new PageInfo<>(skTokenList);
    LOG.info("Total number of records: {}", pageInfo.getTotal());
    LOG.info("Total number of pages: {}", pageInfo.getPages());

    List<SkTokenQueryResp> list = BeanUtil.copyToList(skTokenList, SkTokenQueryResp.class);
    PageResp<SkTokenQueryResp> pageResp = new PageResp<>();
    pageResp.setTotal(pageInfo.getTotal());
    pageResp.setList(list);
    return pageResp;
  }

  public void delete(Long id) {
    skTokenMapper.deleteByPrimaryKey(id);
  }
}
