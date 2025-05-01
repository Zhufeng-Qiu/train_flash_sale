package com.zephyr.train.member.controller;

import com.zephyr.train.common.resp.CommonResp;
import com.zephyr.train.member.req.MemberLoginReq;
import com.zephyr.train.member.req.MemberRegisterReq;
import com.zephyr.train.member.req.MemberSendCodeReq;
import com.zephyr.train.member.resp.MemberLoginResp;
import com.zephyr.train.member.service.MemberService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
public class MemberController {

  @Resource
  private MemberService memberService;

  @GetMapping("/count")
  public CommonResp<Integer> count() {
    int count = memberService.count();
    CommonResp<Integer> commonResp = new CommonResp<>();
    commonResp.setContent(count);
    return commonResp;
  }

  @PostMapping("/register")
  public CommonResp<Long> register(@Valid MemberRegisterReq req) {
    long register = memberService.register(req);
    CommonResp<Long> commonResp = new CommonResp<>();
    commonResp.setContent(register);
    return commonResp;
  }

  @PostMapping("/send-code")
  public CommonResp<Long> sendCode(@Valid @RequestBody MemberSendCodeReq req) {
    memberService.sendCode(req);
    CommonResp<Long> commonResp = new CommonResp<>();
    return commonResp;
  }

  @PostMapping("/login")
  public CommonResp<MemberLoginResp> login(@Valid MemberLoginReq req) {
    MemberLoginResp resp = memberService.login(req);
    return new CommonResp<>(resp);
  }
}
