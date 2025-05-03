package com.zephyr.train.member.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class MemberLoginReq {
  @NotBlank(message = "[Mobile] cannot be blank")
  @Pattern(regexp = "^(\\+1\\s?)?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}$", message = "Mobile format is not correct")
  private String mobile;

  @NotBlank(message = "[Verification code] cannot be blank")
  private String code;

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("MemberLoginReq{");
    sb.append("mobile='").append(mobile).append('\'');
    sb.append(", code='").append(code).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
