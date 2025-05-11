package com.zephyr.train.business.controller.admin;

import com.zephyr.train.common.resp.CommonResp;
import com.zephyr.train.common.resp.PageResp;
import com.zephyr.train.business.req.DailyTrainStationQueryReq;
import com.zephyr.train.business.req.DailyTrainStationSaveReq;
import com.zephyr.train.business.resp.DailyTrainStationQueryResp;
import com.zephyr.train.business.service.DailyTrainStationService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.bouncycastle.cert.ocsp.Req;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/daily-train-station")
public class DailyTrainStationAdminController {

  @Resource
  private DailyTrainStationService dailyTrainStationService;

  @PostMapping("/save")
  public CommonResp<Object> save(@Valid @RequestBody DailyTrainStationSaveReq req) {
    dailyTrainStationService.save(req);
    return new CommonResp<>();
  }

  @GetMapping("/query-list")
  public CommonResp<PageResp<DailyTrainStationQueryResp>> queryList(@Valid DailyTrainStationQueryReq req) {
    PageResp<DailyTrainStationQueryResp> list = dailyTrainStationService.queryList(req);
    return new CommonResp<>(list);
  }

  @DeleteMapping("/delete/{id}")
  public CommonResp<Object> delete(@PathVariable Long id) {
    dailyTrainStationService.delete(id);
    return new CommonResp<>();
  }
}
