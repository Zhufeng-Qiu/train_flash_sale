package com.zephyr.train.business.req;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ConfirmOrderSaveReq {

    /**
     * id
     */
    private Long id;

    /**
     * Member Id
     */
    @NotNull(message = "[Member Id] cannot be null")
    private Long memberId;

    /**
     * Date
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT-7")
    @NotNull(message = "[Date] cannot be null")
    private Date date;

    /**
     * Train Number
     */
    @NotBlank(message = "[Train Number] cannot be blank")
    private String trainCode;

    /**
     * Departure Station
     */
    @NotBlank(message = "[Departure Station] cannot be blank")
    private String start;

    /**
     * Arrival Station
     */
    @NotBlank(message = "[Arrival Station] cannot be blank")
    private String end;

    /**
     * Ticket Id
     */
    @NotNull(message = "[Ticket Id] cannot be null")
    private Long dailyTrainTicketId;

    /**
     * Ticket
     */
    @NotBlank(message = "[Ticket] cannot be blank")
    private String tickets;

    /**
     * Order Status|Enum[ConfirmOrderStatusEnum]
     */
    @NotBlank(message = "[Order Status] cannot be blank")
    private String status;

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

    public Long getDailyTrainTicketId() {
        return dailyTrainTicketId;
    }

    public void setDailyTrainTicketId(Long dailyTrainTicketId) {
        this.dailyTrainTicketId = dailyTrainTicketId;
    }

    public String getTickets() {
        return tickets;
    }

    public void setTickets(String tickets) {
        this.tickets = tickets;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
        sb.append(", date=").append(date);
        sb.append(", trainCode=").append(trainCode);
        sb.append(", start=").append(start);
        sb.append(", end=").append(end);
        sb.append(", dailyTrainTicketId=").append(dailyTrainTicketId);
        sb.append(", tickets=").append(tickets);
        sb.append(", status=").append(status);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}
