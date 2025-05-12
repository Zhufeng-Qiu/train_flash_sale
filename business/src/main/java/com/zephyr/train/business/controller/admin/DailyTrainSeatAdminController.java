package com.zephyr.train.business.controller.admin;

import com.zephyr.train.common.resp.CommonResp;
import com.zephyr.train.common.resp.PageResp;
import com.zephyr.train.business.req.DailyTrainSeatQueryReq;
import com.zephyr.train.business.req.DailyTrainSeatSaveReq;
import com.zephyr.train.business.resp.DailyTrainSeatQueryResp;
import com.zephyr.train.business.service.DailyTrainSeatService;
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
@RequestMapping("/admin/daily-train-seat")
public class DailyTrainSeatAdminController {

  @Resource
  private DailyTrainSeatService dailyTrainSeatService;

  @PostMapping("/save")
  public CommonResp<Object> save(@Valid @RequestBody DailyTrainSeatSaveReq req) {
    dailyTrainSeatService.save(req);
    return new CommonResp<>();
  }

  @GetMapping("/query-list")
  public CommonResp<PageResp<DailyTrainSeatQueryResp>> queryList(@Valid DailyTrainSeatQueryReq req) {
    PageResp<DailyTrainSeatQueryResp> list = dailyTrainSeatService.queryList(req);
    return new CommonResp<>(list);
  }

  @DeleteMapping("/delete/{id}")
  public CommonResp<Object> delete(@PathVariable Long id) {
    dailyTrainSeatService.delete(id);
    return new CommonResp<>();
  }
}
