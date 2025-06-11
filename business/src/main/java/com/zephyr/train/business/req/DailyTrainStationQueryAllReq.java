package com.zephyr.train.business.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class DailyTrainStationQueryAllReq {

  /**
   * Date
   */
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  @NotNull(message = "[Date] cannot be null")
  private Date date;

  /**
   * Train number
   */
  @NotBlank(message = "[Train number] cannot be null")
  private String trainCode;

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

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("DailyTrainStationQueryReq{");
    sb.append("date=").append(date);
    sb.append(", trainCode='").append(trainCode).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
