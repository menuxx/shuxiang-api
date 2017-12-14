/*
 * This file is generated by jOOQ.
*/
package com.menuxx.common.db.tables.records;


import com.menuxx.common.db.tables.TAdminUser;

import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record10;
import org.jooq.Row10;
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
public class TAdminUserRecord extends UpdatableRecordImpl<TAdminUserRecord> implements Record10<UInteger, String, String, Timestamp, String, Integer, Integer, Integer, Timestamp, Timestamp> {

    private static final long serialVersionUID = 89136965;

    /**
     * Setter for <code>onecode.t_admin_user.id</code>.
     */
    public TAdminUserRecord setId(UInteger value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_admin_user.id</code>.
     */
    public UInteger getId() {
        return (UInteger) get(0);
    }

    /**
     * Setter for <code>onecode.t_admin_user.username</code>. 用户名
     */
    public TAdminUserRecord setUsername(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_admin_user.username</code>. 用户名
     */
    public String getUsername() {
        return (String) get(1);
    }

    /**
     * Setter for <code>onecode.t_admin_user.password</code>. 密码
     */
    public TAdminUserRecord setPassword(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_admin_user.password</code>. 密码
     */
    public String getPassword() {
        return (String) get(2);
    }

    /**
     * Setter for <code>onecode.t_admin_user.last_login_time</code>. 最后登录时间
     */
    public TAdminUserRecord setLastLoginTime(Timestamp value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_admin_user.last_login_time</code>. 最后登录时间
     */
    public Timestamp getLastLoginTime() {
        return (Timestamp) get(3);
    }

    /**
     * Setter for <code>onecode.t_admin_user.last_login_ip</code>. 最后登录的ip
     */
    public TAdminUserRecord setLastLoginIp(String value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_admin_user.last_login_ip</code>. 最后登录的ip
     */
    public String getLastLoginIp() {
        return (String) get(4);
    }

    /**
     * Setter for <code>onecode.t_admin_user.salt</code>. 盐
     */
    public TAdminUserRecord setSalt(Integer value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_admin_user.salt</code>. 盐
     */
    public Integer getSalt() {
        return (Integer) get(5);
    }

    /**
     * Setter for <code>onecode.t_admin_user.status</code>. -1 删除
     */
    public TAdminUserRecord setStatus(Integer value) {
        set(6, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_admin_user.status</code>. -1 删除
     */
    public Integer getStatus() {
        return (Integer) get(6);
    }

    /**
     * Setter for <code>onecode.t_admin_user.enable</code>. 1 启用，0 禁用
     */
    public TAdminUserRecord setEnable(Integer value) {
        set(7, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_admin_user.enable</code>. 1 启用，0 禁用
     */
    public Integer getEnable() {
        return (Integer) get(7);
    }

    /**
     * Setter for <code>onecode.t_admin_user.create_at</code>. 创建时间
     */
    public TAdminUserRecord setCreateAt(Timestamp value) {
        set(8, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_admin_user.create_at</code>. 创建时间
     */
    public Timestamp getCreateAt() {
        return (Timestamp) get(8);
    }

    /**
     * Setter for <code>onecode.t_admin_user.update_at</code>. 更新时间
     */
    public TAdminUserRecord setUpdateAt(Timestamp value) {
        set(9, value);
        return this;
    }

    /**
     * Getter for <code>onecode.t_admin_user.update_at</code>. 更新时间
     */
    public Timestamp getUpdateAt() {
        return (Timestamp) get(9);
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
    // Record10 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row10<UInteger, String, String, Timestamp, String, Integer, Integer, Integer, Timestamp, Timestamp> fieldsRow() {
        return (Row10) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row10<UInteger, String, String, Timestamp, String, Integer, Integer, Integer, Timestamp, Timestamp> valuesRow() {
        return (Row10) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UInteger> field1() {
        return TAdminUser.T_ADMIN_USER.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return TAdminUser.T_ADMIN_USER.USERNAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return TAdminUser.T_ADMIN_USER.PASSWORD;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field4() {
        return TAdminUser.T_ADMIN_USER.LAST_LOGIN_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return TAdminUser.T_ADMIN_USER.LAST_LOGIN_IP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field6() {
        return TAdminUser.T_ADMIN_USER.SALT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field7() {
        return TAdminUser.T_ADMIN_USER.STATUS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field8() {
        return TAdminUser.T_ADMIN_USER.ENABLE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field9() {
        return TAdminUser.T_ADMIN_USER.CREATE_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field10() {
        return TAdminUser.T_ADMIN_USER.UPDATE_AT;
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
        return getUsername();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getPassword();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value4() {
        return getLastLoginTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getLastLoginIp();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value6() {
        return getSalt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value7() {
        return getStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value8() {
        return getEnable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value9() {
        return getCreateAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value10() {
        return getUpdateAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TAdminUserRecord value1(UInteger value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TAdminUserRecord value2(String value) {
        setUsername(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TAdminUserRecord value3(String value) {
        setPassword(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TAdminUserRecord value4(Timestamp value) {
        setLastLoginTime(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TAdminUserRecord value5(String value) {
        setLastLoginIp(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TAdminUserRecord value6(Integer value) {
        setSalt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TAdminUserRecord value7(Integer value) {
        setStatus(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TAdminUserRecord value8(Integer value) {
        setEnable(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TAdminUserRecord value9(Timestamp value) {
        setCreateAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TAdminUserRecord value10(Timestamp value) {
        setUpdateAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TAdminUserRecord values(UInteger value1, String value2, String value3, Timestamp value4, String value5, Integer value6, Integer value7, Integer value8, Timestamp value9, Timestamp value10) {
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
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached TAdminUserRecord
     */
    public TAdminUserRecord() {
        super(TAdminUser.T_ADMIN_USER);
    }

    /**
     * Create a detached, initialised TAdminUserRecord
     */
    public TAdminUserRecord(UInteger id, String username, String password, Timestamp lastLoginTime, String lastLoginIp, Integer salt, Integer status, Integer enable, Timestamp createAt, Timestamp updateAt) {
        super(TAdminUser.T_ADMIN_USER);

        set(0, id);
        set(1, username);
        set(2, password);
        set(3, lastLoginTime);
        set(4, lastLoginIp);
        set(5, salt);
        set(6, status);
        set(7, enable);
        set(8, createAt);
        set(9, updateAt);
    }
}
