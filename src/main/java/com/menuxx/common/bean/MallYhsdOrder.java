package com.menuxx.common.bean;

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2018/1/31
 * 微信: yin80871901
 */
public class MallYhsdOrder {

	private Integer id;

	private String localOrderNo;

	private String orderId;

	private Integer userId;

	private Integer totalAmount;

	private String paymentMethodType;

	private String metaFields;

	private String shipments;

	private Integer addressId;

	private String itemNames;

	private Integer paymentMethodId;

	public String getItemNames() {
		return itemNames;
	}

	public void setItemNames(String itemNames) {
		this.itemNames = itemNames;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Integer totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getMetaFields() {
		return metaFields;
	}

	public void setMetaFields(String metaFields) {
		this.metaFields = metaFields;
	}

	public String getShipments() {
		return shipments;
	}

	public void setShipments(String shipments) {
		this.shipments = shipments;
	}

	public Integer getAddressId() {
		return addressId;
	}

	public void setAddressId(Integer addressId) {
		this.addressId = addressId;
	}

	public Integer getPaymentMethodId() {
		return paymentMethodId;
	}

	public void setPaymentMethodId(Integer paymentMethodId) {
		this.paymentMethodId = paymentMethodId;
	}

	public String getPaymentMethodType() {
		return paymentMethodType;
	}

	public void setPaymentMethodType(String paymentMethodType) {
		this.paymentMethodType = paymentMethodType;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLocalOrderNo() {
		return localOrderNo;
	}

	public void setLocalOrderNo(String orderNo) {
		this.localOrderNo = orderNo;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String yhsdOrderId) {
		this.orderId = yhsdOrderId;
	}

}
