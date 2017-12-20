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
public class TWxUser implements Serializable {

    private static final long serialVersionUID = 555371453;

    private final UInteger  id;
    private final String    nickname;
    private final String    headimgurl;
    private final String    openid;
    private final String    refreshToken;
    private final String    country;
    private final String    province;
    private final String    city;
    private final Integer   sex;
    private final String    unionid;
    private final Timestamp createAt;
    private final Timestamp updateAt;

    public TWxUser(TWxUser value) {
        this.id = value.id;
        this.nickname = value.nickname;
        this.headimgurl = value.headimgurl;
        this.openid = value.openid;
        this.refreshToken = value.refreshToken;
        this.country = value.country;
        this.province = value.province;
        this.city = value.city;
        this.sex = value.sex;
        this.unionid = value.unionid;
        this.createAt = value.createAt;
        this.updateAt = value.updateAt;
    }

    public TWxUser(
        UInteger  id,
        String    nickname,
        String    headimgurl,
        String    openid,
        String    refreshToken,
        String    country,
        String    province,
        String    city,
        Integer   sex,
        String    unionid,
        Timestamp createAt,
        Timestamp updateAt
    ) {
        this.id = id;
        this.nickname = nickname;
        this.headimgurl = headimgurl;
        this.openid = openid;
        this.refreshToken = refreshToken;
        this.country = country;
        this.province = province;
        this.city = city;
        this.sex = sex;
        this.unionid = unionid;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    public UInteger getId() {
        return this.id;
    }

    public String getNickname() {
        return this.nickname;
    }

    public String getHeadimgurl() {
        return this.headimgurl;
    }

    public String getOpenid() {
        return this.openid;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

    public String getCountry() {
        return this.country;
    }

    public String getProvince() {
        return this.province;
    }

    public String getCity() {
        return this.city;
    }

    public Integer getSex() {
        return this.sex;
    }

    public String getUnionid() {
        return this.unionid;
    }

    public Timestamp getCreateAt() {
        return this.createAt;
    }

    public Timestamp getUpdateAt() {
        return this.updateAt;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TWxUser (");

        sb.append(id);
        sb.append(", ").append(nickname);
        sb.append(", ").append(headimgurl);
        sb.append(", ").append(openid);
        sb.append(", ").append(refreshToken);
        sb.append(", ").append(country);
        sb.append(", ").append(province);
        sb.append(", ").append(city);
        sb.append(", ").append(sex);
        sb.append(", ").append(unionid);
        sb.append(", ").append(createAt);
        sb.append(", ").append(updateAt);

        sb.append(")");
        return sb.toString();
    }
}
