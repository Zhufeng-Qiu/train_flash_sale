package com.zephyr.train.common.resp;

public class CommonResp<T> {

  /**
   * Success or fail in business operation
   */
  private boolean success = true;

  /**
   * Response messsage
   */
  private String message;

  /**
   * Return generic data with custom type
   */
  private T content;

  public CommonResp() {
  }

  public CommonResp(boolean success, String message, T content) {
    this.success = success;
    this.message = message;
    this.content = content;
  }

  public CommonResp(T content) {
    this.content = content;
  }

  public boolean getSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public T getContent() {
    return content;
  }

  public void setContent(T content) {
    this.content = content;
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("CommonResp{");
    sb.append("success=").append(success);
    sb.append(", message='").append(message).append('\'');
    sb.append(", content=").append(content);
    sb.append('}');
    return sb.toString();
  }
}
