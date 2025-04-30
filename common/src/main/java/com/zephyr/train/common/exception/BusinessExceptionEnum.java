package com.zephyr.train.common.exception;

public enum BusinessExceptionEnum {
  MEMBER_MOBILE_REGISTERED("Mobile has been registered");

  private String desc;

  BusinessExceptionEnum(String desc) {
    this.desc = desc;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  @Override
  public String toString() {
    return "BusinessExceptionEnum{" +
        "desc='" + desc + '\'' +
        '}';
  }
}
