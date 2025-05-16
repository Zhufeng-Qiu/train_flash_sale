package com.zephyr.train.business.controller;

import com.zephyr.train.business.resp.TrainQueryResp;
import com.zephyr.train.business.service.TrainSeatService;
import com.zephyr.train.business.service.TrainService;
import com.zephyr.train.common.resp.CommonResp;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/train")
public class TrainController {

  @Resource
  private TrainService trainService;

  @Resource
  private TrainSeatService trainSeatService;

  @GetMapping("/query-all")
  public CommonResp<List<TrainQueryResp>> queryList() {
    List<TrainQueryResp> list = trainService.queryAll();
    return new CommonResp<>(list);
  }
}
