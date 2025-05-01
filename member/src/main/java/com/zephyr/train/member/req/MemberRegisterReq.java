package com.zephyr.train.member.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class MemberRegisterReq {
  @NotBlank(message="mobile cannot be blank")
  @Pattern(regexp = "^(\\+1\\s?)?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}$", message = "Mobile format is not correct")
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
