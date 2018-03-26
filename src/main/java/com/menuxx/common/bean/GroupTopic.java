package com.menuxx.common.bean;

import java.util.Date;

public class GroupTopic {

    public static final int STATUS_POST = 1;

    public static final int STATUS_RELEASE = 2;

    private Integer id;

    private User creator;

    private Integer creatorId;

    private Date createAt;

    private String content;

    private Integer sortWeight1;

    private Integer sortWeight2;

    private Integer groupId;

    private Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public Integer getSortWeight1() {
        return sortWeight1;
    }

    public void setSortWeight1(Integer sortWeight1) {
        this.sortWeight1 = sortWeight1;
    }

    public Integer getSortWeight2() {
        return sortWeight2;
    }

    public void setSortWeight2(Integer sortWeight2) {
        this.sortWeight2 = sortWeight2;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }
}