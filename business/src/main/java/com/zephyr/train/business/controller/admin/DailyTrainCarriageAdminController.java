package com.zephyr.train.business.controller.admin;

import com.zephyr.train.common.resp.CommonResp;
import com.zephyr.train.common.resp.PageResp;
import com.zephyr.train.business.req.DailyTrainCarriageQueryReq;
import com.zephyr.train.business.req.DailyTrainCarriageSaveReq;
import com.zephyr.train.business.resp.DailyTrainCarriageQueryResp;
import com.zephyr.train.business.service.DailyTrainCarriageService;
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
@RequestMapping("/admin/daily-train-carriage")
public class DailyTrainCarriageAdminController {

  @Resource
  private DailyTrainCarriageService dailyTrainCarriageService;

  @PostMapping("/save")
  public CommonResp<Object> save(@Valid @RequestBody DailyTrainCarriageSaveReq req) {
    dailyTrainCarriageService.save(req);
    return new CommonResp<>();
  }

  @GetMapping("/query-list")
  public CommonResp<PageResp<DailyTrainCarriageQueryResp>> queryList(@Valid DailyTrainCarriageQueryReq req) {
    PageResp<DailyTrainCarriageQueryResp> list = dailyTrainCarriageService.queryList(req);
    return new CommonResp<>(list);
  }

  @DeleteMapping("/delete/{id}")
  public CommonResp<Object> delete(@PathVariable Long id) {
    dailyTrainCarriageService.delete(id);
    return new CommonResp<>();
  }
}
