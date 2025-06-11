package com.zephyr.train.business.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

public class ConfirmOrderDoReq {

    /**
     * Log Id
     */
    private String logId;

    /**
     * Member Id
     */
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
    @NotEmpty(message = "[Ticket] cannot be blank")
    private List<ConfirmOrderTicketReq> tickets;

    /**
     * CAPTCHA
     */
    @NotBlank(message = "[CAPTCHA] cannot be null")
    private String imageCode;

    /**
     * CAPTCHA token
     */
    @NotBlank(message = "[CAPTCHA] illegal parameter")
    private String imageCodeToken;

    /**
     * Number of users in queue for ticket Queue Simulation:
     */
    private int lineNumber;

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

    public List<ConfirmOrderTicketReq> getTickets() {
        return tickets;
    }

    public void setTickets(List<ConfirmOrderTicketReq> tickets) {
        this.tickets = tickets;
    }

    public String getImageCode() {
        return imageCode;
    }

    public void setImageCode(String imageCode) {
        this.imageCode = imageCode;
    }

    public String getImageCodeToken() {
        return imageCodeToken;
    }

    public void setImageCodeToken(String imageCodeToken) {
        this.imageCodeToken = imageCodeToken;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    public String toString() {
        return "ConfirmOrderDoReq{" +
            "memberId=" + memberId +
            ", date=" + date +
            ", trainCode='" + trainCode + '\'' +
            ", start='" + start + '\'' +
            ", end='" + end + '\'' +
            ", dailyTrainTicketId=" + dailyTrainTicketId +
            ", tickets=" + tickets +
            ", imageCode='" + imageCode + '\'' +
            ", imageCodeToken='" + imageCodeToken + '\'' +
            ", logId='" + logId + '\'' +
            ", lineNumber=" + lineNumber +
            '}';
    }
}
