package com.zephyr.train.business.feign;

import com.zephyr.train.common.req.MemberTicketReq;
import com.zephyr.train.common.resp.CommonResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

// @FeignClient("member")
@FeignClient(name = "member", url = "http://127.0.0.1:8001")
public interface MemberFeign {

  @GetMapping("/member/feign/ticket/save")
  CommonResp<Object> save(@RequestBody MemberTicketReq req);

}
