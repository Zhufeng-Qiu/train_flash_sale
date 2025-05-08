package com.zephyr.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zephyr.train.business.domain.Station;
import com.zephyr.train.business.domain.StationExample;
import com.zephyr.train.business.mapper.StationMapper;
import com.zephyr.train.business.req.StationQueryReq;
import com.zephyr.train.business.req.StationSaveReq;
import com.zephyr.train.business.resp.StationQueryResp;
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
public class StationService {

  private static final Logger LOG = LoggerFactory.getLogger(StationService.class);

  @Resource
  private StationMapper stationMapper;

  public void save(StationSaveReq req) {
    DateTime now = DateTime.now();
    Station station = BeanUtil.copyProperties(req, Station.class);

    // Check whether unique key exists before saving
    Station stationDB = selectByUnique(req.getName());
    if (ObjectUtil.isNotEmpty(stationDB)) {
      throw new BusinessException(BusinessExceptionEnum.BUSINESS_STATION_NAME_UNIQUE_ERROR);
    }
    if (ObjectUtil.isNull(station.getId())) {
      station.setId(SnowUtil.getSnowflakeNextId());
      station.setCreateTime(now);
      station.setUpdateTime(now);
      stationMapper.insert(station);
    } else {
      station.setUpdateTime(now);
      stationMapper.updateByPrimaryKey(station);
    }
  }

  private Station selectByUnique(String name) {
    StationExample stationExample = new StationExample();
    stationExample.createCriteria().andNameEqualTo(name);
    List<Station> list = stationMapper.selectByExample(stationExample);
    if (CollUtil.isNotEmpty(list)) {
      return list.get(0);
    }
    return null;
  }

  public PageResp<StationQueryResp> queryList(StationQueryReq req) {
    StationExample stationExample = new StationExample();
    stationExample.setOrderByClause("id desc");
    StationExample.Criteria criteria = stationExample.createCriteria();

    LOG.info("Query page: {}", req.getPage());
    LOG.info("Query page size: {}", req.getSize());
    PageHelper.startPage(req.getPage(), req.getSize());
    List<Station> stationList = stationMapper.selectByExample(stationExample);

    PageInfo<Station> pageInfo = new PageInfo<>(stationList);
    LOG.info("Total number of records: {}", pageInfo.getTotal());
    LOG.info("Total number of pages: {}", pageInfo.getPages());

    List<StationQueryResp> list = BeanUtil.copyToList(stationList, StationQueryResp.class);
    PageResp<StationQueryResp> pageResp = new PageResp<>();
    pageResp.setTotal(pageInfo.getTotal());
    pageResp.setList(list);
    return pageResp;
  }

  public void delete(Long id) {
    stationMapper.deleteByPrimaryKey(id);
  }

  public List<StationQueryResp> queryAll() {
    StationExample stationExample = new StationExample();
    stationExample.setOrderByClause("name_pinyin asc");
    List<Station> stationList = stationMapper.selectByExample(stationExample);
    return BeanUtil.copyToList(stationList, StationQueryResp.class);
  }
}
