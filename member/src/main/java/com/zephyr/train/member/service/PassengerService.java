package com.zephyr.train.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zephyr.train.common.context.LoginMemberContext;
import com.zephyr.train.common.resp.PageResp;
import com.zephyr.train.common.util.SnowUtil;
import com.zephyr.train.member.domain.Passenger;
import com.zephyr.train.member.domain.PassengerExample;
import com.zephyr.train.member.mapper.PassengerMapper;
import com.zephyr.train.member.req.PassengerQueryReq;
import com.zephyr.train.member.req.PassengerSaveReq;
import com.zephyr.train.member.resp.PassengerQueryResp;
import jakarta.annotation.Resource;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PassengerService {

  private static final Logger LOG = LoggerFactory.getLogger(PassengerService.class);

  @Resource
  private PassengerMapper passengerMapper;

  public void save(PassengerSaveReq req) {
    DateTime now = DateTime.now();
    Passenger passenger = BeanUtil.copyProperties(req, Passenger.class);
    if (ObjectUtil.isNull(passenger.getId())) {
      passenger.setId(SnowUtil.getSnowflakeNextId());
      passenger.setMemberId(LoginMemberContext.getId());
      passenger.setCreateTime(now);
      passenger.setUpdateTime(now);
      passengerMapper.insert(passenger);
    } else {
      passenger.setUpdateTime(now);
      passengerMapper.updateByPrimaryKey(passenger);
    }
  }

  public PageResp<PassengerQueryResp> queryList(PassengerQueryReq req) {
    PassengerExample passengerExample = new PassengerExample();
    passengerExample.setOrderByClause("id desc");
    PassengerExample.Criteria criteria = passengerExample.createCriteria();
    if (ObjectUtil.isNotNull(req.getMemberId())) {
      criteria.andMemberIdEqualTo(req.getMemberId());
    }
    LOG.info("Query page: {}", req.getPage());
    LOG.info("Query page size: {}", req.getSize());
    PageHelper.startPage(req.getPage(), req.getSize());
    List<Passenger> passengerList = passengerMapper.selectByExample(passengerExample);

    PageInfo<Passenger> pageInfo = new PageInfo<>(passengerList);
    LOG.info("Total number of records: {}", pageInfo.getTotal());
    LOG.info("Total number of pages: {}", pageInfo.getPages());

    List<PassengerQueryResp> list = BeanUtil.copyToList(passengerList, PassengerQueryResp.class);
    PageResp<PassengerQueryResp> pageResp = new PageResp<>();
    pageResp.setTotal(pageInfo.getTotal());
    pageResp.setList(list);
    return pageResp;
  }

  public void delete(Long id) {
    passengerMapper.deleteByPrimaryKey(id);
  }

  /**
   * Query all the passengers for current member
   */
  public List<PassengerQueryResp> queryMine() {
    PassengerExample passengerExample = new PassengerExample();
    passengerExample.setOrderByClause("name asc");
    PassengerExample.Criteria criteria = passengerExample.createCriteria();
    criteria.andMemberIdEqualTo(LoginMemberContext.getId());
    List<Passenger> list = passengerMapper.selectByExample(passengerExample);
    return BeanUtil.copyToList(list, PassengerQueryResp.class);
  }
}
