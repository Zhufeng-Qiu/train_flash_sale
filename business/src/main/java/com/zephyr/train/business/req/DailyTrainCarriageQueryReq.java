package com.zephyr.train.business.req;

import com.zephyr.train.common.req.PageReq;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

public class DailyTrainCarriageQueryReq extends PageReq {

    /**
     * Date
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    /**
     * Train Number
     */
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
        return "DailyTrainCarriageQueryReq{" +
            "date=" + date +
            ", trainCode='" + trainCode + '\'' +
            "} " + super.toString();
    }
}
