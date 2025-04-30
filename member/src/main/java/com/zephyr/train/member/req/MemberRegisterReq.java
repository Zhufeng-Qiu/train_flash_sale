package com.zephyr.train.member.req;

import jakarta.validation.constraints.NotBlank;

public class MemberRegisterReq {
  @NotBlank(message="mobile cannot be blank")
  private String mobile;

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  @Override
  public String toString() {
    return "MemberRegisterReq{" +
        "mobile='" + mobile + '\'' +
        '}';
  }
}
