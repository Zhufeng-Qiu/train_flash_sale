package com.zephyr.train.business.req;

import com.zephyr.train.common.req.PageReq;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

public class DailyTrainQueryReq extends PageReq {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;
    private String code;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "DailyTrainQueryReq{" +
            "date=" + date +
            ", code='" + code + '\'' +
            "} " + super.toString();
    }
}
