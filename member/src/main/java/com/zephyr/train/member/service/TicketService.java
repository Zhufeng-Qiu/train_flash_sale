package com.zephyr.train.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zephyr.train.common.req.MemberTicketReq;
import com.zephyr.train.common.resp.PageResp;
import com.zephyr.train.common.util.SnowUtil;
import com.zephyr.train.member.domain.Ticket;
import com.zephyr.train.member.domain.TicketExample;
import com.zephyr.train.member.mapper.TicketMapper;
import com.zephyr.train.member.req.TicketQueryReq;
import com.zephyr.train.member.resp.TicketQueryResp;
import jakarta.annotation.Resource;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TicketService {

  private static final Logger LOG = LoggerFactory.getLogger(TicketService.class);

  @Resource
  private TicketMapper ticketMapper;

  /**
   * Member purchases tickets then store
   *
   * @param req
   */
  public void save(MemberTicketReq req) {
    DateTime now = DateTime.now();
    Ticket ticket = BeanUtil.copyProperties(req, Ticket.class);
    ticket.setId(SnowUtil.getSnowflakeNextId());
    ticket.setCreateTime(now);
    ticket.setUpdateTime(now);
    ticketMapper.insert(ticket);
  }

  public PageResp<TicketQueryResp> queryList(TicketQueryReq req) {
    TicketExample ticketExample = new TicketExample();
    ticketExample.setOrderByClause("id desc");
    TicketExample.Criteria criteria = ticketExample.createCriteria();

    LOG.info("Query page: {}", req.getPage());
    LOG.info("Query page size: {}", req.getSize());
    PageHelper.startPage(req.getPage(), req.getSize());
    List<Ticket> ticketList = ticketMapper.selectByExample(ticketExample);

    PageInfo<Ticket> pageInfo = new PageInfo<>(ticketList);
    LOG.info("Total number of records: {}", pageInfo.getTotal());
    LOG.info("Total number of pages: {}", pageInfo.getPages());

    List<TicketQueryResp> list = BeanUtil.copyToList(ticketList, TicketQueryResp.class);
    PageResp<TicketQueryResp> pageResp = new PageResp<>();
    pageResp.setTotal(pageInfo.getTotal());
    pageResp.setList(list);
    return pageResp;
  }

  public void delete(Long id) {
    ticketMapper.deleteByPrimaryKey(id);
  }
}
