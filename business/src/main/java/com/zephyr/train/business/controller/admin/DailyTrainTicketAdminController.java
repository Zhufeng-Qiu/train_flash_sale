package com.zephyr.train.business.controller.admin;

import com.zephyr.train.common.resp.CommonResp;
import com.zephyr.train.common.resp.PageResp;
import com.zephyr.train.business.req.DailyTrainTicketQueryReq;
import com.zephyr.train.business.req.DailyTrainTicketSaveReq;
import com.zephyr.train.business.resp.DailyTrainTicketQueryResp;
import com.zephyr.train.business.service.DailyTrainTicketService;
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
@RequestMapping("/admin/daily-train-ticket")
public class DailyTrainTicketAdminController {

  @Resource
  private DailyTrainTicketService dailyTrainTicketService;

  @PostMapping("/save")
  public CommonResp<Object> save(@Valid @RequestBody DailyTrainTicketSaveReq req) {
    dailyTrainTicketService.save(req);
    return new CommonResp<>();
  }

  @GetMapping("/query-list")
  public CommonResp<PageResp<DailyTrainTicketQueryResp>> queryList(@Valid DailyTrainTicketQueryReq req) {
    PageResp<DailyTrainTicketQueryResp> list = dailyTrainTicketService.queryList(req);
    return new CommonResp<>(list);
  }

  @DeleteMapping("/delete/{id}")
  public CommonResp<Object> delete(@PathVariable Long id) {
    dailyTrainTicketService.delete(id);
    return new CommonResp<>();
  }
}
