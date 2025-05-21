package com.zephyr.train.common.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public class MemberTicketReq {

  /**
   * Member Id
   */
  @NotNull(message = "[Member Id] cannot be null")
  private Long memberId;

  /**
   * Passenger Id
   */
  @NotNull(message = "[Passenger Id] cannot be null")
  private Long passengerId;

  /**
   * Passenger Name
   */
  @NotNull(message = "[Passenger Name] cannot be null")
  private String passengerName;

  /**
   * Date
   */
  @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT-7")
  @NotNull(message = "[Date] cannot be null")
  private Date date;

  /**
   * Train Number
   */
  @NotBlank(message = "[Train Number] cannot be null")
  private String trainCode;

  /**
   * Carriage Index
   */
  @NotNull(message = "[Carriage Index] cannot be null")
  private Integer carriageIndex;

  /**
   * Row|01, 02
   */
  @NotBlank(message = "[Row] cannot be null")
  private String row;

  /**
   * Column|Enum[SeatColEnum]
   */
  @NotBlank(message = "[Column] cannot be null")
  private String col;

  /**
   * Departure Station
   */
  @NotBlank(message = "[Departure Station] cannot be null")
  private String start;

  /**
   * Departure Station Alias
   */
  @NotBlank(message = "[Departure Station Alias] cannot be blank")
  private String startPinyin;

  /**
   * Departure Time
   */
  @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT-7")
  @NotNull(message = "[Departure Time] cannot be null")
  private Date startTime;

  /**
   * Arrival Station
   */
  @NotBlank(message = "[Arrival Station] cannot be null")
  private String end;

  /**
   * Arrival Station Alias
   */
  @NotBlank(message = "[Arrival Station Alias] cannot be blank")
  private String endPinyin;

  /**
   * Arrival Time
   */
  @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT-7")
  @NotNull(message = "[Arrival Time] cannot be null")
  private Date endTime;

  /**
   * Seat Type|Enum[SeatTypeEnum]
   */
  @NotBlank(message = "[Seat Type] cannot be null")
  private String seatType;

  public Long getMemberId() {
    return memberId;
  }

  public void setMemberId(Long memberId) {
    this.memberId = memberId;
  }

  public Long getPassengerId() {
    return passengerId;
  }

  public void setPassengerId(Long passengerId) {
    this.passengerId = passengerId;
  }

  public String getPassengerName() {
    return passengerName;
  }

  public void setPassengerName(String passengerName) {
    this.passengerName = passengerName;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public String getTrainCode() {
    return trainCode;
  }

  public void setTrainCode(String trainCode) {
    this.trainCode = trainCode;
  }

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

  public String getStart() {
    return start;
  }

  public void setStart(String start) {
    this.start = start;
  }

  public String getEnd() {
    return end;
  }

  public void setEnd(String end) {
    this.end = end;
  }

  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }

  public String getSeatType() {
    return seatType;
  }

  public void setSeatType(String seatType) {
    this.seatType = seatType;
  }

  public String getStartPinyin() {
    return startPinyin;
  }

  public void setStartPinyin(String startPinyin) {
    this.startPinyin = startPinyin;
  }

  public String getEndPinyin() {
    return endPinyin;
  }

  public void setEndPinyin(String endPinyin) {
    this.endPinyin = endPinyin;
  }

  @Override
  public String toString() {
    return "MemberTicketReq{" +
        "memberId=" + memberId +
        ", passengerId=" + passengerId +
        ", passengerName='" + passengerName + '\'' +
        ", date=" + date +
        ", trainCode='" + trainCode + '\'' +
        ", carriageIndex=" + carriageIndex +
        ", row='" + row + '\'' +
        ", col='" + col + '\'' +
        ", start='" + start + '\'' +
        ", startPinyin='" + startPinyin + '\'' +
        ", startTime=" + startTime +
        ", end='" + end + '\'' +
        ", endPinyin='" + endPinyin + '\'' +
        ", endTime=" + endTime +
        ", seatType='" + seatType + '\'' +
        '}';
  }
}

