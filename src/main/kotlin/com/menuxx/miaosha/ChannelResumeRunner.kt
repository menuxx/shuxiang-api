package com.menuxx.miaosha

import com.menuxx.common.db.ChannelItemRecordDb
import com.menuxx.miaosha.bean.ChannelItem
import com.menuxx.miaosha.store.ChannelItemStore
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/8
 * 微信: yin80871901
 *
 * 将
 */
@Component
class ChannelResumeRunner(
        private val channelItemRecordDb: ChannelItemRecordDb
) : CommandLineRunner
{
    private val logger = LoggerFactory.getLogger(ChannelResumeRunner::class.java)

    override fun run(vararg args: String) {
        
        logger.info("load channel: start")

        val counts = channelItemRecordDb.loadLatestItemCount().map { it.channelId to if (it.queueNum == 0) { 1 } else { it.queueNum } }.toMap<Int, Int>()

        // 将所有的 未持有 item 分组后 恢复到内存
        channelItemRecordDb.loadChannelNotObtainItems().groupBy { it.channelId }.forEach { channelId, group ->
            logger.info("load channel: $channelId, size : ${group.size}")
            ChannelItemStore.putChannelItems(counts[channelId]!!, channelId, group.map { item ->
                ChannelItem(
                        id = item.id,
                        channelId = item.channelId,
                        itemId = item.itemId,
                        obtainUserId = item.obtainUserId,
                        obtainTime = item.obtainTime?.toInstant()
                )
            })
        }
        logger.info("load channel: end")
    }
}