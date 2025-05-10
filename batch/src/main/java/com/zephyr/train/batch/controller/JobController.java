package com.zephyr.train.batch.controller;

import com.zephyr.train.batch.req.CronJobReq;
import com.zephyr.train.batch.resp.CronJobResp;
import com.zephyr.train.common.resp.CommonResp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/admin/job")
public class JobController {

  private static Logger LOG = LoggerFactory.getLogger(JobController.class);

  @Autowired
  private SchedulerFactoryBean schedulerFactoryBean;

  @RequestMapping(value = "/add")
  public CommonResp add(@RequestBody CronJobReq cronJobReq) {
    String jobClassName = cronJobReq.getName();
    String jobGroupName = cronJobReq.getGroup();
    String cronExpression = cronJobReq.getCronExpression();
    String description = cronJobReq.getDescription();
    LOG.info("Start to create scheduled job: {}，{}，{}，{}", jobClassName, jobGroupName, cronExpression, description);
    CommonResp commonResp = new CommonResp();

    try {
      // Get scheduler instance via SchedulerFactory
      Scheduler sched = schedulerFactoryBean.getScheduler();

      // Start scheduler
      sched.start();

      // Construct job detail
      JobDetail jobDetail = JobBuilder.newJob((Class<? extends Job>) Class.forName(jobClassName)).withIdentity(jobClassName, jobGroupName).build();

      // Create cron schedule builder (job duration)
      CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);

      // Create a new trigger using new cron expression
      CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobClassName, jobGroupName).withDescription(description).withSchedule(scheduleBuilder).build();

