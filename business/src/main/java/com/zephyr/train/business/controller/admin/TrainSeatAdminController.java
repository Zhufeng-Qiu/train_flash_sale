package com.zephyr.train.business.controller.admin;

import com.zephyr.train.common.resp.CommonResp;
import com.zephyr.train.common.resp.PageResp;
import com.zephyr.train.business.req.TrainSeatQueryReq;
import com.zephyr.train.business.req.TrainSeatSaveReq;
import com.zephyr.train.business.resp.TrainSeatQueryResp;
import com.zephyr.train.business.service.TrainSeatService;
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
@RequestMapping("/admin/train-seat")
public class TrainSeatAdminController {

  @Resource
  private TrainSeatService trainSeatService;

  @PostMapping("/save")
  public CommonResp<Object> save(@Valid @RequestBody TrainSeatSaveReq req) {
    trainSeatService.save(req);
    return new CommonResp<>();
  }

  @GetMapping("/query-list")
  public CommonResp<PageResp<TrainSeatQueryResp>> queryList(@Valid TrainSeatQueryReq req) {
    PageResp<TrainSeatQueryResp> list = trainSeatService.queryList(req);
    return new CommonResp<>(list);
  }

  @DeleteMapping("/delete/{id}")
  public CommonResp<Object> delete(@PathVariable Long id) {
    trainSeatService.delete(id);
    return new CommonResp<>();
  }
}
