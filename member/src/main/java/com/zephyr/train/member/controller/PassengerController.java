package com.zephyr.train.member.controller;

import com.zephyr.train.common.context.LoginMemberContext;
import com.zephyr.train.common.resp.CommonResp;
import com.zephyr.train.common.resp.PageResp;
import com.zephyr.train.member.req.PassengerQueryReq;
import com.zephyr.train.member.req.PassengerSaveReq;
import com.zephyr.train.member.resp.PassengerQueryResp;
import com.zephyr.train.member.service.PassengerService;
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
@RequestMapping("/passenger")
public class PassengerController {

  @Resource
  private PassengerService passengerService;

  @PostMapping("/save")
  public CommonResp<Object> save(@Valid @RequestBody PassengerSaveReq req) {
    passengerService.save(req);
    return new CommonResp<>();
  }

  @GetMapping("/query-list")
  public CommonResp<PageResp<PassengerQueryResp>> queryList(@Valid PassengerQueryReq req) {
    req.setMemberId(LoginMemberContext.getId());
    PageResp<PassengerQueryResp> list = passengerService.queryList(req);
    return new CommonResp<>(list);
  }

  @DeleteMapping("/delete/{id}")
  public CommonResp<Object> delete(@PathVariable Long id) {
    passengerService.delete(id);
    return new CommonResp<>();
  }

  @GetMapping("/query-mine")
  public CommonResp<List<PassengerQueryResp>> queryMine() {
    List<PassengerQueryResp> list = passengerService.queryMine();
    return new CommonResp<>(list);
  }
}
