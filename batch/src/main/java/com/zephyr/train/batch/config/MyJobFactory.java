package com.zephyr.train.batch.config;

import jakarta.annotation.Resource;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.stereotype.Component;

@Component
public class MyJobFactory extends SpringBeanJobFactory {

  @Resource
  private AutowireCapableBeanFactory beanFactory;

  /**
   * Override createJobInstance method in super class, and autowire its created class
   */
  @Override
  protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
    Object jobInstance = super.createJobInstance(bundle);
    beanFactory.autowireBean(jobInstance);
    return jobInstance;
  }
}
