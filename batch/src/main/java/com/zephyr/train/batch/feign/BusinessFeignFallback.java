package com.zephyr.train.batch.feign;

import com.zephyr.train.common.resp.CommonResp;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class BusinessFeignFallback implements BusinessFeign {
  @Override
  public String hello() {
    return "Fallback";
  }

  @Override
  public CommonResp<Object> genDaily(Date date) {
    return null;
  }
}
