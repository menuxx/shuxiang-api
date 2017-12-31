package com.menuxx.common.bean;

import java.util.Date;
import java.util.List;

public class ItemCodeTask {

    public static final int STATUS_CREATED = 1; // 进行中

    public static final int STATUS_FINISH = 2;  // 以结束

    private Integer id;

    private String remark;

    private Integer status;

    private Date createAt;

    private Date updateAt;

    private List<ItemCodeBatch> batches;

    public List<ItemCodeBatch> getBatches() {
        return batches;
    }

    public void setBatches(List<ItemCodeBatch> batches) {
        this.batches = batches;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }
}