package com.zephyr.train.business.enums;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public enum SeatColEnum {

  YDZ_A("A", "A", "1"),
  YDZ_C("C", "C", "1"),
  YDZ_D("D", "D", "1"),
  YDZ_F("F", "F", "1"),
  EDZ_A("A", "A", "2"),
  EDZ_B("B", "B", "2"),
  EDZ_C("C", "C", "2"),
  EDZ_D("D", "D", "2"),
  EDZ_F("F", "F", "2");

  private String code;

  private String desc;

  /**
   * Correspond to SeatTypeEnum.code
   */
  private String type;

  SeatColEnum(String code, String desc, String type) {
    this.code = code;
    this.desc = desc;
    this.type = type;
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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  /**
   * Get the columns based on seat type of carriage. For example, if the seat type of carriage is first class seat, columnList={ACDF}
   */
  public static List<SeatColEnum> getColsByType(String seatType) {
    List<SeatColEnum> colList = new ArrayList<>();
    EnumSet<SeatColEnum> seatColEnums = EnumSet.allOf(SeatColEnum.class);
    for (SeatColEnum anEnum : seatColEnums) {
      if (seatType.equals(anEnum.getType())) {
        colList.add(anEnum);
      }
    }
    return colList;
  }

}
