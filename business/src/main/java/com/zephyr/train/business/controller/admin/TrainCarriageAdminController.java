package com.zephyr.train.business.controller.admin;

import com.zephyr.train.common.resp.CommonResp;
import com.zephyr.train.common.resp.PageResp;
import com.zephyr.train.business.req.TrainCarriageQueryReq;
import com.zephyr.train.business.req.TrainCarriageSaveReq;
import com.zephyr.train.business.resp.TrainCarriageQueryResp;
import com.zephyr.train.business.service.TrainCarriageService;
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
@RequestMapping("/admin/train-carriage")
public class TrainCarriageAdminController {

  @Resource
  private TrainCarriageService trainCarriageService;

  @PostMapping("/save")
  public CommonResp<Object> save(@Valid @RequestBody TrainCarriageSaveReq req) {
    trainCarriageService.save(req);
    return new CommonResp<>();
  }

  @GetMapping("/query-list")
  public CommonResp<PageResp<TrainCarriageQueryResp>> queryList(@Valid TrainCarriageQueryReq req) {
    PageResp<TrainCarriageQueryResp> list = trainCarriageService.queryList(req);
    return new CommonResp<>(list);
  }

  @DeleteMapping("/delete/{id}")
  public CommonResp<Object> delete(@PathVariable Long id) {
    trainCarriageService.delete(id);
    return new CommonResp<>();
  }
}
