package com.zephyr.train.business.req;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TrainSaveReq {

    /**
     * id
     */
    private Long id;

    /**
     * Train Number
     */
    @NotBlank(message = "[Train Number] cannot be blank")
    private String code;

    /**
     * Train Type|Enum[TrainTypeEnum]
     */
    @NotBlank(message = "[Train Type] cannot be blank")
    private String type;

    /**
     * Departure Station
     */
    @NotBlank(message = "[Departure Station] cannot be blank")
    private String start;

    /**
     * Departure Station Alias
     */
    @NotBlank(message = "[Departure Station Alias] cannot be blank")
    private String startPinyin;

    /**
     * Start Time
     */
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT-7")
    @NotNull(message = "[Start Time] cannot be null")
    private Date startTime;

    /**
     * Arrival Station
     */
    @NotBlank(message = "[Arrival Station] cannot be blank")
    private String end;

    /**
     * Arrival Station Alias
     */
    @NotBlank(message = "[Arrival Station Alias] cannot be blank")
    private String endPinyin;

    /**
     * End Time
     */
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT-7")
    @NotNull(message = "[End Time] cannot be null")
    private Date endTime;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
        sb.append(", code=").append(code);
        sb.append(", type=").append(type);
        sb.append(", start=").append(start);
        sb.append(", startPinyin=").append(startPinyin);
        sb.append(", startTime=").append(startTime);
        sb.append(", end=").append(end);
        sb.append(", endPinyin=").append(endPinyin);
        sb.append(", endTime=").append(endTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}
