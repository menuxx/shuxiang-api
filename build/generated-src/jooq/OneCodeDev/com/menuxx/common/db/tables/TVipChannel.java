/*
 * This file is generated by jOOQ.
*/
package com.menuxx.common.db.tables;


import com.menuxx.common.db.Keys;
import com.menuxx.common.db.Onecode;
import com.menuxx.common.db.tables.records.TVipChannelRecord;

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
public class TVipChannel extends TableImpl<TVipChannelRecord> {

    private static final long serialVersionUID = -1671348316;

    /**
     * The reference instance of <code>onecode.t_vip_channel</code>
     */
    public static final TVipChannel T_VIP_CHANNEL = new TVipChannel();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TVipChannelRecord> getRecordType() {
        return TVipChannelRecord.class;
    }

    /**
     * The column <code>onecode.t_vip_channel.id</code>.
     */
    public final TableField<TVipChannelRecord, UInteger> ID = createField("id", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>onecode.t_vip_channel.express_fee</code>. 邮递费
     */
    public final TableField<TVipChannelRecord, Integer> EXPRESS_FEE = createField("express_fee", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "邮递费");

    /**
     * The column <code>onecode.t_vip_channel.pay_fee</code>. 需要支付的费用
     */
    public final TableField<TVipChannelRecord, Integer> PAY_FEE = createField("pay_fee", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "需要支付的费用");

    /**
     * The column <code>onecode.t_vip_channel.status</code>. 状态 0 创建，1 开始，2 结束
     */
    public final TableField<TVipChannelRecord, Integer> STATUS = createField("status", org.jooq.impl.SQLDataType.INTEGER.defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "状态 0 创建，1 开始，2 结束");

    /**
     * The column <code>onecode.t_vip_channel.stock</code>. 库存量
     */
    public final TableField<TVipChannelRecord, Integer> STOCK = createField("stock", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "库存量");

    /**
     * The column <code>onecode.t_vip_channel.owner_name</code>. 大v名字
     */
    public final TableField<TVipChannelRecord, String> OWNER_NAME = createField("owner_name", org.jooq.impl.SQLDataType.VARCHAR.length(30), this, "大v名字");

    /**
     * The column <code>onecode.t_vip_channel.owner_avatar_url</code>. 头像 images/vip-channels/
     */
    public final TableField<TVipChannelRecord, String> OWNER_AVATAR_URL = createField("owner_avatar_url", org.jooq.impl.SQLDataType.VARCHAR.length(300), this, "头像 images/vip-channels/");

    /**
     * The column <code>onecode.t_vip_channel.gift_txt</code>. 赠言
     */
    public final TableField<TVipChannelRecord, String> GIFT_TXT = createField("gift_txt", org.jooq.impl.SQLDataType.VARCHAR.length(3000).nullable(false).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "赠言");

    /**
     * The column <code>onecode.t_vip_channel.item_id</code>. 对应的商品(图书)id
     */
    public final TableField<TVipChannelRecord, UInteger> ITEM_ID = createField("item_id", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "对应的商品(图书)id");

    /**
     * The column <code>onecode.t_vip_channel.merchant_id</code>.
     */
    public final TableField<TVipChannelRecord, UInteger> MERCHANT_ID = createField("merchant_id", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>onecode.t_vip_channel.create_at</code>. 渠道创建时间
     */
    public final TableField<TVipChannelRecord, Timestamp> CREATE_AT = createField("create_at", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.inline("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "渠道创建时间");

    /**
     * The column <code>onecode.t_vip_channel.update_at</code>. 更新时间
     */
    public final TableField<TVipChannelRecord, Timestamp> UPDATE_AT = createField("update_at", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.inline("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "更新时间");

    /**
     * The column <code>onecode.t_vip_channel.expired_time</code>. 渠道过去时间
     */
    public final TableField<TVipChannelRecord, Timestamp> EXPIRED_TIME = createField("expired_time", org.jooq.impl.SQLDataType.TIMESTAMP, this, "渠道过去时间");

    /**
     * The column <code>onecode.t_vip_channel.start_time</code>. 渠道开始时间
     */
    public final TableField<TVipChannelRecord, Timestamp> START_TIME = createField("start_time", org.jooq.impl.SQLDataType.TIMESTAMP, this, "渠道开始时间");

    /**
     * Create a <code>onecode.t_vip_channel</code> table reference
     */
    public TVipChannel() {
        this("t_vip_channel", null);
    }

    /**
     * Create an aliased <code>onecode.t_vip_channel</code> table reference
     */
    public TVipChannel(String alias) {
        this(alias, T_VIP_CHANNEL);
    }

    private TVipChannel(String alias, Table<TVipChannelRecord> aliased) {
        this(alias, aliased, null);
    }

    private TVipChannel(String alias, Table<TVipChannelRecord> aliased, Field<?>[] parameters) {
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
    public Identity<TVipChannelRecord, UInteger> getIdentity() {
        return Keys.IDENTITY_T_VIP_CHANNEL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<TVipChannelRecord> getPrimaryKey() {
        return Keys.KEY_T_VIP_CHANNEL_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<TVipChannelRecord>> getKeys() {
        return Arrays.<UniqueKey<TVipChannelRecord>>asList(Keys.KEY_T_VIP_CHANNEL_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TVipChannel as(String alias) {
        return new TVipChannel(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public TVipChannel rename(String name) {
        return new TVipChannel(name, null);
    }
}
