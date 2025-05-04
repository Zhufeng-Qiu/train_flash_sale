package com.zephyr.train.common.req;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;

public class PageReq {

  @NotNull(message = "[Page number] cannot be null")
  private Integer page;

  @NotNull(message = "[Page size] cannot be null")
  @Max(value = 100, message = "[Page size] cannot exceed 100")
  private Integer size;

  public Integer getPage() {
    return page;
  }

  public void setPage(Integer page) {
    this.page = page;
  }

  public Integer getSize() {
    return size;
  }

  public void setSize(Integer size) {
    this.size = size;
  }

  @Override
  public String toString() {
    return "PageReq{" +
        "page=" + page +
        ", size=" + size +
        '}';
  }
}
