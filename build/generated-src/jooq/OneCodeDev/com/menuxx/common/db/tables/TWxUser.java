/*
 * This file is generated by jOOQ.
*/
package com.menuxx.common.db.tables;


import com.menuxx.common.db.Keys;
import com.menuxx.common.db.Onecode;
import com.menuxx.common.db.tables.records.TWxUserRecord;

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
public class TWxUser extends TableImpl<TWxUserRecord> {

    private static final long serialVersionUID = -474861073;

    /**
     * The reference instance of <code>onecode.t_wx_user</code>
     */
    public static final TWxUser T_WX_USER = new TWxUser();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TWxUserRecord> getRecordType() {
        return TWxUserRecord.class;
    }

    /**
     * The column <code>onecode.t_wx_user.id</code>.
     */
    public final TableField<TWxUserRecord, UInteger> ID = createField("id", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>onecode.t_wx_user.nickname</code>. 昵称
     */
    public final TableField<TWxUserRecord, String> NICKNAME = createField("nickname", org.jooq.impl.SQLDataType.VARCHAR.length(50).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "昵称");

    /**
     * The column <code>onecode.t_wx_user.headimgurl</code>. 头像
     */
    public final TableField<TWxUserRecord, String> HEADIMGURL = createField("headimgurl", org.jooq.impl.SQLDataType.VARCHAR.length(300), this, "头像");

    /**
     * The column <code>onecode.t_wx_user.openid</code>. 微信唯一识别id
     */
    public final TableField<TWxUserRecord, String> OPENID = createField("openid", org.jooq.impl.SQLDataType.VARCHAR.length(128).nullable(false).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "微信唯一识别id");

    /**
     * The column <code>onecode.t_wx_user.country</code>. 县区
     */
    public final TableField<TWxUserRecord, String> COUNTRY = createField("country", org.jooq.impl.SQLDataType.VARCHAR.length(20), this, "县区");

    /**
     * The column <code>onecode.t_wx_user.province</code>. 省份
     */
    public final TableField<TWxUserRecord, String> PROVINCE = createField("province", org.jooq.impl.SQLDataType.VARCHAR.length(20), this, "省份");

    /**
     * The column <code>onecode.t_wx_user.city</code>. 城市
     */
    public final TableField<TWxUserRecord, String> CITY = createField("city", org.jooq.impl.SQLDataType.VARCHAR.length(20), this, "城市");

    /**
     * The column <code>onecode.t_wx_user.sex</code>. 性别
     */
    public final TableField<TWxUserRecord, Integer> SEX = createField("sex", org.jooq.impl.SQLDataType.INTEGER, this, "性别");

    /**
     * The column <code>onecode.t_wx_user.unionid</code>. 同一体系相共享id
     */
    public final TableField<TWxUserRecord, String> UNIONID = createField("unionid", org.jooq.impl.SQLDataType.VARCHAR.length(128).nullable(false).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "同一体系相共享id");

    /**
     * The column <code>onecode.t_wx_user.create_at</code>. 创建时间
     */
    public final TableField<TWxUserRecord, Timestamp> CREATE_AT = createField("create_at", org.jooq.impl.SQLDataType.TIMESTAMP, this, "创建时间");

    /**
     * The column <code>onecode.t_wx_user.update_at</code>. 更新时间
     */
    public final TableField<TWxUserRecord, Timestamp> UPDATE_AT = createField("update_at", org.jooq.impl.SQLDataType.TIMESTAMP, this, "更新时间");

    /**
     * Create a <code>onecode.t_wx_user</code> table reference
     */
    public TWxUser() {
        this("t_wx_user", null);
    }

    /**
     * Create an aliased <code>onecode.t_wx_user</code> table reference
     */
    public TWxUser(String alias) {
        this(alias, T_WX_USER);
    }

    private TWxUser(String alias, Table<TWxUserRecord> aliased) {
        this(alias, aliased, null);
    }

    private TWxUser(String alias, Table<TWxUserRecord> aliased, Field<?>[] parameters) {
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
    public Identity<TWxUserRecord, UInteger> getIdentity() {
        return Keys.IDENTITY_T_WX_USER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<TWxUserRecord> getPrimaryKey() {
        return Keys.KEY_T_WX_USER_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<TWxUserRecord>> getKeys() {
        return Arrays.<UniqueKey<TWxUserRecord>>asList(Keys.KEY_T_WX_USER_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TWxUser as(String alias) {
        return new TWxUser(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public TWxUser rename(String name) {
        return new TWxUser(name, null);
    }
}
