package com.zephyr.train.${module}.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zephyr.train.common.resp.PageResp;
import com.zephyr.train.common.util.SnowUtil;
import com.zephyr.train.${module}.domain.${Domain};
import com.zephyr.train.${module}.domain.${Domain}Example;
import com.zephyr.train.${module}.mapper.${Domain}Mapper;
import com.zephyr.train.${module}.req.${Domain}QueryReq;
import com.zephyr.train.${module}.req.${Domain}SaveReq;
import com.zephyr.train.${module}.resp.${Domain}QueryResp;
import jakarta.annotation.Resource;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ${Domain}Service {

  private static final Logger LOG = LoggerFactory.getLogger(${Domain}Service.class);

  @Resource
  private ${Domain}Mapper ${domain}Mapper;

  public void save(${Domain}SaveReq req) {
    DateTime now = DateTime.now();
    ${Domain} ${domain} = BeanUtil.copyProperties(req, ${Domain}.class);
    if (ObjectUtil.isNull(${domain}.getId())) {
      ${domain}.setId(SnowUtil.getSnowflakeNextId());
      ${domain}.setCreateTime(now);
      ${domain}.setUpdateTime(now);
      ${domain}Mapper.insert(${domain});
    } else {
      ${domain}.setUpdateTime(now);
      ${domain}Mapper.updateByPrimaryKey(${domain});
    }
  }

  public PageResp<${Domain}QueryResp> queryList(${Domain}QueryReq req) {
    ${Domain}Example ${domain}Example = new ${Domain}Example();
    ${domain}Example.setOrderByClause("id desc");
    ${Domain}Example.Criteria criteria = ${domain}Example.createCriteria();

    LOG.info("Query page: {}", req.getPage());
    LOG.info("Query page size: {}", req.getSize());
    PageHelper.startPage(req.getPage(), req.getSize());
    List<${Domain}> ${domain}List = ${domain}Mapper.selectByExample(${domain}Example);

    PageInfo<${Domain}> pageInfo = new PageInfo<>(${domain}List);
    LOG.info("Total number of records: {}", pageInfo.getTotal());
    LOG.info("Total number of pages: {}", pageInfo.getPages());

    List<${Domain}QueryResp> list = BeanUtil.copyToList(${domain}List, ${Domain}QueryResp.class);
    PageResp<${Domain}QueryResp> pageResp = new PageResp<>();
    pageResp.setTotal(pageInfo.getTotal());
    pageResp.setList(list);
    return pageResp;
  }

  public void delete(Long id) {
    ${domain}Mapper.deleteByPrimaryKey(id);
  }
}
