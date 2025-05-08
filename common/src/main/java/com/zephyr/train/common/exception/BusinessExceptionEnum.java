package com.zephyr.train.common.exception;

public enum BusinessExceptionEnum {
  MEMBER_MOBILE_REGISTERED("Mobile has been registered"),
  MEMBER_MOBILE_NOT_REGISTERED("Please get SMS verification code first"),
  MEMBER_MOBILE_CODE_ERROR("SMS verification code does not match"),

  BUSINESS_STATION_NAME_UNIQUE_ERROR("[Station] already exists"),
  BUSINESS_TRAIN_CODE_UNIQUE_ERROR("[Train Number] already exists"),
  BUSINESS_TRAIN_STATION_INDEX_UNIQUE_ERROR("[Station Index] already exists for current train number"),
  BUSINESS_TRAIN_STATION_NAME_UNIQUE_ERROR("[Station Name] already exists for current train number"),
  BUSINESS_TRAIN_CARRIAGE_INDEX_UNIQUE_ERROR("[Carriage Index] already exists for current train number");

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
