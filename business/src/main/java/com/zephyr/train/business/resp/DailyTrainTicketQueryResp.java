package com.zephyr.train.business.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;

public class DailyTrainTicketQueryResp implements Serializable {

    /**
     * id
     */
    @JsonSerialize(using= ToStringSerializer.class)
    private Long id;

    /**
     * Date
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT-7")
    private Date date;

    /**
     * Train Number
     */
    private String trainCode;

    /**
     * Departure Station
     */
    private String start;

    /**
     * Departure Station Alias
     */
    private String startPinyin;

    /**
     * Start Time
     */
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT-7")
    private Date startTime;

    /**
     * Departure Station Index|The Nth stop of the entire train route
     */
    private Integer startIndex;

    /**
     * Arrival Station
     */
    private String end;

    /**
     * Arrival Station Alias
     */
    private String endPinyin;

    /**
     * End Time
     */
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT-7")
    private Date endTime;

    /**
     * Arrival Station Index|The Nth stop of the entire train route
     */
    private Integer endIndex;

    /**
     * First Class Remaining Tickets
     */
    private Integer ydz;

    /**
     * First Class Ticket Price
     */
    private BigDecimal ydzPrice;

    /**
     * Second Class Remaining Tickets
     */
    private Integer edz;

    /**
     * Second Class Ticket Price
     */
    private BigDecimal edzPrice;

    /**
     * Soft Sleeper Remaining Tickets
     */
    private Integer rw;

    /**
     * Soft Sleeper Ticket Price
     */
    private BigDecimal rwPrice;

    /**
     * Hard Seat Remaining Tickets
     */
    private Integer yw;

    /**
     * Hard Seat Ticket Price
     */
    private BigDecimal ywPrice;

    /**
     * Create Time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT-7")
    private Date createTime;

    /**
     * Update Time
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT-7")
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getStartPinyin() {
        return startPinyin;
    }

    public void setStartPinyin(String startPinyin) {
        this.startPinyin = startPinyin;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getEndPinyin() {
        return endPinyin;
    }

    public void setEndPinyin(String endPinyin) {
        this.endPinyin = endPinyin;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(Integer endIndex) {
        this.endIndex = endIndex;
    }

    public Integer getYdz() {
        return ydz;
    }

    public void setYdz(Integer ydz) {
        this.ydz = ydz;
    }

    public BigDecimal getYdzPrice() {
        return ydzPrice;
    }

    public void setYdzPrice(BigDecimal ydzPrice) {
        this.ydzPrice = ydzPrice;
    }

    public Integer getEdz() {
        return edz;
    }

    public void setEdz(Integer edz) {
        this.edz = edz;
    }

    public BigDecimal getEdzPrice() {
        return edzPrice;
    }

    public void setEdzPrice(BigDecimal edzPrice) {
        this.edzPrice = edzPrice;
    }

    public Integer getRw() {
        return rw;
    }

    public void setRw(Integer rw) {
        this.rw = rw;
    }

    public BigDecimal getRwPrice() {
        return rwPrice;
    }

    public void setRwPrice(BigDecimal rwPrice) {
        this.rwPrice = rwPrice;
    }

    public Integer getYw() {
        return yw;
    }

    public void setYw(Integer yw) {
        this.yw = yw;
    }

    public BigDecimal getYwPrice() {
        return ywPrice;
    }

    public void setYwPrice(BigDecimal ywPrice) {
        this.ywPrice = ywPrice;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", date=").append(date);
        sb.append(", trainCode=").append(trainCode);
        sb.append(", start=").append(start);
        sb.append(", startPinyin=").append(startPinyin);
        sb.append(", startTime=").append(startTime);
        sb.append(", startIndex=").append(startIndex);
        sb.append(", end=").append(end);
        sb.append(", endPinyin=").append(endPinyin);
        sb.append(", endTime=").append(endTime);
        sb.append(", endIndex=").append(endIndex);
        sb.append(", ydz=").append(ydz);
        sb.append(", ydzPrice=").append(ydzPrice);
        sb.append(", edz=").append(edz);
        sb.append(", edzPrice=").append(edzPrice);
        sb.append(", rw=").append(rw);
        sb.append(", rwPrice=").append(rwPrice);
        sb.append(", yw=").append(yw);
        sb.append(", ywPrice=").append(ywPrice);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}