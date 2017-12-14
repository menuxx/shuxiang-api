/*
 * This file is generated by jOOQ.
*/
package com.menuxx.common.db.tables.records;


import com.menuxx.common.db.tables.TOrderCharge;

import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.UInteger;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.5"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TOrderChargeRecord extends UpdatableRecordImpl<TOrderChargeRecord> {

    private static final long serialVersionUID = 1198966246;

    /**
     * Setter for <code>onecode.t_order_charge.id</code>.
     */
    public TOrderChargeRecord setId(UInteger value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_order_charge.id</code>.
     */
    public UInteger getId() {
        return (UInteger) get(0);
    }

    /**
     * Setter for <code>onecode.t_order_charge.appid</code>. 微信分配的小程序ID
     */
    public TOrderChargeRecord setAppid(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_order_charge.appid</code>. 微信分配的小程序ID
     */
    public String getAppid() {
        return (String) get(1);
    }

    /**
     * Setter for <code>onecode.t_order_charge.mch_id</code>. 微信支付分配的商户号
     */
    public TOrderChargeRecord setMchId(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_order_charge.mch_id</code>. 微信支付分配的商户号
     */
    public String getMchId() {
        return (String) get(2);
    }

    /**
     * Setter for <code>onecode.t_order_charge.openid</code>. trade_type=JSAPI，此参数必传，用户在商户appid下的唯一标识。openid如何获取，可参考【获取openid】。
     */
    public TOrderChargeRecord setOpenid(String value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_order_charge.openid</code>. trade_type=JSAPI，此参数必传，用户在商户appid下的唯一标识。openid如何获取，可参考【获取openid】。
     */
    public String getOpenid() {
        return (String) get(3);
    }

    /**
     * Setter for <code>onecode.t_order_charge.out_trade_no</code>. 商户系统内部的订单号,32个字符内、可包含字母, 其他说明见商户订单号
     */
    public TOrderChargeRecord setOutTradeNo(String value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_order_charge.out_trade_no</code>. 商户系统内部的订单号,32个字符内、可包含字母, 其他说明见商户订单号
     */
    public String getOutTradeNo() {
        return (String) get(4);
    }

    /**
     * Setter for <code>onecode.t_order_charge.device_info</code>. 调用接口提交的终端设备号
     */
    public TOrderChargeRecord setDeviceInfo(String value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_order_charge.device_info</code>. 调用接口提交的终端设备号
     */
    public String getDeviceInfo() {
        return (String) get(5);
    }

    /**
     * Setter for <code>onecode.t_order_charge.nonce_str</code>. 微信返回的随机字符串
     */
    public TOrderChargeRecord setNonceStr(String value) {
        set(6, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_order_charge.nonce_str</code>. 微信返回的随机字符串
     */
    public String getNonceStr() {
        return (String) get(6);
    }

    /**
     * Setter for <code>onecode.t_order_charge.sign</code>. 微信返回的签名，详见签名算法
     */
    public TOrderChargeRecord setSign(String value) {
        set(7, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_order_charge.sign</code>. 微信返回的签名，详见签名算法
     */
    public String getSign() {
        return (String) get(7);
    }

    /**
     * Setter for <code>onecode.t_order_charge.prepay_id</code>. 微信生成的预支付回话标识，用于后续接口调用中使用，该值有效期为2小时
     */
    public TOrderChargeRecord setPrepayId(String value) {
        set(8, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_order_charge.prepay_id</code>. 微信生成的预支付回话标识，用于后续接口调用中使用，该值有效期为2小时
     */
    public String getPrepayId() {
        return (String) get(8);
    }

    /**
     * Setter for <code>onecode.t_order_charge.trade_type</code>. 小程序取值如下：JSAPI，详细说明见参数规定
     */
    public TOrderChargeRecord setTradeType(String value) {
        set(9, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_order_charge.trade_type</code>. 小程序取值如下：JSAPI，详细说明见参数规定
     */
    public String getTradeType() {
        return (String) get(9);
    }

    /**
     * Setter for <code>onecode.t_order_charge.total_fee</code>. 订单总金额，单位为分，详见支付金额
     */
    public TOrderChargeRecord setTotalFee(Integer value) {
        set(10, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_order_charge.total_fee</code>. 订单总金额，单位为分，详见支付金额
     */
    public Integer getTotalFee() {
        return (Integer) get(10);
    }

    /**
     * Setter for <code>onecode.t_order_charge.fee_type</code>. 符合ISO 4217标准的三位字母代码，默认人民币：CNY，其他值列表详见货币类型
     */
    public TOrderChargeRecord setFeeType(String value) {
        set(11, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_order_charge.fee_type</code>. 符合ISO 4217标准的三位字母代码，默认人民币：CNY，其他值列表详见货币类型
     */
    public String getFeeType() {
        return (String) get(11);
    }

    /**
     * Setter for <code>onecode.t_order_charge.attach</code>. 附加数据，在查询API和支付通知中原样返回，该字段主要用于商户携带订单的自定义数据
     */
    public TOrderChargeRecord setAttach(String value) {
        set(12, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_order_charge.attach</code>. 附加数据，在查询API和支付通知中原样返回，该字段主要用于商户携带订单的自定义数据
     */
    public String getAttach() {
        return (String) get(12);
    }

    /**
     * Setter for <code>onecode.t_order_charge.detail</code>. 单品优惠字段(详见单品优惠开发文档)
     */
    public TOrderChargeRecord setDetail(String value) {
        set(13, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_order_charge.detail</code>. 单品优惠字段(详见单品优惠开发文档)
     */
    public String getDetail() {
        return (String) get(13);
    }

    /**
     * Setter for <code>onecode.t_order_charge.body</code>. 商品简单描述，该字段须严格按照规范传递，具体请见参数规定
     */
    public TOrderChargeRecord setBody(String value) {
        set(14, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_order_charge.body</code>. 商品简单描述，该字段须严格按照规范传递，具体请见参数规定
     */
    public String getBody() {
        return (String) get(14);
    }

    /**
     * Setter for <code>onecode.t_order_charge.sign_type</code>. 签名类型，目前支持HMAC-SHA256和MD5，默认为MD5
     */
    public TOrderChargeRecord setSignType(String value) {
        set(15, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_order_charge.sign_type</code>. 签名类型，目前支持HMAC-SHA256和MD5，默认为MD5
     */
    public String getSignType() {
        return (String) get(15);
    }

    /**
     * Setter for <code>onecode.t_order_charge.limit_pay</code>. no_credit--指定不能使用信用卡支付
     */
    public TOrderChargeRecord setLimitPay(String value) {
        set(16, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_order_charge.limit_pay</code>. no_credit--指定不能使用信用卡支付
     */
    public String getLimitPay() {
        return (String) get(16);
    }

    /**
     * Setter for <code>onecode.t_order_charge.notify_url</code>. 接收微信支付异步通知回调地址，通知url必须为直接可访问的url，不能携带参数。
     */
    public TOrderChargeRecord setNotifyUrl(String value) {
        set(17, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_order_charge.notify_url</code>. 接收微信支付异步通知回调地址，通知url必须为直接可访问的url，不能携带参数。
     */
    public String getNotifyUrl() {
        return (String) get(17);
    }

    /**
     * Setter for <code>onecode.t_order_charge.goods_tag</code>. 商品标记，代金券或立减优惠功能的参数，说明详见代金券或立减优惠
     */
    public TOrderChargeRecord setGoodsTag(String value) {
        set(18, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_order_charge.goods_tag</code>. 商品标记，代金券或立减优惠功能的参数，说明详见代金券或立减优惠
     */
    public String getGoodsTag() {
        return (String) get(18);
    }

    /**
     * Setter for <code>onecode.t_order_charge.time_expire</code>. 订单失效时间，格式为yyyyMMddHHmmss，如2009年12月27日9点10分10秒表示为20091227091010。其他详见时间规则

注意：最短失效时间间隔必须大于5分钟
     */
    public TOrderChargeRecord setTimeExpire(Timestamp value) {
        set(19, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_order_charge.time_expire</code>. 订单失效时间，格式为yyyyMMddHHmmss，如2009年12月27日9点10分10秒表示为20091227091010。其他详见时间规则

注意：最短失效时间间隔必须大于5分钟
     */
    public Timestamp getTimeExpire() {
        return (Timestamp) get(19);
    }

    /**
     * Setter for <code>onecode.t_order_charge.time_start</code>. 订单生成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010。其他详见时间规则
     */
    public TOrderChargeRecord setTimeStart(Timestamp value) {
        set(20, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_order_charge.time_start</code>. 订单生成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010。其他详见时间规则
     */
    public Timestamp getTimeStart() {
        return (Timestamp) get(20);
    }

    /**
     * Setter for <code>onecode.t_order_charge.spbill_create_ip</code>. APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP。
     */
    public TOrderChargeRecord setSpbillCreateIp(String value) {
        set(21, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_order_charge.spbill_create_ip</code>. APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP。
     */
    public String getSpbillCreateIp() {
        return (String) get(21);
    }

    /**
     * Setter for <code>onecode.t_order_charge.err_code_des</code>. 错误返回的信息描述
     */
    public TOrderChargeRecord setErrCodeDes(String value) {
        set(22, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_order_charge.err_code_des</code>. 错误返回的信息描述
     */
    public String getErrCodeDes() {
        return (String) get(22);
    }

    /**
     * Setter for <code>onecode.t_order_charge.err_code</code>. 详细参见第6节错误列表
     */
    public TOrderChargeRecord setErrCode(String value) {
        set(23, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_order_charge.err_code</code>. 详细参见第6节错误列表
     */
    public String getErrCode() {
        return (String) get(23);
    }

    /**
     * Setter for <code>onecode.t_order_charge.result_code</code>. SUCCESS/FAIL
     */
    public TOrderChargeRecord setResultCode(String value) {
        set(24, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_order_charge.result_code</code>. SUCCESS/FAIL
     */
    public String getResultCode() {
        return (String) get(24);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<UInteger> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached TOrderChargeRecord
     */
    public TOrderChargeRecord() {
        super(TOrderCharge.T_ORDER_CHARGE);
    }

    /**
     * Create a detached, initialised TOrderChargeRecord
     */
    public TOrderChargeRecord(UInteger id, String appid, String mchId, String openid, String outTradeNo, String deviceInfo, String nonceStr, String sign, String prepayId, String tradeType, Integer totalFee, String feeType, String attach, String detail, String body, String signType, String limitPay, String notifyUrl, String goodsTag, Timestamp timeExpire, Timestamp timeStart, String spbillCreateIp, String errCodeDes, String errCode, String resultCode) {
        super(TOrderCharge.T_ORDER_CHARGE);

        set(0, id);
        set(1, appid);
        set(2, mchId);
        set(3, openid);
        set(4, outTradeNo);
        set(5, deviceInfo);
        set(6, nonceStr);
        set(7, sign);
        set(8, prepayId);
        set(9, tradeType);
        set(10, totalFee);
        set(11, feeType);
        set(12, attach);
        set(13, detail);
        set(14, body);
        set(15, signType);
        set(16, limitPay);
        set(17, notifyUrl);
        set(18, goodsTag);
        set(19, timeExpire);
        set(20, timeStart);
        set(21, spbillCreateIp);
        set(22, errCodeDes);
        set(23, errCode);
        set(24, resultCode);
    }
}