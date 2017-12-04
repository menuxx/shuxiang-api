package com.menuxx.ocapiserver.bean;

import java.util.Date;

public class VipChannel {
    private Integer id;

    private Integer expressFee;

    private Integer payFee;

    private Integer stock;

    private String ownerName;

    private String ownerAvatarUrl;

    private String giftTxt;

    private Integer itemIt;

    private Integer merchantId;

    private Date createAt;

    private Date updateAt;

    private Date expiredTime;

    private Date startTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getExpressFee() {
        return expressFee;
    }

    public void setExpressFee(Integer expressFee) {
        this.expressFee = expressFee;
    }

    public Integer getPayFee() {
        return payFee;
    }

    public void setPayFee(Integer payFee) {
        this.payFee = payFee;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName == null ? null : ownerName.trim();
    }

    public String getOwnerAvatarUrl() {
        return ownerAvatarUrl;
    }

    public void setOwnerAvatarUrl(String ownerAvatarUrl) {
        this.ownerAvatarUrl = ownerAvatarUrl == null ? null : ownerAvatarUrl.trim();
    }

    public String getGiftTxt() {
        return giftTxt;
    }

    public void setGiftTxt(String giftTxt) {
        this.giftTxt = giftTxt == null ? null : giftTxt.trim();
    }

    public Integer getItemIt() {
        return itemIt;
    }

    public void setItemIt(Integer itemIt) {
        this.itemIt = itemIt;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
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

    public Date getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Date expiredTime) {
        this.expiredTime = expiredTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
}