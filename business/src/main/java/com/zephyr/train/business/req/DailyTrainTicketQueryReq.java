package com.zephyr.train.business.req;

import com.zephyr.train.common.req.PageReq;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

public class DailyTrainTicketQueryReq extends PageReq {

    /**
     * Date
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    /**
     * Train number
     */
    private String trainCode;

    /**
     * Departure station
     */
    private String start;

    /**
     * Arrival station
     */
    private String end;

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

    @Override
    public String toString() {
        return "DailyTrainTicketQueryReq{" +
            "date=" + date +
            ", trainCode='" + trainCode + '\'' +
            ", start='" + start + '\'' +
            ", end='" + end + '\'' +
            "} " + super.toString();
    }
}
