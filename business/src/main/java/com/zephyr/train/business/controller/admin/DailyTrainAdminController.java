package com.zephyr.train.business.controller.admin;

import com.zephyr.train.common.resp.CommonResp;
import com.zephyr.train.common.resp.PageResp;
import com.zephyr.train.business.req.DailyTrainQueryReq;
import com.zephyr.train.business.req.DailyTrainSaveReq;
import com.zephyr.train.business.resp.DailyTrainQueryResp;
import com.zephyr.train.business.service.DailyTrainService;
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
@RequestMapping("/admin/daily-train")
public class DailyTrainAdminController {

  @Resource
  private DailyTrainService dailyTrainService;

  @PostMapping("/save")
  public CommonResp<Object> save(@Valid @RequestBody DailyTrainSaveReq req) {
    dailyTrainService.save(req);
    return new CommonResp<>();
  }

  @GetMapping("/query-list")
  public CommonResp<PageResp<DailyTrainQueryResp>> queryList(@Valid DailyTrainQueryReq req) {
    PageResp<DailyTrainQueryResp> list = dailyTrainService.queryList(req);
    return new CommonResp<>(list);
  }

  @DeleteMapping("/delete/{id}")
  public CommonResp<Object> delete(@PathVariable Long id) {
    dailyTrainService.delete(id);
    return new CommonResp<>();
  }
}
