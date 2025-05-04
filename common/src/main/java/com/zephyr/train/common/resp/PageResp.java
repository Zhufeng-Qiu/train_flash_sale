package com.zephyr.train.common.resp;

import java.io.Serializable;
import java.util.List;

public class PageResp<T> implements Serializable {

  /**
   * Total number
   */
  private Long total;

  /**
   * List for current page
   */
  private List<T> list;

  public Long getTotal() {
    return total;
  }

  public void setTotal(Long total) {
    this.total = total;
  }

  public List<T> getList() {
    return list;
  }

  public void setList(List<T> list) {
    this.list = list;
  }

  @Override
  public String toString() {
    return "PageResp{" +
        "total=" + total +
        ", list=" + list +
        '}';
  }
}
