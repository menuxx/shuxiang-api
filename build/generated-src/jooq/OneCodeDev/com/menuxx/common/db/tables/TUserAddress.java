/*
 * This file is generated by jOOQ.
*/
package com.menuxx.common.db.tables;


import com.menuxx.common.db.Keys;
import com.menuxx.common.db.Onecode;
import com.menuxx.common.db.tables.records.TUserAddressRecord;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Identity;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;
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
public class TUserAddress extends TableImpl<TUserAddressRecord> {

    private static final long serialVersionUID = -908715913;

    /**
     * The reference instance of <code>onecode.t_user_address</code>
     */
    public static final TUserAddress T_USER_ADDRESS = new TUserAddress();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TUserAddressRecord> getRecordType() {
        return TUserAddressRecord.class;
    }

    /**
     * The column <code>onecode.t_user_address.id</code>.
     */
    public final TableField<TUserAddressRecord, UInteger> ID = createField("id", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>onecode.t_user_address.user_id</code>. 关联用户id
     */
    public final TableField<TUserAddressRecord, UInteger> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.INTEGERUNSIGNED, this, "关联用户id");

    /**
     * The column <code>onecode.t_user_address.default_apply</code>. 默认使用
     */
    public final TableField<TUserAddressRecord, Integer> DEFAULT_APPLY = createField("default_apply", org.jooq.impl.SQLDataType.INTEGER, this, "默认使用");

    /**
     * The column <code>onecode.t_user_address.city</code>. 城市
     */
    public final TableField<TUserAddressRecord, String> CITY = createField("city", org.jooq.impl.SQLDataType.VARCHAR.length(50).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "城市");

    /**
     * The column <code>onecode.t_user_address.country</code>. 县区
     */
    public final TableField<TUserAddressRecord, String> COUNTRY = createField("country", org.jooq.impl.SQLDataType.VARCHAR.length(50), this, "县区");

    /**
     * The column <code>onecode.t_user_address.province</code>. 省
     */
    public final TableField<TUserAddressRecord, String> PROVINCE = createField("province", org.jooq.impl.SQLDataType.VARCHAR.length(50).nullable(false).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "省");

    /**
     * The column <code>onecode.t_user_address.detail_info</code>. 详细收货地址信息
     */
    public final TableField<TUserAddressRecord, String> DETAIL_INFO = createField("detail_info", org.jooq.impl.SQLDataType.VARCHAR.length(200).nullable(false).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "详细收货地址信息");

    /**
     * The column <code>onecode.t_user_address.tel_number</code>. 联系电话
     */
    public final TableField<TUserAddressRecord, String> TEL_NUMBER = createField("tel_number", org.jooq.impl.SQLDataType.VARCHAR.length(16).nullable(false).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "联系电话");

    /**
     * The column <code>onecode.t_user_address.postal_code</code>. 邮编
     */
    public final TableField<TUserAddressRecord, String> POSTAL_CODE = createField("postal_code", org.jooq.impl.SQLDataType.VARCHAR.length(50).nullable(false).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "邮编");

    /**
     * The column <code>onecode.t_user_address.receiver_name</code>. 收货人姓名
     */
    public final TableField<TUserAddressRecord, String> RECEIVER_NAME = createField("receiver_name", org.jooq.impl.SQLDataType.VARCHAR.length(50).nullable(false).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "收货人姓名");

    /**
     * The column <code>onecode.t_user_address.primary</code>. 首选地址标注
     */
    public final TableField<TUserAddressRecord, Integer> PRIMARY = createField("primary", org.jooq.impl.SQLDataType.INTEGER, this, "首选地址标注");

    /**
     * The column <code>onecode.t_user_address.status</code>. 0 已删除
     */
    public final TableField<TUserAddressRecord, Integer> STATUS = createField("status", org.jooq.impl.SQLDataType.INTEGER, this, "0 已删除");

    /**
     * The column <code>onecode.t_user_address.create_at</code>.
     */
    public final TableField<TUserAddressRecord, Timestamp> CREATE_AT = createField("create_at", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.inline("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * The column <code>onecode.t_user_address.update_at</code>.
     */
    public final TableField<TUserAddressRecord, Timestamp> UPDATE_AT = createField("update_at", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

    /**
     * Create a <code>onecode.t_user_address</code> table reference
     */
    public TUserAddress() {
        this("t_user_address", null);
    }

    /**
     * Create an aliased <code>onecode.t_user_address</code> table reference
     */
    public TUserAddress(String alias) {
        this(alias, T_USER_ADDRESS);
    }

    private TUserAddress(String alias, Table<TUserAddressRecord> aliased) {
        this(alias, aliased, null);
    }

    private TUserAddress(String alias, Table<TUserAddressRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Onecode.ONECODE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<TUserAddressRecord, UInteger> getIdentity() {
        return Keys.IDENTITY_T_USER_ADDRESS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<TUserAddressRecord> getPrimaryKey() {
        return Keys.KEY_T_USER_ADDRESS_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<TUserAddressRecord>> getKeys() {
        return Arrays.<UniqueKey<TUserAddressRecord>>asList(Keys.KEY_T_USER_ADDRESS_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TUserAddress as(String alias) {
        return new TUserAddress(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public TUserAddress rename(String name) {
        return new TUserAddress(name, null);
    }
}