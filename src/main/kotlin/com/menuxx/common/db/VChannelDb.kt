package com.menuxx.common.db

import com.menuxx.NoArg
import com.menuxx.PageParam
import com.menuxx.common.bean.*
import com.menuxx.common.db.tables.TItem
import com.menuxx.common.db.tables.TOrder
import com.menuxx.common.db.tables.TUser
import com.menuxx.common.db.tables.TVChannel
import com.menuxx.miaosha.bean.ChannelItem
import com.menuxx.miaosha.exception.LaunchException
import com.menuxx.miaosha.store.ChannelItemStore
import com.menuxx.weixin.util.nullSkipUpdate
import org.jooq.DSLContext
import org.jooq.types.UInteger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/5
 * 微信: yin80871901
 */
@Service
class VChannelDb(
        private val dsl: DSLContext,
        private val channelItemDb: ChannelItemRecordDb
) {

    private final val tVipChannel = TVChannel.T_V_CHANNEL
    private final val tItem = TItem.T_ITEM
    private final val tOrder = TOrder.T_ORDER
    private final val tUser = TUser.T_USER

    val StatusCreated = 0
    val StatusStarted = 1
    val StatusFinishd = 2

    /**
     * 通过 id 获取简单对象
     */
    fun getSimpleById(channelId: Int) : VChannel? {
        return dsl.select().from(tVipChannel).where(tVipChannel.ID.eq(UInteger.valueOf(channelId))).fetchOneInto(VChannel::class.java)
    }

    fun getById(channelId: Int) : VChannel? {
        val channelItemRecord = dsl.select()
                .from(tVipChannel)
                .leftJoin(tItem).on(tVipChannel.ITEM_ID.eq(tItem.ID))
                .where(tVipChannel.ID.eq(UInteger.valueOf(channelId)))
                .fetchOne()

        if ( channelItemRecord != null ) {
            val channelItem = channelItemRecord.into(tVipChannel).into(VChannel::class.java)
            channelItem.item = channelItemRecord.into(tItem).into(Item::class.java)
            return channelItem
        }

        return null
    }

    fun loadVChannels(merchantId: Int, page: PageParam) : List<VChannel> {
        return dsl.select().from(tVipChannel)
                .leftJoin(tItem).on(tVipChannel.ITEM_ID.eq(tItem.ID))
                .orderBy(tVipChannel.CREATE_AT.desc())
                .offset(page.getOffset()).limit(page.getLimit())
                .fetchArray().map {
            val channel = it.into(tVipChannel).into(VChannel::class.java)
            val item = it.into(tItem).into(Item::class.java)
            channel.item = item
            channel
        }
    }

    /**
     * 新增渠道
     */
    fun insertVChannel(vChannel: VChannel) : VChannel {
        return dsl.insertInto(tVipChannel)
                .set( nullSkipUpdate(dsl.newRecord(tVipChannel, vChannel)) )
                .returning()
                .fetchOne()
                .into(VChannel::class.java)
    }

    /**
     * 更新渠道信息
     */
    @Transactional
    fun updateVChannel(channelId: Int, vChannel: VChannel) : VChannel? {
        dsl.update(tVipChannel)
                    .set( nullSkipUpdate(dsl.newRecord(tVipChannel, vChannel)) )
                    .where(tVipChannel.ID.eq(UInteger.valueOf(channelId))).execute()
        return getById(channelId)
    }

    fun updateToStarted(channelId: Int) {
        dsl.update(tVipChannel)
                .set(tVipChannel.STATUS, StatusStarted)
                .where(tVipChannel.ID.eq(UInteger.valueOf(channelId)))
                .execute()
    }

    val LaunchNoChannel = 101   // 渠道不存在
    val LaunchStatusFail = 102 // 状态不允许执行该操作
    val LaunchRecordRowsFail = 103 // 数据库记录条数不正确

    /**
     * 启动 一个 channel
     */
    @Transactional
    @Throws(LaunchException::class)
    fun launchChannel(channelId: Int) {
        // 获取渠道信息
        val channel = getById(channelId) ?: throw LaunchException(LaunchNoChannel, "渠道不存在")
        if ( channel.status != StatusCreated ) {
            throw LaunchException(LaunchStatusFail, "状态不允许执行该操作")
        }
        // 构建
        val items = (1..channel.stock).map { ChannelItemRecord(null, channelId, channel.itemId, null, null, null, null) }
        // 插入到数据库
        val arows = channelItemDb.addChannelBatch(items)
        if ( arows != channel.stock ) {
            throw LaunchException(LaunchRecordRowsFail, "数据库输入记录条数不正确")
        }
        // 从数据库读取, 以便获取 id
        val itemWithIds = channelItemDb.loadChannelItems(channelId).map { item ->
            ChannelItem(
                    id = item.id,
                    channelId = item.channelId,
                    itemId = item.itemId,
                    obtainUserId = item.obtainUserId,
                    obtainTime = item.obtainTime?.toInstant()
            )
        }
        if (itemWithIds.size != channel.stock) {
            throw LaunchException(LaunchRecordRowsFail, "数据库记录输出条数不正确")
        }
        // 存储到 渠道存储中
        ChannelItemStore.putChannelItems(1, channelId, itemWithIds)
        updateToStarted(channelId)
    }

    /**
     * 获取渠道指定用户的订单
     */
    fun getChannelUserOrder(userId: Int, channelId: Int) : Order? {
        return dsl.select().from(tOrder).where(tOrder.USER_ID.eq(UInteger.valueOf(userId)).and(tOrder.CHANNEL_ID.eq(UInteger.valueOf(channelId)))).fetchOneInto(Order::class.java)
    }

    /**
     * 获取渠道内用户的数量
     */
    fun getChannelOrderUsersCount(channelId: Int) : Int {
        return dsl.select().from(tOrder).where(
                tOrder.STATUS.greaterOrEqual(2)
                .and(tOrder.CHANNEL_ID.eq(UInteger.valueOf(channelId)))
        ).count()
    }

    /**
     * 获取渠道订单用户
     */
    @NoArg
    data class QueueNumUser(var queueNum: Int, var user: User, var updateAt: Date)
    fun loadChannelOrderUsers(channelId: Int, page: PageParam) : List<QueueNumUser> {
        return dsl.select()
                .from(tOrder)
                .leftJoin(tUser).on(tUser.ID.eq(tOrder.USER_ID))
                .where(
                        tOrder.STATUS.greaterOrEqual(2)
                                .and(tOrder.CHANNEL_ID.eq(UInteger.valueOf(channelId)))
                )
                .orderBy(tOrder.QUEUE_NUM.asc())
                .limit(page.getLimit()).offset(page.getOffset()).fetchArray().map {
            val order = it.into(tOrder).into(QueueNumUser::class.java)
            order.user = it.into(tUser).into(User::class.java)
            order
        }
    }

}