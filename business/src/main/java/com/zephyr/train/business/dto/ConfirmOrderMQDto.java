package com.zephyr.train.business.dto;

import java.util.Date;

public class ConfirmOrderMQDto {
  /**
   * Log Id, when converting from synchronous to asynchronous operations, use the same log ID.
   */
  private String logId;

  /**
   * Date
   */
  private Date date;

  /**
   * Train number
   */
  private String trainCode;

  public String getLogId() {
    return logId;
  }

  public void setLogId(String logId) {
    this.logId = logId;
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

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("ConfirmOrderMQDto{");
    sb.append("logId=").append(logId);
    sb.append(", date=").append(date);
    sb.append(", trainCode='").append(trainCode).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
