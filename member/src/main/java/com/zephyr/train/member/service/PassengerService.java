package com.zephyr.train.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.zephyr.train.common.context.LoginMemberContext;
import com.zephyr.train.common.util.SnowUtil;
import com.zephyr.train.member.domain.Passenger;
import com.zephyr.train.member.domain.PassengerExample;
import com.zephyr.train.member.mapper.PassengerMapper;
import com.zephyr.train.member.req.PassengerQueryReq;
import com.zephyr.train.member.req.PassengerSaveReq;
import com.zephyr.train.member.resp.PassengerQueryResp;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PassengerService {
  @Resource
  private PassengerMapper passengerMapper;

  public void save(PassengerSaveReq req) {
    DateTime now = DateTime.now();
    Passenger passenger = BeanUtil.copyProperties(req, Passenger.class);
    passenger.setId(SnowUtil.getSnowflakeNextId());
    passenger.setMemberId(LoginMemberContext.getId());
    passenger.setCreateTime(now);
    passenger.setUpdateTime(now);
    passengerMapper.insert(passenger);
  }

  public List<PassengerQueryResp> queryList(PassengerQueryReq req) {
    PassengerExample passengerExample = new PassengerExample();
    PassengerExample.Criteria criteria = passengerExample.createCriteria();
    if (ObjectUtil.isNotNull(req.getMemberId())) {
      criteria.andMemberIdEqualTo(req.getMemberId());
    }
    PageHelper.startPage(req.getPage(), req.getSize());
    List<Passenger> passengerList = passengerMapper.selectByExample(passengerExample);
    return BeanUtil.copyToList(passengerList, PassengerQueryResp.class);
  }
}
