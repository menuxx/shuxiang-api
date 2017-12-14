/*
 * This file is generated by jOOQ.
*/
package com.menuxx.common.db.tables.pojos;


import java.io.Serializable;
import java.sql.Timestamp;

import javax.annotation.Generated;

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
public class TItem implements Serializable {

    private static final long serialVersionUID = -1677190690;

    private final UInteger  id;
    private final UInteger  merchantId;
    private final Integer   status;
    private final String    name;
    private final Integer   price;
    private final String    thumbImgs;
    private final String    isbn;
    private final String    press;
    private final String    authors;
    private final String    translators;
    private final String    describe;
    private final String    shopUrl;
    private final Timestamp createAt;
    private final Timestamp updateAt;

    public TItem(TItem value) {
        this.id = value.id;
        this.merchantId = value.merchantId;
        this.status = value.status;
        this.name = value.name;
        this.price = value.price;
        this.thumbImgs = value.thumbImgs;
        this.isbn = value.isbn;
        this.press = value.press;
        this.authors = value.authors;
        this.translators = value.translators;
        this.describe = value.describe;
        this.shopUrl = value.shopUrl;
        this.createAt = value.createAt;
        this.updateAt = value.updateAt;
    }

    public TItem(
        UInteger  id,
        UInteger  merchantId,
        Integer   status,
        String    name,
        Integer   price,
        String    thumbImgs,
        String    isbn,
        String    press,
        String    authors,
        String    translators,
        String    describe,
        String    shopUrl,
        Timestamp createAt,
        Timestamp updateAt
    ) {
        this.id = id;
        this.merchantId = merchantId;
        this.status = status;
        this.name = name;
        this.price = price;
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

    public UInteger getId() {
        return this.id;
    }

    public UInteger getMerchantId() {
        return this.merchantId;
    }

    public Integer getStatus() {
        return this.status;
    }

    public String getName() {
        return this.name;
    }

    public Integer getPrice() {
        return this.price;
    }

    public String getThumbImgs() {
        return this.thumbImgs;
    }

    public String getIsbn() {
        return this.isbn;
    }

    public String getPress() {
        return this.press;
    }

    public String getAuthors() {
        return this.authors;
    }

    public String getTranslators() {
        return this.translators;
    }

    public String getDescribe() {
        return this.describe;
    }

    public String getShopUrl() {
        return this.shopUrl;
    }

    public Timestamp getCreateAt() {
        return this.createAt;
    }

    public Timestamp getUpdateAt() {
        return this.updateAt;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TItem (");

        sb.append(id);
        sb.append(", ").append(merchantId);
        sb.append(", ").append(status);
        sb.append(", ").append(name);
        sb.append(", ").append(price);
        sb.append(", ").append(thumbImgs);
        sb.append(", ").append(isbn);
        sb.append(", ").append(press);
        sb.append(", ").append(authors);
        sb.append(", ").append(translators);
        sb.append(", ").append(describe);
        sb.append(", ").append(shopUrl);
        sb.append(", ").append(createAt);
        sb.append(", ").append(updateAt);

        sb.append(")");
        return sb.toString();
    }
}
