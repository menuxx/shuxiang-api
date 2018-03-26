package com.menuxx.common.bean;

public class OrderCharge {

    private Integer id;

    private String appid;

    private String mchId;

    private String openid;

    private String outTradeNo;

    private String deviceInfo;

    private String nonceStr;

    private String prepayId;

    private String tradeType;

    private Integer totalFee;

    private Integer settlementTotalFee;

    private String feeType;

    private Integer cashFee;

    private String cashFeeType;

    private String attach;

    private String body;

    private String sign;

    private String signType;

    private String goodsTag;

    private String timeExpire;

    private String timeStart;

    private String timeEnd;

    private String spbillCreateIp;

    private String errCodeDes;

    private String errCode;

    private String resultCode;

    private String transactionId;

    private String detail;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid == null ? null : appid.trim();
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId == null ? null : mchId.trim();
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid == null ? null : openid.trim();
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo == null ? null : outTradeNo.trim();
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo == null ? null : deviceInfo.trim();
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr == null ? null : nonceStr.trim();
    }

    public String getPrepayId() {
        return prepayId;
    }

    public void setPrepayId(String prepayId) {
        this.prepayId = prepayId == null ? null : prepayId.trim();
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType == null ? null : tradeType.trim();
    }

    public Integer getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Integer totalFee) {
        this.totalFee = totalFee;
    }

    public Integer getSettlementTotalFee() {
        return settlementTotalFee;
    }

    public void setSettlementTotalFee(Integer settlementTotalFee) {
        this.settlementTotalFee = settlementTotalFee;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType == null ? null : feeType.trim();
    }

    public Integer getCashFee() {
        return cashFee;
    }

    public void setCashFee(Integer cashFee) {
        this.cashFee = cashFee;
    }

    public String getCashFeeType() {
        return cashFeeType;
    }

    public void setCashFeeType(String cashFeeType) {
        this.cashFeeType = cashFeeType == null ? null : cashFeeType.trim();
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach == null ? null : attach.trim();
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body == null ? null : body.trim();
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign == null ? null : sign.trim();
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType == null ? null : signType.trim();
    }

    public String getGoodsTag() {
        return goodsTag;
    }

    public void setGoodsTag(String goodsTag) {
        this.goodsTag = goodsTag == null ? null : goodsTag.trim();
    }

    public String getSpbillCreateIp() {
        return spbillCreateIp;
    }

    public void setSpbillCreateIp(String spbillCreateIp) {
        this.spbillCreateIp = spbillCreateIp == null ? null : spbillCreateIp.trim();
    }

    public String getErrCodeDes() {
        return errCodeDes;
    }

    public void setErrCodeDes(String errCodeDes) {
        this.errCodeDes = errCodeDes == null ? null : errCodeDes.trim();
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode == null ? null : errCode.trim();
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode == null ? null : resultCode.trim();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId == null ? null : transactionId.trim();
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail == null ? null : detail.trim();
    }

    public String getTimeExpire() {
        return timeExpire;
    }

    public void setTimeExpire(String timeExpire) {
        this.timeExpire = timeExpire;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("OrderCharge{");
        sb.append("id=").append(id);
        sb.append(", appid='").append(appid).append('\'');
        sb.append(", mchId='").append(mchId).append('\'');
        sb.append(", openid='").append(openid).append('\'');
        sb.append(", outTradeNo='").append(outTradeNo).append('\'');
        sb.append(", deviceInfo='").append(deviceInfo).append('\'');
        sb.append(", nonceStr='").append(nonceStr).append('\'');
        sb.append(", prepayId='").append(prepayId).append('\'');
        sb.append(", tradeType='").append(tradeType).append('\'');
        sb.append(", totalFee=").append(totalFee);
        sb.append(", settlementTotalFee=").append(settlementTotalFee);
        sb.append(", feeType='").append(feeType).append('\'');
        sb.append(", cashFee=").append(cashFee);
        sb.append(", cashFeeType='").append(cashFeeType).append('\'');
        sb.append(", attach='").append(attach).append('\'');
        sb.append(", body='").append(body).append('\'');
        sb.append(", sign='").append(sign).append('\'');
        sb.append(", signType='").append(signType).append('\'');
        sb.append(", goodsTag='").append(goodsTag).append('\'');
        sb.append(", timeExpire='").append(timeExpire).append('\'');
        sb.append(", timeStart='").append(timeStart).append('\'');
        sb.append(", timeEnd='").append(timeEnd).append('\'');
        sb.append(", spbillCreateIp='").append(spbillCreateIp).append('\'');
        sb.append(", errCodeDes='").append(errCodeDes).append('\'');
        sb.append(", errCode='").append(errCode).append('\'');
        sb.append(", resultCode='").append(resultCode).append('\'');
        sb.append(", transactionId='").append(transactionId).append('\'');
        sb.append(", detail='").append(detail).append('\'');
        sb.append('}');
        return sb.toString();
    }
}