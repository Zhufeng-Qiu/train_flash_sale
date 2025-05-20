package com.zephyr.train.business.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ConfirmOrderTicketReq {

  /**
   * Passenger Id
   */
  @NotNull(message = "[Passenger Id] cannot be null")
  private Long passengerId;

  /**
   * Passenger Type
   */
  @NotBlank(message = "[Passenger Type] cannot be null")
  private String passengerType;

  /**
   * Passenger Name
   */
  @NotBlank(message = "[Passenger Name] cannot be null")
  private String passengerName;

  /**
   * Passenger Id Card
   */
  @NotBlank(message = "[Passenger Id Card] cannot be null")
  private String passengerIdCard;

  /**
   * Seat Type code
   */
  @NotBlank(message = "[Seat Type] cannot be null")
  private String seatTypeCode;

  /**
   * Seat, can be null, e.g. A1
   */
  private String seat;

  public Long getPassengerId() {
    return passengerId;
  }

  public void setPassengerId(Long passengerId) {
    this.passengerId = passengerId;
  }

  public String getPassengerType() {
    return passengerType;
  }

  public void setPassengerType(String passengerType) {
    this.passengerType = passengerType;
  }

  public String getPassengerName() {
    return passengerName;
  }

  public void setPassengerName(String passengerName) {
    this.passengerName = passengerName;
  }

  public String getPassengerIdCard() {
    return passengerIdCard;
  }

  public void setPassengerIdCard(String passengerIdCard) {
    this.passengerIdCard = passengerIdCard;
  }

  public String getSeatTypeCode() {
    return seatTypeCode;
  }

  public void setSeatTypeCode(String seatTypeCode) {
    this.seatTypeCode = seatTypeCode;
  }

  public String getSeat() {
    return seat;
  }

  public void setSeat(String seat) {
    this.seat = seat;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("ConfirmOrderTicketReq{");
    sb.append("passengerId=").append(passengerId);
    sb.append(", passengerType='").append(passengerType).append('\'');
    sb.append(", passengerName='").append(passengerName).append('\'');
    sb.append(", passengerIdCard='").append(passengerIdCard).append('\'');
    sb.append(", seatTypeCode='").append(seatTypeCode).append('\'');
    sb.append(", seat='").append(seat).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
