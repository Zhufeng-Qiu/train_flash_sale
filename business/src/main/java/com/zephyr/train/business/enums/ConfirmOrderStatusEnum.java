package com.zephyr.train.business.enums;

public enum ConfirmOrderStatusEnum {

  INIT("I", "Initial"),
  PENDING("P", "Processing"),
  SUCCESS("S", "Success"),
  FAILURE("F", "Failure"),
  EMPTY("E", "No Tickets Available"),
  CANCEL("C", "Cancelled");

  private String code;

  private String desc;

  ConfirmOrderStatusEnum(String code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  @Override    public String toString() {
    return "ConfirmOrderStatusEnum{" +
        "code='" + code + '\'' +
        ", desc='" + desc + '\'' +
        "} " + super.toString();
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public String getDesc() {
    return desc;
  }

}
