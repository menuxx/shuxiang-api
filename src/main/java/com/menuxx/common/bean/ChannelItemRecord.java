package com.menuxx.common.bean;

import java.util.Date;

public class ChannelItemRecord {

    private Integer id;

    private Integer channelId;

    private Integer itemId;

    private Integer obtainUserId;

    private Date obtainTime;

    public ChannelItemRecord(Integer id, Integer channelId, Integer itemId, Integer obtainUserId, Date obtainTime) {
        this.id = id;
        this.channelId = channelId;
        this.itemId = itemId;
        this.obtainUserId = obtainUserId;
        this.obtainTime = obtainTime;
    }

    public ChannelItemRecord() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getObtainUserId() {
        return obtainUserId;
    }

    public void setObtainUserId(Integer obtainUserId) {
        this.obtainUserId = obtainUserId;
    }

    public Date getObtainTime() {
        return obtainTime;
    }

    public void setObtainTime(Date obtainTime) {
        this.obtainTime = obtainTime;
    }
}