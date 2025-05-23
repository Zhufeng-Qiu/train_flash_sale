package com.zephyr.train.business.controller.admin;

import com.zephyr.train.common.resp.CommonResp;
import com.zephyr.train.common.resp.PageResp;
import com.zephyr.train.business.req.ConfirmOrderQueryReq;
import com.zephyr.train.business.req.ConfirmOrderDoReq;
import com.zephyr.train.business.resp.ConfirmOrderQueryResp;
import com.zephyr.train.business.service.ConfirmOrderService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/confirm-order")
public class ConfirmOrderAdminController {

  @Resource
  private ConfirmOrderService confirmOrderService;

  @PostMapping("/save")
  public CommonResp<Object> save(@Valid @RequestBody ConfirmOrderDoReq req) {
    confirmOrderService.save(req);
    return new CommonResp<>();
  }

  @GetMapping("/query-list")
  public CommonResp<PageResp<ConfirmOrderQueryResp>> queryList(@Valid ConfirmOrderQueryReq req) {
    PageResp<ConfirmOrderQueryResp> list = confirmOrderService.queryList(req);
    return new CommonResp<>(list);
  }

  @DeleteMapping("/delete/{id}")
  public CommonResp<Object> delete(@PathVariable Long id) {
    confirmOrderService.delete(id);
    return new CommonResp<>();
  }
}
