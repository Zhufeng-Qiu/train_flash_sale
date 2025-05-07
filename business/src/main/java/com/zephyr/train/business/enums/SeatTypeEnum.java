package com.zephyr.train.business.enums;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

public enum SeatTypeEnum {

  YDZ("1", "First Class Seat", new BigDecimal("0.4")),
  EDZ("2", "Second Class Seat", new BigDecimal("0.3")),
  RW("3", "Soft Sleeper", new BigDecimal("0.6")),
  YW("4", "Hard Seat", new BigDecimal("0.5"));

  private String code;

  private String desc;

  /**
   * Basic price rate N ¥/km，0.4 means 0.4 ¥/km
   */
  private BigDecimal price;

  SeatTypeEnum(String code, String desc, BigDecimal price) {
    this.code = code;
    this.desc = desc;
    this.price = price;
  }

  public String getCode() {
    return code;
  }

  public String getDesc() {
    return desc;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public static List<HashMap<String,String>> getEnumList() {
    List<HashMap<String, String>> list = new ArrayList<>();
    for (SeatTypeEnum anEnum : EnumSet.allOf(SeatTypeEnum.class)) {
      HashMap<String, String> map = new HashMap<>();
      map.put("code",anEnum.code);
      map.put("desc",anEnum.desc);
      list.add(map);
    }
    return list;
  }

  public static SeatTypeEnum getEnumByCode(String code) {
    for (SeatTypeEnum enums : SeatTypeEnum.values()) {
      if (enums.getCode().equalsIgnoreCase(code)) {
        return enums;
      }
    }
    return null;
  }
}