      sched.scheduleJob(jobDetail, trigger);

    } catch (SchedulerException e) {
      LOG.error("Fail to create scheduled job: " + e);
      commonResp.setSuccess(false);
      commonResp.setMessage("Fail to create scheduled job: schedule exception");
    } catch (ClassNotFoundException e) {
      LOG.error("Fail to create scheduled job: " + e);
      commonResp.setSuccess(false);
      commonResp.setMessage("Fail to create scheduled job：job class does not exist");
    }
    LOG.info("Create scheduled job completed: {}", commonResp);
    return commonResp;
  }

  @RequestMapping(value = "/pause")
  public CommonResp pause(@RequestBody CronJobReq cronJobReq) {
    String jobClassName = cronJobReq.getName();
    String jobGroupName = cronJobReq.getGroup();
    LOG.info("Start to pause scheduled job: {}，{}", jobClassName, jobGroupName);
    CommonResp commonResp = new CommonResp();
    try {
      Scheduler sched = schedulerFactoryBean.getScheduler();
      sched.pauseJob(JobKey.jobKey(jobClassName, jobGroupName));
    } catch (SchedulerException e) {
      LOG.error("Fail to pause scheduled job: " + e);
      commonResp.setSuccess(false);
      commonResp.setMessage("Fail to pause scheduled job: schedule exception");
    }
    LOG.info("Pause scheduled job completed: {}", commonResp);
    return commonResp;
  }

  @RequestMapping(value = "/resume")
  public CommonResp resume(@RequestBody CronJobReq cronJobReq) {
    String jobClassName = cronJobReq.getName();
    String jobGroupName = cronJobReq.getGroup();
    LOG.info("Start to resume scheduled job: {}，{}", jobClassName, jobGroupName);
    CommonResp commonResp = new CommonResp();
    try {
      Scheduler sched = schedulerFactoryBean.getScheduler();
      sched.resumeJob(JobKey.jobKey(jobClassName, jobGroupName));
    } catch (SchedulerException e) {
      LOG.error("Fail to resume scheduled job: " + e);
      commonResp.setSuccess(false);
      commonResp.setMessage("Fail to resume scheduled job: schedule exception");
    }
    LOG.info("Resume scheduled job completed: {}", commonResp);
    return commonResp;
  }

  @RequestMapping(value = "/reschedule")
  public CommonResp reschedule(@RequestBody CronJobReq cronJobReq) {
    String jobClassName = cronJobReq.getName();
    String jobGroupName = cronJobReq.getGroup();
    String cronExpression = cronJobReq.getCronExpression();
    String description = cronJobReq.getDescription();
    LOG.info("Start to reschedule scheduled job: {}，{}，{}，{}", jobClassName, jobGroupName, cronExpression, description);
    CommonResp commonResp = new CommonResp();
    try {
      Scheduler scheduler = schedulerFactoryBean.getScheduler();
      TriggerKey triggerKey = TriggerKey.triggerKey(jobClassName, jobGroupName);
      // Cron scheduler builder
      CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
      CronTriggerImpl trigger1 = (CronTriggerImpl) scheduler.getTrigger(triggerKey);
      trigger1.setStartTime(new Date()); // reset start time
      CronTrigger trigger = trigger1;

      // Create a new trigger using new cron expression
      trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withDescription(description).withSchedule(scheduleBuilder).build();

      // Set scheduled job using new trigger
      scheduler.rescheduleJob(triggerKey, trigger);
    } catch (Exception e) {
      LOG.error("Fail to reschedule scheduled job: " + e);
      commonResp.setSuccess(false);
      commonResp.setMessage("Fail to reschedule scheduled job: schedule exception");
    }
    LOG.info("Reschedule scheduled job completed: {}", commonResp);
    return commonResp;
  }

  @RequestMapping(value = "/delete")
  public CommonResp delete(@RequestBody CronJobReq cronJobReq) {
    String jobClassName = cronJobReq.getName();
    String jobGroupName = cronJobReq.getGroup();
    LOG.info("Start to delete scheduled job: {}，{}", jobClassName, jobGroupName);
    CommonResp commonResp = new CommonResp();
    try {
      Scheduler scheduler = schedulerFactoryBean.getScheduler();
      scheduler.pauseTrigger(TriggerKey.triggerKey(jobClassName, jobGroupName));
      scheduler.unscheduleJob(TriggerKey.triggerKey(jobClassName, jobGroupName));
      scheduler.deleteJob(JobKey.jobKey(jobClassName, jobGroupName));
    } catch (SchedulerException e) {
      LOG.error("Fail to delete scheduled job: " + e);
      commonResp.setSuccess(false);
      commonResp.setMessage("Fail to delete scheduled job: schedule exception");
    }
    LOG.info("Delete scheduled job completed: {}", commonResp);
    return commonResp;
  }

  @RequestMapping(value="/query")
  public CommonResp query() {
    LOG.info("Start to retrieve all the scheduled job");
    CommonResp commonResp = new CommonResp();
    List<CronJobResp> cronJobDtoList = new ArrayList();
    try {
      Scheduler scheduler = schedulerFactoryBean.getScheduler();
      for (String groupName : scheduler.getJobGroupNames()) {
        for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
          CronJobResp cronJobResp = new CronJobResp();
          cronJobResp.setName(jobKey.getName());
          cronJobResp.setGroup(jobKey.getGroup());

          //get job's trigger
          List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
          CronTrigger cronTrigger = (CronTrigger) triggers.get(0);
          cronJobResp.setNextFireTime(cronTrigger.getNextFireTime());
          cronJobResp.setPreFireTime(cronTrigger.getPreviousFireTime());
          cronJobResp.setCronExpression(cronTrigger.getCronExpression());
          cronJobResp.setDescription(cronTrigger.getDescription());
          Trigger.TriggerState triggerState = scheduler.getTriggerState(cronTrigger.getKey());
          cronJobResp.setState(triggerState.name());

          cronJobDtoList.add(cronJobResp);
        }

      }
    } catch (SchedulerException e) {
      LOG.error("Fail to retrieve all the scheduled job: " + e);
      commonResp.setSuccess(false);
      commonResp.setMessage("Fail to retrieve all the scheduled job: schedule exception");
    }
    commonResp.setContent(cronJobDtoList);
    LOG.info("Retrieve all the scheduled job comopleted: {}", commonResp);
    return commonResp;
  }

}
