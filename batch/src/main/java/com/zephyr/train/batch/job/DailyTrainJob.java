package com.zephyr.train.batch.job;

import cn.hutool.core.util.RandomUtil;
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

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    // Add Log ID
    MDC.put("LOG_ID", System.currentTimeMillis() + RandomUtil.randomString(3));
    LOG.info("Start to generate daily train data");
    LOG.info("Generate daily train data completed");
  }
}
