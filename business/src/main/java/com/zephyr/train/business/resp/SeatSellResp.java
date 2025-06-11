package com.zephyr.train.business.resp;

public class SeatSellResp {

  /**
   * Carriage Index
   */
  private Integer carriageIndex;

  /**
   * Row|01, 02
   */
  private String row;

  /**
   * Column|Enum[SeatColEnum]
   */
  private String col;

  /**
   * Seat Type|Enum[SeatTypeEnum]
   */
  private String seatType;

  /**
   * Sale Status|Represents route stations using 01; 0: for sale, 1: sold
   */
  private String sell;

  public Integer getCarriageIndex() {
    return carriageIndex;
  }

  public void setCarriageIndex(Integer carriageIndex) {
    this.carriageIndex = carriageIndex;
  }

  public String getRow() {
    return row;
  }

  public void setRow(String row) {
    this.row = row;
  }

  public String getCol() {
    return col;
  }

  public void setCol(String col) {
    this.col = col;
  }

  public String getSeatType() {
    return seatType;
  }

  public void setSeatType(String seatType) {
    this.seatType = seatType;
  }

  public String getSell() {
    return sell;
  }

  public void setSell(String sell) {
    this.sell = sell;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getClass().getSimpleName());
    sb.append(" [");
    sb.append("Hash = ").append(hashCode());
    sb.append(", carriageIndex=").append(carriageIndex);
    sb.append(", row=").append(row);
    sb.append(", col=").append(col);
    sb.append(", seatType=").append(seatType);
    sb.append(", sell=").append(sell);
    sb.append("]");
    return sb.toString();
  }
}
