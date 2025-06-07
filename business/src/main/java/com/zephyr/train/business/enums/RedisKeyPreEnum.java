package com.zephyr.train.business.enums;

public enum RedisKeyPreEnum {

  CONFIRM_ORDER("LOCK_CONFIRM_ORDER", "Purchase ticket lock"),
  SK_TOKEN("LOCK_SK_TOKEN", "Token lock"),
  SK_TOKEN_COUNT("SK_TOKEN_COUNT", "Token count");

  private final String code;

  private final String desc;

  RedisKeyPreEnum(String code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public String getCode() {
    return code;
  }

  public String getDesc() {
    return desc;
  }
}
