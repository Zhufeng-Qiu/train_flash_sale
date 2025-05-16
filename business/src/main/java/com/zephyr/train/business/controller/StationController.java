package com.zephyr.train.business.controller;

import com.zephyr.train.business.resp.StationQueryResp;
import com.zephyr.train.business.service.StationService;
import com.zephyr.train.common.resp.CommonResp;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/station")
public class StationController {

  @Resource
  private StationService stationService;

  @GetMapping("/query-all")
  public CommonResp<List<StationQueryResp>> queryList() {
    List<StationQueryResp> list = stationService.queryAll();
    return new CommonResp<>(list);
  }
}
