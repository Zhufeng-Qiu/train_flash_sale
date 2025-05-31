package com.zephyr.train.member.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

public class TicketQueryResp {

    /**
     * id
     */
    @JsonSerialize(using= ToStringSerializer.class)
    private Long id;

    /**
     * Member Id
     */
    @JsonSerialize(using= ToStringSerializer.class)
    private Long memberId;

    /**
     * Passenger Id
     */
    @JsonSerialize(using= ToStringSerializer.class)
    private Long passengerId;

    /**
     * Passenger Name
     */
    private String passengerName;

    /**
     * Date
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT-7")
    private Date trainDate;

    /**
     * Train Number
     */
    private String trainCode;

    /**
     * Carriage Index
     */
    private Integer carriageIndex;

    /**
     * Row|01, 02
     */
    private String seatRow;

    /**
     * Column|Enum[SeatColEnum]
     */
    private String seatCol;

    /**
     * Departure Station
     */
    private String startStation;

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
     * Arrival Station
     */
    private String endStation;

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
     * Seat Type|Enum[SeatTypeEnum]
     */
    private String seatType;

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

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public Date getTrainDate() {
        return trainDate;
    }

    public void setTrainDate(Date trainDate) {
        this.trainDate = trainDate;
    }

    public String getTrainCode() {
        return trainCode;
    }

    public void setTrainCode(String trainCode) {
        this.trainCode = trainCode;
    }

    public Integer getCarriageIndex() {
        return carriageIndex;
    }

    public void setCarriageIndex(Integer carriageIndex) {
        this.carriageIndex = carriageIndex;
    }

    public String getSeatRow() {
        return seatRow;
    }

    public void setSeatRow(String seatRow) {
        this.seatRow = seatRow;
    }

    public String getSeatCol() {
        return seatCol;
    }

    public void setSeatCol(String seatCol) {
        this.seatCol = seatCol;
    }

    public String getStartStation() {
        return startStation;
    }

    public void setStartStation(String startStation) {
        this.startStation = startStation;
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

    public String getEndStation() {
        return endStation;
    }

    public void setEndStation(String endStation) {
        this.endStation = endStation;
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

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
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
        sb.append(", memberId=").append(memberId);
        sb.append(", passengerId=").append(passengerId);
        sb.append(", passengerName=").append(passengerName);
        sb.append(", trainDate=").append(trainDate);
        sb.append(", trainCode=").append(trainCode);
        sb.append(", carriageIndex=").append(carriageIndex);
        sb.append(", seatRow=").append(seatRow);
        sb.append(", seatCol=").append(seatCol);
        sb.append(", startStation=").append(startStation);
        sb.append(", startPinyin=").append(startPinyin);
        sb.append(", startTime=").append(startTime);
        sb.append(", endStation=").append(endStation);
        sb.append(", endPinyin=").append(endPinyin);
        sb.append(", endTime=").append(endTime);
        sb.append(", seatType=").append(seatType);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}