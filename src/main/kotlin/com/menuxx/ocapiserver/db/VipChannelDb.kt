package com.menuxx.ocapiserver.db

import com.menuxx.ocapiserver.PageParam
import com.menuxx.ocapiserver.bean.Item
import com.menuxx.ocapiserver.bean.VipChannel
import com.menuxx.ocapiserver.db.tables.TItem
import com.menuxx.ocapiserver.db.tables.TVipChannel
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
class VipChannelDb(private val dsl: DSLContext) {

    fun getById(channelId: Int) : VipChannel {
        val tVipChannel = TVipChannel.T_VIP_CHANNEL
        return dsl.select().from(tVipChannel).where(tVipChannel.ID.eq(UInteger.valueOf(channelId))).fetchOne().into(VipChannel::class.java)
    }

    fun loadVipChannels(merchantId: Int, page: PageParam) : List<VipChannel> {
        val tVipChannel = TVipChannel.T_VIP_CHANNEL
        val tItem = TItem.T_ITEM
        return dsl.select().from(tVipChannel)
                .leftJoin(tItem).on(tVipChannel.ITEM_ID.eq(tItem.ID))
                .offset(page.getOffset()).limit(page.getLimit()).fetchArray().map {
            val channel = it.into(VipChannel::class.java)
            val item = it.into(Item::class.java)
            channel.item = item
            channel
        }
    }

    /**
     * 新增渠道
     */
    fun insertVipChannel(vipChannel: VipChannel) : VipChannel {
        val tVipChannel = TVipChannel.T_VIP_CHANNEL
        return dsl.insertInto(tVipChannel).set(dsl.newRecord(tVipChannel, vipChannel)).returning().fetchOne().into(VipChannel::class.java)
    }

    /**
     * 更新渠道信息
     */
    @Transactional
    fun updateVipChannel(channelId: Int, vipChannel: VipChannel) : VipChannel {
        val tVipChannel = TVipChannel.T_VIP_CHANNEL
        dsl.update(tVipChannel)
                    .set(dsl.newRecord(tVipChannel, vipChannel))
                    .where(tVipChannel.ID.eq(UInteger.valueOf(channelId))).execute()
        return getById(channelId)
    }

}