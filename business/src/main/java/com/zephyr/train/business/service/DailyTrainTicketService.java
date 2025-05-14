package com.zephyr.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zephyr.train.common.resp.PageResp;
import com.zephyr.train.common.util.SnowUtil;
import com.zephyr.train.business.domain.DailyTrainTicket;
import com.zephyr.train.business.domain.DailyTrainTicketExample;
import com.zephyr.train.business.mapper.DailyTrainTicketMapper;
import com.zephyr.train.business.req.DailyTrainTicketQueryReq;
import com.zephyr.train.business.req.DailyTrainTicketSaveReq;
import com.zephyr.train.business.resp.DailyTrainTicketQueryResp;
import jakarta.annotation.Resource;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DailyTrainTicketService {

  private static final Logger LOG = LoggerFactory.getLogger(DailyTrainTicketService.class);

  @Resource
  private DailyTrainTicketMapper dailyTrainTicketMapper;

  public void save(DailyTrainTicketSaveReq req) {
    DateTime now = DateTime.now();
    DailyTrainTicket dailyTrainTicket = BeanUtil.copyProperties(req, DailyTrainTicket.class);
    if (ObjectUtil.isNull(dailyTrainTicket.getId())) {
      dailyTrainTicket.setId(SnowUtil.getSnowflakeNextId());
      dailyTrainTicket.setCreateTime(now);
      dailyTrainTicket.setUpdateTime(now);
      dailyTrainTicketMapper.insert(dailyTrainTicket);
    } else {
      dailyTrainTicket.setUpdateTime(now);
      dailyTrainTicketMapper.updateByPrimaryKey(dailyTrainTicket);
    }
  }

  public PageResp<DailyTrainTicketQueryResp> queryList(DailyTrainTicketQueryReq req) {
    DailyTrainTicketExample dailyTrainTicketExample = new DailyTrainTicketExample();
    dailyTrainTicketExample.setOrderByClause("id desc");
    DailyTrainTicketExample.Criteria criteria = dailyTrainTicketExample.createCriteria();

    LOG.info("Query page: {}", req.getPage());
    LOG.info("Query page size: {}", req.getSize());
    PageHelper.startPage(req.getPage(), req.getSize());
    List<DailyTrainTicket> dailyTrainTicketList = dailyTrainTicketMapper.selectByExample(dailyTrainTicketExample);

    PageInfo<DailyTrainTicket> pageInfo = new PageInfo<>(dailyTrainTicketList);
    LOG.info("Total number of records: {}", pageInfo.getTotal());
    LOG.info("Total number of pages: {}", pageInfo.getPages());

    List<DailyTrainTicketQueryResp> list = BeanUtil.copyToList(dailyTrainTicketList, DailyTrainTicketQueryResp.class);
    PageResp<DailyTrainTicketQueryResp> pageResp = new PageResp<>();
    pageResp.setTotal(pageInfo.getTotal());
    pageResp.setList(list);
    return pageResp;
  }

  public void delete(Long id) {
    dailyTrainTicketMapper.deleteByPrimaryKey(id);
  }
}
