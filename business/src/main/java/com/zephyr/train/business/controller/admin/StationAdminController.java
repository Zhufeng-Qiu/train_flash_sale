package com.zephyr.train.business.controller.admin;

import com.zephyr.train.business.req.StationQueryReq;
import com.zephyr.train.business.req.StationSaveReq;
import com.zephyr.train.business.resp.StationQueryResp;
import com.zephyr.train.business.service.StationService;
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
@RequestMapping("/admin/station")
public class StationAdminController {

  @Resource
  private StationService stationService;

  @PostMapping("/save")
  public CommonResp<Object> save(@Valid @RequestBody StationSaveReq req) {
    stationService.save(req);
    return new CommonResp<>();
  }

  @GetMapping("/query-list")
  public CommonResp<PageResp<StationQueryResp>> queryList(@Valid StationQueryReq req) {
    PageResp<StationQueryResp> list = stationService.queryList(req);
    return new CommonResp<>(list);
  }

  @DeleteMapping("/delete/{id}")
  public CommonResp<Object> delete(@PathVariable Long id) {
    stationService.delete(id);
    return new CommonResp<>();
  }

  @GetMapping("/query-all")
  public CommonResp<List<StationQueryResp>> queryList() {
    List<StationQueryResp> list = stationService.queryAll();
    return new CommonResp<>(list);
  }
}
