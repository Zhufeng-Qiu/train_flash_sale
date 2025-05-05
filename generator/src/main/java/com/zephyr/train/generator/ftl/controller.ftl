package com.zephyr.train.member.controller;

import com.zephyr.train.common.context.LoginMemberContext;
import com.zephyr.train.common.resp.CommonResp;
import com.zephyr.train.common.resp.PageResp;
import com.zephyr.train.member.req.${Domain}QueryReq;
import com.zephyr.train.member.req.${Domain}SaveReq;
import com.zephyr.train.member.resp.${Domain}QueryResp;
import com.zephyr.train.member.service.${Domain}Service;
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
@RequestMapping("/passenger")
public class ${Domain}Controller {

  @Resource
  private ${Domain}Service ${domain}Service;

  @PostMapping("/save")
  public CommonResp<Object> save(@Valid @RequestBody ${Domain}SaveReq req) {
    ${domain}Service.save(req);
    return new CommonResp<>();
  }

  @GetMapping("/query-list")
  public CommonResp<PageResp<${Domain}QueryResp>> queryList(@Valid ${Domain}QueryReq req) {
    req.setMemberId(LoginMemberContext.getId());
    PageResp<${Domain}QueryResp> list = ${domain}Service.queryList(req);
    return new CommonResp<>(list);
  }

  @DeleteMapping("/delete/{id}")
  public CommonResp<Object> delete(@PathVariable Long id) {
    ${domain}Service.delete(id);
    return new CommonResp<>();
  }
}
