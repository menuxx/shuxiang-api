/*
 * This file is generated by jOOQ.
*/
package com.menuxx.common.db.tables.records;


import com.menuxx.common.db.tables.TWxUser;

import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record11;
import org.jooq.Row11;
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
public class TWxUserRecord extends UpdatableRecordImpl<TWxUserRecord> implements Record11<UInteger, String, String, String, String, String, String, Integer, String, Timestamp, Timestamp> {

    private static final long serialVersionUID = -330315684;

    /**
     * Setter for <code>onecode.t_wx_user.id</code>.
     */
    public TWxUserRecord setId(UInteger value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_wx_user.id</code>.
     */
    public UInteger getId() {
        return (UInteger) get(0);
    }

    /**
     * Setter for <code>onecode.t_wx_user.nickname</code>. 昵称
     */
    public TWxUserRecord setNickname(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_wx_user.nickname</code>. 昵称
     */
    public String getNickname() {
        return (String) get(1);
    }

    /**
     * Setter for <code>onecode.t_wx_user.headimgurl</code>. 头像
     */
    public TWxUserRecord setHeadimgurl(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_wx_user.headimgurl</code>. 头像
     */
    public String getHeadimgurl() {
        return (String) get(2);
    }

    /**
     * Setter for <code>onecode.t_wx_user.openid</code>. 微信唯一识别id
     */
    public TWxUserRecord setOpenid(String value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_wx_user.openid</code>. 微信唯一识别id
     */
    public String getOpenid() {
        return (String) get(3);
    }

    /**
     * Setter for <code>onecode.t_wx_user.country</code>. 县区
     */
    public TWxUserRecord setCountry(String value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_wx_user.country</code>. 县区
     */
    public String getCountry() {
        return (String) get(4);
    }

    /**
     * Setter for <code>onecode.t_wx_user.province</code>. 省份
     */
    public TWxUserRecord setProvince(String value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_wx_user.province</code>. 省份
     */
    public String getProvince() {
        return (String) get(5);
    }

    /**
     * Setter for <code>onecode.t_wx_user.city</code>. 城市
     */
    public TWxUserRecord setCity(String value) {
        set(6, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_wx_user.city</code>. 城市
     */
    public String getCity() {
        return (String) get(6);
    }

    /**
     * Setter for <code>onecode.t_wx_user.sex</code>. 性别
     */
    public TWxUserRecord setSex(Integer value) {
        set(7, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_wx_user.sex</code>. 性别
     */
    public Integer getSex() {
        return (Integer) get(7);
    }

    /**
     * Setter for <code>onecode.t_wx_user.unionid</code>. 同一体系相共享id
     */
    public TWxUserRecord setUnionid(String value) {
        set(8, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_wx_user.unionid</code>. 同一体系相共享id
     */
    public String getUnionid() {
        return (String) get(8);
    }

    /**
     * Setter for <code>onecode.t_wx_user.create_at</code>. 创建时间
     */
    public TWxUserRecord setCreateAt(Timestamp value) {
        set(9, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_wx_user.create_at</code>. 创建时间
     */
    public Timestamp getCreateAt() {
        return (Timestamp) get(9);
    }

    /**
     * Setter for <code>onecode.t_wx_user.update_at</code>. 更新时间
     */
    public TWxUserRecord setUpdateAt(Timestamp value) {
        set(10, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_wx_user.update_at</code>. 更新时间
     */
    public Timestamp getUpdateAt() {
        return (Timestamp) get(10);
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
    // Record11 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row11<UInteger, String, String, String, String, String, String, Integer, String, Timestamp, Timestamp> fieldsRow() {
        return (Row11) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row11<UInteger, String, String, String, String, String, String, Integer, String, Timestamp, Timestamp> valuesRow() {
        return (Row11) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UInteger> field1() {
        return TWxUser.T_WX_USER.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return TWxUser.T_WX_USER.NICKNAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return TWxUser.T_WX_USER.HEADIMGURL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return TWxUser.T_WX_USER.OPENID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return TWxUser.T_WX_USER.COUNTRY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return TWxUser.T_WX_USER.PROVINCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field7() {
        return TWxUser.T_WX_USER.CITY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field8() {
        return TWxUser.T_WX_USER.SEX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field9() {
        return TWxUser.T_WX_USER.UNIONID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field10() {
        return TWxUser.T_WX_USER.CREATE_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field11() {
        return TWxUser.T_WX_USER.UPDATE_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger value1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getNickname();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getHeadimgurl();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getOpenid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getCountry();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getProvince();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value7() {
        return getCity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value8() {
        return getSex();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value9() {
        return getUnionid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value10() {
        return getCreateAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value11() {
        return getUpdateAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TWxUserRecord value1(UInteger value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TWxUserRecord value2(String value) {
        setNickname(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TWxUserRecord value3(String value) {
        setHeadimgurl(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TWxUserRecord value4(String value) {
        setOpenid(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TWxUserRecord value5(String value) {
        setCountry(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TWxUserRecord value6(String value) {
        setProvince(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TWxUserRecord value7(String value) {
        setCity(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TWxUserRecord value8(Integer value) {
        setSex(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TWxUserRecord value9(String value) {
        setUnionid(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TWxUserRecord value10(Timestamp value) {
        setCreateAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TWxUserRecord value11(Timestamp value) {
        setUpdateAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TWxUserRecord values(UInteger value1, String value2, String value3, String value4, String value5, String value6, String value7, Integer value8, String value9, Timestamp value10, Timestamp value11) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached TWxUserRecord
     */
    public TWxUserRecord() {
        super(TWxUser.T_WX_USER);
    }

    /**
     * Create a detached, initialised TWxUserRecord
     */
    public TWxUserRecord(UInteger id, String nickname, String headimgurl, String openid, String country, String province, String city, Integer sex, String unionid, Timestamp createAt, Timestamp updateAt) {
        super(TWxUser.T_WX_USER);

        set(0, id);
        set(1, nickname);
        set(2, headimgurl);
        set(3, openid);
        set(4, country);
        set(5, province);
        set(6, city);
        set(7, sex);
        set(8, unionid);
        set(9, createAt);
        set(10, updateAt);
    }
}