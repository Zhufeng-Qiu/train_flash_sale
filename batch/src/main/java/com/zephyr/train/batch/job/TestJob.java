package com.zephyr.train.batch.job;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@DisallowConcurrentExecution
public class TestJob implements Job {
  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    System.out.println("TestJob1111111 TEST starts");
//    try {
//      Thread.sleep(3000);
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    }
    System.out.println("TestJob1111111 TEST ends");
  }
}
