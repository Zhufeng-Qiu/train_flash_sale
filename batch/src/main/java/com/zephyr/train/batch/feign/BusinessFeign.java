package com.zephyr.train.batch.feign;

import com.zephyr.train.common.resp.CommonResp;
import java.util.Date;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//@FeignClient(name = "business", url = "http://127.0.0.1:8002/business")
@FeignClient("business")
public interface BusinessFeign {

  @GetMapping("/business/hello")
  String hello();

  @GetMapping("/business/admin/daily-train/gen-daily/{date}")
  CommonResp<Object> genDaily(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date);
}