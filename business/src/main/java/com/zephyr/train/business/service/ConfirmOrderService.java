package com.zephyr.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zephyr.train.common.resp.PageResp;
import com.zephyr.train.common.util.SnowUtil;
import com.zephyr.train.business.domain.ConfirmOrder;
import com.zephyr.train.business.domain.ConfirmOrderExample;
import com.zephyr.train.business.mapper.ConfirmOrderMapper;
import com.zephyr.train.business.req.ConfirmOrderQueryReq;
import com.zephyr.train.business.req.ConfirmOrderSaveReq;
import com.zephyr.train.business.resp.ConfirmOrderQueryResp;
import jakarta.annotation.Resource;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ConfirmOrderService {

  private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderService.class);

  @Resource
  private ConfirmOrderMapper confirmOrderMapper;

  public void save(ConfirmOrderSaveReq req) {
    DateTime now = DateTime.now();
    ConfirmOrder confirmOrder = BeanUtil.copyProperties(req, ConfirmOrder.class);
    if (ObjectUtil.isNull(confirmOrder.getId())) {
      confirmOrder.setId(SnowUtil.getSnowflakeNextId());
      confirmOrder.setCreateTime(now);
      confirmOrder.setUpdateTime(now);
      confirmOrderMapper.insert(confirmOrder);
    } else {
      confirmOrder.setUpdateTime(now);
      confirmOrderMapper.updateByPrimaryKey(confirmOrder);
    }
  }

  public PageResp<ConfirmOrderQueryResp> queryList(ConfirmOrderQueryReq req) {
    ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
    confirmOrderExample.setOrderByClause("id desc");
    ConfirmOrderExample.Criteria criteria = confirmOrderExample.createCriteria();

    LOG.info("Query page: {}", req.getPage());
    LOG.info("Query page size: {}", req.getSize());
    PageHelper.startPage(req.getPage(), req.getSize());
    List<ConfirmOrder> confirmOrderList = confirmOrderMapper.selectByExample(confirmOrderExample);

    PageInfo<ConfirmOrder> pageInfo = new PageInfo<>(confirmOrderList);
    LOG.info("Total number of records: {}", pageInfo.getTotal());
    LOG.info("Total number of pages: {}", pageInfo.getPages());

    List<ConfirmOrderQueryResp> list = BeanUtil.copyToList(confirmOrderList, ConfirmOrderQueryResp.class);
    PageResp<ConfirmOrderQueryResp> pageResp = new PageResp<>();
    pageResp.setTotal(pageInfo.getTotal());
    pageResp.setList(list);
    return pageResp;
  }

  public void delete(Long id) {
    confirmOrderMapper.deleteByPrimaryKey(id);
  }
}
