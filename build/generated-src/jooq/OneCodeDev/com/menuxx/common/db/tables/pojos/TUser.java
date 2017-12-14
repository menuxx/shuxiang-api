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
public class TUser implements Serializable {

    private static final long serialVersionUID = 246526306;

    private final UInteger  id;
    private final String    userName;
    private final String    phoneNumber;
    private final Timestamp lastLoginTime;
    private final String    lastLoginIp;
    private final UInteger  wxUserId;
    private final String    avatarUrl;
    private final Integer   enable;
    private final Timestamp updateAt;
    private final Timestamp createAt;

    public TUser(TUser value) {
        this.id = value.id;
        this.userName = value.userName;
        this.phoneNumber = value.phoneNumber;
        this.lastLoginTime = value.lastLoginTime;
        this.lastLoginIp = value.lastLoginIp;
        this.wxUserId = value.wxUserId;
        this.avatarUrl = value.avatarUrl;
        this.enable = value.enable;
        this.updateAt = value.updateAt;
        this.createAt = value.createAt;
    }

    public TUser(
        UInteger  id,
        String    userName,
        String    phoneNumber,
        Timestamp lastLoginTime,
        String    lastLoginIp,
        UInteger  wxUserId,
        String    avatarUrl,
        Integer   enable,
        Timestamp updateAt,
        Timestamp createAt
    ) {
        this.id = id;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.lastLoginTime = lastLoginTime;
        this.lastLoginIp = lastLoginIp;
        this.wxUserId = wxUserId;
        this.avatarUrl = avatarUrl;
        this.enable = enable;
        this.updateAt = updateAt;
        this.createAt = createAt;
    }

    public UInteger getId() {
        return this.id;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public Timestamp getLastLoginTime() {
        return this.lastLoginTime;
    }

    public String getLastLoginIp() {
        return this.lastLoginIp;
    }

    public UInteger getWxUserId() {
        return this.wxUserId;
    }

    public String getAvatarUrl() {
        return this.avatarUrl;
    }

    public Integer getEnable() {
        return this.enable;
    }

    public Timestamp getUpdateAt() {
        return this.updateAt;
    }

    public Timestamp getCreateAt() {
        return this.createAt;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TUser (");

        sb.append(id);
        sb.append(", ").append(userName);
        sb.append(", ").append(phoneNumber);
        sb.append(", ").append(lastLoginTime);
        sb.append(", ").append(lastLoginIp);
        sb.append(", ").append(wxUserId);
        sb.append(", ").append(avatarUrl);
        sb.append(", ").append(enable);
        sb.append(", ").append(updateAt);
        sb.append(", ").append(createAt);

        sb.append(")");
        return sb.toString();
    }
}