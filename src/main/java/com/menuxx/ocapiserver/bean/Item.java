package com.menuxx.ocapiserver.bean;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class Item {

    public Item() {
    }

    public Item(Integer id, Integer merchantId, Integer status, String name, String thumbImgs, String isbn, String press, String authors, String translators, String describe, String shopUrl, Date createAt, Date updateAt) {
        this.id = id;
        this.merchantId = merchantId;
        this.status = status;
        this.name = name;
        this.thumbImgs = thumbImgs;
        this.isbn = isbn;
        this.press = press;
        this.authors = authors;
        this.translators = translators;
        this.describe = describe;
        this.shopUrl = shopUrl;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    private Integer id;

    private Integer merchantId;

    private Integer status;

    @NotNull
    private String name;

    private String thumbImgs;

    @NotNull
    private String isbn;

    @NotNull
    private String press;

    @NotNull
    private String authors;

    @NotNull
    private String translators;

    private String describe;

    @NotNull
    private String shopUrl;

    private Date createAt;

    private Date updateAt;

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getThumbImgs() {
        return thumbImgs;
    }

    public void setThumbImgs(String thumbImgs) {
        this.thumbImgs = thumbImgs == null ? null : thumbImgs.trim();
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn == null ? null : isbn.trim();
    }

    public String getPress() {
        return press;
    }

    public void setPress(String press) {
        this.press = press == null ? null : press.trim();
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors == null ? null : authors.trim();
    }

    public String getTranslators() {
        return translators;
    }

    public void setTranslators(String translators) {
        this.translators = translators;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe == null ? null : describe.trim();
    }

    public String getShopUrl() {
        return shopUrl;
    }

    public void setShopUrl(String shopUrl) {
        this.shopUrl = shopUrl;
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