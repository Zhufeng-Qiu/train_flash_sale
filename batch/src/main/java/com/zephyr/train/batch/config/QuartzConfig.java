package com.zephyr.train.batch.config;

import com.zephyr.train.batch.job.TestJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

  /**
   * Declare a job
   * @return
   */
  @Bean
  public JobDetail jobDetail() {
    return JobBuilder.newJob(TestJob.class)
        .withIdentity("TestJob", "test")
        .storeDurably()
        .build();
  }

  /**
   * Declare a trigger to specify when the job should be executed
   * @return
   */
  @Bean
  public Trigger trigger() {
    return TriggerBuilder.newTrigger()
        .forJob(jobDetail())
        .withIdentity("trigger", "trigger")
        .startNow()
        .withSchedule(CronScheduleBuilder.cronSchedule("*/2 * * * * ?"))
        .build();
  }
}
