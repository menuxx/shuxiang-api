package com.menuxx.common.db

import com.menuxx.apiserver.PageParam
import com.menuxx.common.bean.ChannelItemRecord
import com.menuxx.common.bean.Item
import com.menuxx.common.bean.VChannel
import com.menuxx.common.db.tables.TItem
import com.menuxx.common.db.tables.TVChannel
import com.menuxx.miaosha.bean.ChannelItem
import com.menuxx.miaosha.exception.LaunchException
import com.menuxx.miaosha.store.ChannelItemStore
import com.menuxx.weixin.util.nullSkipUpdate
import org.jooq.DSLContext
import org.jooq.types.UInteger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

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

    val StatusCreated = 0
    val StatusStarted = 1
    val StatusFinishd = 2

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

    fun loadVipChannels(merchantId: Int, page: PageParam) : List<VChannel> {
        return dsl.select().from(tVipChannel)
                .leftJoin(tItem).on(tVipChannel.ITEM_ID.eq(tItem.ID))
                .offset(page.getOffset()).limit(page.getLimit()).fetchArray().map {
            val channel = it.into(tVipChannel).into(VChannel::class.java)
            val item = it.into(tItem).into(Item::class.java)
            channel.item = item
            channel
        }
    }

    /**
     * 新增渠道
     */
    fun insertVipChannel(vChannel: VChannel) : VChannel {
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
    fun updateVipChannel(channelId: Int, vChannel: VChannel) : VChannel? {
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
        val items = (1..channel.stock).map { ChannelItemRecord(null, channelId, channel.itemId, null, null) }
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
        if (itemWithIds.size == channel.stock) {
            throw LaunchException(LaunchRecordRowsFail, "数据库记录输出条数不正确")
        }
        // 存储到 渠道存储中
        ChannelItemStore.putChannelItems(channelId, itemWithIds)
        updateToStarted(channelId)
    }

}