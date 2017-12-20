package com.menuxx.common.db

import com.menuxx.Const
import com.menuxx.common.bean.ChannelItemRecord
import com.menuxx.common.db.tables.TChannelItemRecord
import org.jooq.DSLContext
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

    val ObtainStatusNot = 0 // 未持有
    val ObtainConsumeStatusNotCounsumed = 0
    val ObtainConsumeStatusCounsumed = 1

    private final val tChannelItemRecord = TChannelItemRecord.T_CHANNEL_ITEM_RECORD

    /**
     * 标注为消费状态
     * 启动状态恢复，就不会恢复该记录
     */
    fun itemConsumed(itemId: Int) : Int {
        return dsl.update(tChannelItemRecord)
                .set(tChannelItemRecord.OBTAIN_CONSUMED, 1)
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
     * 加载所有没有被持有的商品
     * 1. 所有 obtainUserId 不为空 但是 所有 obtainTime 大于 MaxObtainSeconds 的
     * 2. 所有 obtainUserId 为空的
     */
    fun loadChannelNotObtainItems() : List<ChannelItemRecord> {
        // 如果 OBTAIN_TIME 大于 当前时间 - 30秒 就是 过期的持有结果
        val expiredTime = Instant.now().minusSeconds(Const.MaxObtainSeconds.toLong())
        return dsl.select().from(tChannelItemRecord).where(
                tChannelItemRecord.OBTAIN_USER_ID.eq(UInteger.valueOf(ObtainStatusNot)) // 找出未持有的
                        .or(
                                tChannelItemRecord.OBTAIN_TIME.lessThan(Timestamp.from(expiredTime))
                                        .and(tChannelItemRecord.OBTAIN_CONSUMED.eq(ObtainConsumeStatusCounsumed))
                        )   // 找出超过持有时间的未消费的
                        .and(tChannelItemRecord.OBTAIN_CONSUMED.eq(ObtainConsumeStatusNotCounsumed)) // 找出未消费的
        ).orderBy(tChannelItemRecord.ID.asc()).fetchArray().map {
            it.into(ChannelItemRecord::class.java)
        }
    }

    /**
     * 加载所有已经启动但是尚未结束的 channel item record 到内存
     * 1. 找到所有符合要求的渠道
     */
    // fun loadAllChannelItems() : List<ChannelItemRecord> {
    // }

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
        ) }
        return step.execute()
    }

}