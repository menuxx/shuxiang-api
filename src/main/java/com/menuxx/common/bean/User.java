package com.menuxx.common.bean;

import com.menuxx.mall.bean.YhsdCustomer;

import java.util.Date;

public class User {

    private Integer id;

    private String userName;

    private String avatarUrl;

    private String phoneNumber;

    private Date lastLoginTime;

    private String lastLoginIp;

    private Integer wxUserId;

    private Integer yhCustomerId;

    private String yhEmail;

    private String yhPassword;

    private Integer enable;

    private String passwordToken;

    private WXUser wxUser;

    private YhsdCustomer customer;

    private Date updateAt;

    private Date createAt;

    public Integer getYhCustomerId() {
        return yhCustomerId;
    }

    public void setYhCustomerId(Integer yhCustomerId) {
        this.yhCustomerId = yhCustomerId;
    }

    public String getYhEmail() {
        return yhEmail;
    }

    public void setYhEmail(String yhEmail) {
        this.yhEmail = yhEmail;
    }

    public String getYhPassword() {
        return yhPassword;
    }

    public void setYhPassword(String yhPassword) {
        this.yhPassword = yhPassword;
    }

    public YhsdCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(YhsdCustomer customer) {
        this.customer = customer;
    }

    public String getPasswordToken() {
        return passwordToken;
    }

    public void setPasswordToken(String passwordToken) {
        this.passwordToken = passwordToken;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber == null ? null : phoneNumber.trim();
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp == null ? null : lastLoginIp.trim();
    }

    public Integer getWxUserId() {
        return wxUserId;
    }

    public void setWxUserId(Integer wxUserId) {
        this.wxUserId = wxUserId;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public WXUser getWxUser() {
        return wxUser;
    }

    public void setWxUser(WXUser wxUser) {
        this.wxUser = wxUser;
    }
}