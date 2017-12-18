package com.menuxx.miaosha.store

import com.menuxx.miaosha.bean.ChannelItem
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/8
 * 微信: yin80871901
 */

class StoreItem<T>(val counter: AtomicInteger, val data: ConcurrentHashMap<Int, T>)

object ChannelItemStore {

    private val logger = LoggerFactory.getLogger(ChannelItemStore::class.java)

    // 支持多频道
    @JvmStatic private val Store = hashMapOf<Int, StoreItem<ChannelItem>>()

    fun createStoreItem(data: ConcurrentHashMap<Int, ChannelItem>?) : StoreItem<ChannelItem> {
        return StoreItem(AtomicInteger(1), data ?: ConcurrentHashMap())
    }

    // 单个添加或替换 channel
    fun putToChannelItems(channelId: Int, item: ChannelItem) {
        var storeItem = Store[channelId]
        if ( storeItem == null ) {
            storeItem = createStoreItem(null)
            storeItem.data.put(item.id, item)
            Store.put(channelId, storeItem)
        } else {
            storeItem.data.put(item.id, item)
        }
    }

    // 添加或替换 一批 items
    fun putChannelItems(channelId: Int, items: ConcurrentHashMap<Int, ChannelItem>) {
        val itemStore = createStoreItem(items)
        Store.put(channelId, itemStore)
    }

    /**
     * 给摸个 channelItem 申请一个 消费令牌
     */
    fun genItemFreeToken(channelId: Int, channelItemId: Int) : String? {
        val channelItems = getChannelStore(channelId)?.data
        return if ( channelItems == null || channelItems[channelItemId] == null) {
            null
        } else {
            val consumeToken = UUID.randomUUID().toString()
            channelItems[channelItemId]?.preConsumeToken = consumeToken
            consumeToken
        }
    }

    fun putChannelItems(channelId: Int, items: List<ChannelItem>) {
        items.forEach { item ->
            putToChannelItems(channelId, item)
        }
    }

    fun getChannelStore(channelId: Int) : StoreItem<ChannelItem>? {
        logger.info("getChannelStore(channelId: $channelId)")
        return Store[channelId]
    }

    /**
     * 找到一个持有
     * 以2000条为一个分片(3900 也只会产生1个分片, 4000 才会产生 2 个分片, 大多数情况下只会产生一个分片，java 单线程的计算能力，还是很高的)，搜索该用户的持有，找到后弹出，会导致该持有被删除
     * 删除的原因是可以让 其他用户在 持有的时候 缩小搜索单位
     * 而且 当 channel_store 配全部删除的时候，就表明已经抢完了
     *
     * 一个用户 消费一个持有的条件是
     * 1 channel 存在该用户的持有
     * 2. 并且持有时间 发生在30秒之内
     * 3. 没有消费的 channelItem
     */
    fun searchObtainFromChannel(userId: Int, channelId: Int) : ChannelItem? {
        val channel = getChannelStore(channelId)?.data
        return channel?.search(Math.floor( (channel.size / 2000).toDouble() ).toLong(), { _, v ->
            if (v.consumeToken != null && v.obtainUserId == userId && Duration.between(v.obtainTime, Instant.now()).seconds < 30) {
                v
            } else {
                null
            }
        })
    }

    fun consumeObtainFromChannel(channelId: Int, itemId: Int) : ChannelItem? {
        val channel = getChannelStore(channelId)?.data
        val channelItem = channel?.get(itemId)
        channelItem?.consumeToken = channelItem?.preConsumeToken
        return channelItem
    }

    /**
     * 持有一个 item 从一个 Channel 中
     */
    fun obtainItemFromChannel(userId: Int, channelId: Int) : ChannelItem? {
        val channel = getChannelStore(channelId)?.data ?: return null
        val freeItems = channel.filter { entry ->
            val obtainTime = entry.value.obtainTime ?: Instant.now()
            entry.value.obtainUserId == null || Duration.between(obtainTime, Instant.now()).seconds > 30
        }
        if ( freeItems.isEmpty() ) {
            return null
        }
        val obtainKey = freeItems.keys.first()
        val obtain = freeItems[obtainKey]!!
        // g更新持有人和持有时间
        obtain.obtainUserId = userId
        obtain.obtainTime = Instant.now()
        return obtain
    }

}