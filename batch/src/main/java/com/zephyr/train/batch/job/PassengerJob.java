package com.zephyr.train.batch.job;

import cn.hutool.core.util.RandomUtil;
import com.zephyr.train.batch.feign.MemberFeign;
import jakarta.annotation.Resource;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@DisallowConcurrentExecution
public class PassengerJob implements Job {

  private static final Logger LOG = LoggerFactory.getLogger(PassengerJob.class);

  @Resource
  MemberFeign memberFeign;

  /**
   * Initialize passenger, prevent the passengers from being deleted for prod version
   * @param jobExecutionContext
   * @throws JobExecutionException
   */
  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    // Add log id
    MDC.put("LOG_ID", System.currentTimeMillis() + RandomUtil.randomString(3));
    LOG.info("Initialization of passenger starts");
    memberFeign.init();
    LOG.info("Initialization of passenger ends");
  }
}
