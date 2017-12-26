package com.menuxx.common.db

import com.menuxx.Const
import com.menuxx.common.bean.ChannelItemRecord
import com.menuxx.common.db.tables.TChannelItemRecord
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jooq.types.UInteger
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/8
 * 微信: yin80871901
 */

@Service
class ChannelItemRecordDb(private val dsl: DSLContext) {

    private val ObtainStatusNot = UInteger.valueOf(0) // 未持有
    private val NotHaveOrderId = UInteger.valueOf(0) // 没有 order_id

    private final val tChannelItemRecord = TChannelItemRecord.T_CHANNEL_ITEM_RECORD

    /**
     * 标注为消费状态
     * 启动状态恢复，就不会恢复该记录
     */
    fun itemConsumed(itemId: Int, orderId: Int, queueNum: Int) : Int {
        return dsl.update(tChannelItemRecord)
                .set(tChannelItemRecord.ORDER_ID, UInteger.valueOf(orderId))
                .set(tChannelItemRecord.QUEUE_NUM, queueNum)
                .where(tChannelItemRecord.ID.eq(UInteger.valueOf(itemId))).execute()
    }

    /**
     * 标注为持有状态
     */
    fun itemObtain(userId: Int, itemId: Int) : Int {
        return dsl.update(tChannelItemRecord)
                .set(tChannelItemRecord.OBTAIN_USER_ID, UInteger.valueOf(userId))
                .set(tChannelItemRecord.OBTAIN_TIME, Timestamp.from(Instant.now()))
                .where(tChannelItemRecord.ID.eq(UInteger.valueOf(itemId))).execute()
    }

    /**
     * 加载所可以消费的商品
     */
    fun loadChannelNotObtainItems() : List<ChannelItemRecord> {
        return dsl.select().from(tChannelItemRecord).where(
                tChannelItemRecord.ORDER_ID.eq(NotHaveOrderId).or(tChannelItemRecord.ORDER_ID.isNull)
        ).orderBy(tChannelItemRecord.ID.asc()).fetchArray().map {
            it.into(ChannelItemRecord::class.java)
        }
    }

    fun loadChannelItems(channelId: Int) : List<ChannelItemRecord> {
        return dsl.select().from(tChannelItemRecord).where(tChannelItemRecord.CHANNEL_ID.eq(UInteger.valueOf(channelId)))
                .fetchArray().map { it.into(ChannelItemRecord::class.java) }
    }

    fun addChannelBatch(list: List<ChannelItemRecord>) : Int {
        val step = dsl.insertInto(tChannelItemRecord).columns(
                tChannelItemRecord.CHANNEL_ID,
                tChannelItemRecord.ITEM_ID
        )
        list.forEach { step.values(
                UInteger.valueOf(it.channelId),
                UInteger.valueOf(it.itemId)
        )}
        return step.execute()
    }

}