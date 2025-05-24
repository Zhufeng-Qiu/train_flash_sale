package com.zephyr.train.business.controller;

import com.zephyr.train.business.req.DailyTrainTicketQueryReq;
import com.zephyr.train.business.resp.DailyTrainTicketQueryResp;
import com.zephyr.train.business.service.DailyTrainTicketService;
import com.zephyr.train.common.resp.CommonResp;
import com.zephyr.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/daily-train-ticket")
public class DailyTrainTicketController {

  @Resource
  private DailyTrainTicketService dailyTrainTicketService;

  @GetMapping("/query-list2")
  public CommonResp<PageResp<DailyTrainTicketQueryResp>> queryList2(@Valid DailyTrainTicketQueryReq req) {
    PageResp<DailyTrainTicketQueryResp> list = dailyTrainTicketService.queryList2(req);
    return new CommonResp<>(list);
  }

  @GetMapping("/query-list")
  public CommonResp<PageResp<DailyTrainTicketQueryResp>> queryList(@Valid DailyTrainTicketQueryReq req) {
    PageResp<DailyTrainTicketQueryResp> list = dailyTrainTicketService.queryList(req);
    return new CommonResp<>(list);
  }
}
