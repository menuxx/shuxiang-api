package com.menuxx.common.bean;

import java.util.Date;

public class ItemCodeBatch {

    public static final int STATUS_CREATED = 1; // 进行中

    public static final int STATUS_FINISH = 2;  // 以结束

    private Integer id;

    private Integer count;

    private String startCode;

    private String endCode;

    private Integer taskId;

    private Integer batchNum;

    private Date createAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getStartCode() {
        return startCode;
    }

    public void setStartCode(String startCode) {
        this.startCode = startCode == null ? null : startCode.trim();
    }

    public String getEndCode() {
        return endCode;
    }

    public void setEndCode(String endCode) {
        this.endCode = endCode;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Integer getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(Integer batchNum) {
        this.batchNum = batchNum;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
}