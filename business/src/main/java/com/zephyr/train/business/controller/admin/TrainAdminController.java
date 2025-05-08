package com.zephyr.train.business.controller.admin;

import com.zephyr.train.business.req.TrainQueryReq;
import com.zephyr.train.business.req.TrainSaveReq;
import com.zephyr.train.business.resp.TrainQueryResp;
import com.zephyr.train.business.service.TrainService;
import com.zephyr.train.common.resp.CommonResp;
import com.zephyr.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/train")
public class TrainAdminController {

  @Resource
  private TrainService trainService;

  @PostMapping("/save")
  public CommonResp<Object> save(@Valid @RequestBody TrainSaveReq req) {
    trainService.save(req);
    return new CommonResp<>();
  }

  @GetMapping("/query-list")
  public CommonResp<PageResp<TrainQueryResp>> queryList(@Valid TrainQueryReq req) {
    PageResp<TrainQueryResp> list = trainService.queryList(req);
    return new CommonResp<>(list);
  }

  @DeleteMapping("/delete/{id}")
  public CommonResp<Object> delete(@PathVariable Long id) {
    trainService.delete(id);
    return new CommonResp<>();
  }

  @GetMapping("/query-all")
  public CommonResp<List<TrainQueryResp>> queryList() {
    List<TrainQueryResp> list = trainService.queryAll();
    return new CommonResp<>(list);
  }
}
