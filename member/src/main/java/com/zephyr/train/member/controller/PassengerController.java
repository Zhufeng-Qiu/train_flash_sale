package com.zephyr.train.member.controller;

import com.zephyr.train.common.context.LoginMemberContext;
import com.zephyr.train.common.resp.CommonResp;
import com.zephyr.train.member.req.PassengerQueryReq;
import com.zephyr.train.member.req.PassengerSaveReq;
import com.zephyr.train.member.resp.PassengerQueryResp;
import com.zephyr.train.member.service.PassengerService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
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
  public CommonResp<Object> queryList(@Valid PassengerQueryReq req) {
    req.setMemberId(LoginMemberContext.getId());
    List<PassengerQueryResp> list = passengerService.queryList(req);
    return new CommonResp<>(list);
  }
}
