package com.zephyr.train.business.controller.admin;

import com.zephyr.train.common.resp.CommonResp;
import com.zephyr.train.common.resp.PageResp;
import com.zephyr.train.business.req.SkTokenQueryReq;
import com.zephyr.train.business.req.SkTokenSaveReq;
import com.zephyr.train.business.resp.SkTokenQueryResp;
import com.zephyr.train.business.service.SkTokenService;
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
@RequestMapping("/admin/sk-token")
public class SkTokenAdminController {

  @Resource
  private SkTokenService skTokenService;

  @PostMapping("/save")
  public CommonResp<Object> save(@Valid @RequestBody SkTokenSaveReq req) {
    skTokenService.save(req);
    return new CommonResp<>();
  }

  @GetMapping("/query-list")
  public CommonResp<PageResp<SkTokenQueryResp>> queryList(@Valid SkTokenQueryReq req) {
    PageResp<SkTokenQueryResp> list = skTokenService.queryList(req);
    return new CommonResp<>(list);
  }

  @DeleteMapping("/delete/{id}")
  public CommonResp<Object> delete(@PathVariable Long id) {
    skTokenService.delete(id);
    return new CommonResp<>();
  }
}
