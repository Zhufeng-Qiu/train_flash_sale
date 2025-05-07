package com.zephyr.train.business.controller.admin;

import com.zephyr.train.common.resp.CommonResp;
import com.zephyr.train.common.resp.PageResp;
import com.zephyr.train.business.req.TrainStationQueryReq;
import com.zephyr.train.business.req.TrainStationSaveReq;
import com.zephyr.train.business.resp.TrainStationQueryResp;
import com.zephyr.train.business.service.TrainStationService;
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
@RequestMapping("/admin/train-station")
public class TrainStationAdminController {

  @Resource
  private TrainStationService trainStationService;

  @PostMapping("/save")
  public CommonResp<Object> save(@Valid @RequestBody TrainStationSaveReq req) {
    trainStationService.save(req);
    return new CommonResp<>();
  }

  @GetMapping("/query-list")
  public CommonResp<PageResp<TrainStationQueryResp>> queryList(@Valid TrainStationQueryReq req) {
    PageResp<TrainStationQueryResp> list = trainStationService.queryList(req);
    return new CommonResp<>(list);
  }

  @DeleteMapping("/delete/{id}")
  public CommonResp<Object> delete(@PathVariable Long id) {
    trainStationService.delete(id);
    return new CommonResp<>();
  }
}
