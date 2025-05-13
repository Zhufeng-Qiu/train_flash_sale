package com.zephyr.train.batch.job;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.zephyr.train.batch.feign.BusinessFeign;
import com.zephyr.train.common.resp.CommonResp;
import jakarta.annotation.Resource;
import java.util.Date;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@DisallowConcurrentExecution
public class DailyTrainJob implements Job {

  private static final Logger LOG = LoggerFactory.getLogger(DailyTrainJob.class);

  @Resource
  BusinessFeign businessFeign;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    // Add Log ID
    MDC.put("LOG_ID", System.currentTimeMillis() + RandomUtil.randomString(3));
    LOG.info("Start to generate daily train data 15 days later");
    Date date = new Date();
    DateTime dateTime = DateUtil.offsetDay(date, 15);
    Date offsetDate = dateTime.toJdkDate();
    CommonResp<Object> commonResp = businessFeign.genDaily(offsetDate);
    LOG.info("Generate daily train data completed, result: {}", commonResp);
  }
}
