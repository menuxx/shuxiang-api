package com.menuxx.common.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

public class Order {

    // 1 已创建, 未付款 2 已消费(付款)，未发货, 3，已发货 4， 已签收

    public static final int CREATED = 1;

    public static final int CONSUMED = 2;

    public static final int DELIVERYD = 3;

    public static final int RECEIVED = 4;

    private Integer id;

    private Integer merchantId;

    private Integer channelId;

    private Integer queueNum;

    @JsonProperty("vChannel")
    private VChannel vChannel;

    private Integer userId;

    private User user;

    private String orderNo;

    private String expressNo;

    private String expressName;

    private String orderName;

    private Integer payAmount;

    private Integer totalAmount;

    private Integer count;

    private Integer paid;

    private Integer status;

    private List<OrderItem> items;

    private Date createAt;

    private Date updateAt;

    private String receiverPhoneNumber;

    private String receiverName;

    private String receiverAreaAddress;

    private String receiverDetailInfo;

    private String receiverPostalCode;

    private String shareImage;


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setExpressName(String expressName) {
        this.expressName = expressName;
    }

    public Integer getQueueNum() {
        return queueNum;
    }

    public void setQueueNum(Integer queueNum) {
        this.queueNum = queueNum;
    }

    @JsonProperty("vChannel")
    public VChannel getVChannel() {
        return vChannel;
    }

    public void setVChannel(VChannel vChannel) {
        this.vChannel = vChannel;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public String getExpressNo() {
        return expressNo;
    }

    public void setExpressNo(String expressNo) {
        this.expressNo = expressNo == null ? null : expressNo.trim();
    }

    public String getExpressName() {
        return expressName;
    }

    public void setExpressType(String expressName) {
        this.expressName = expressName;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName == null ? null : orderName.trim();
    }

    public Integer getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(Integer payAmount) {
        this.payAmount = payAmount;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getPaid() {
        return paid;
    }

    public void setPaid(Integer paid) {
        this.paid = paid;
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

    public String getReceiverPhoneNumber() {
        return receiverPhoneNumber;
    }

    public void setReceiverPhoneNumber(String receiverPhoneNumber) {
        this.receiverPhoneNumber = receiverPhoneNumber == null ? null : receiverPhoneNumber.trim();
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName == null ? null : receiverName.trim();
    }

    public String getReceiverAreaAddress() {
        return receiverAreaAddress;
    }

    public void setReceiverAreaAddress(String receiverAreaAddress) {
        this.receiverAreaAddress = receiverAreaAddress == null ? null : receiverAreaAddress.trim();
    }

    public String getReceiverDetailInfo() {
        return receiverDetailInfo;
    }

    public void setReceiverDetailInfo(String receiverDetailInfo) {
        this.receiverDetailInfo = receiverDetailInfo == null ? null : receiverDetailInfo.trim();
    }

    public String getReceiverPostalCode() {
        return receiverPostalCode;
    }

    public void setReceiverPostalCode(String receiverPostalCode) {
        this.receiverPostalCode = receiverPostalCode == null ? null : receiverPostalCode.trim();
    }

    public String getShareImage() {
        return shareImage;
    }

    public void setShareImage(String shareImage) {
        this.shareImage = shareImage;
    }
}