package com.menuxx.miaosha.store

import com.menuxx.miaosha.disruptor.ChannelUserEvent
import java.util.concurrent.ConcurrentHashMap


/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/10
 * 微信: yin80871901
 */

class ChannelUserGroup {

    private val UserGroup = ConcurrentHashMap<Int, ChannelUserEvent>()

    fun getUser(userId: Int) = UserGroup[userId]

    fun getAndInsertUser(userId: Int, channelUser: ChannelUserEvent) : ChannelUserEvent {
        var user = UserGroup[userId]
        if ( user == null ) {
            user = channelUser
            UserGroup.put(userId, channelUser)
        }
        return user
    }

}

object ChannelUserStore {

    @JvmStatic private val UserStore = ConcurrentHashMap<Int, ChannelUserGroup>()

    fun getUserGroup(channelId: Int) : ChannelUserGroup {
        var group = UserStore[channelId]
        if ( group == null ) {
            group = ChannelUserGroup()
            UserStore.put(channelId, group)
        }
        return group
    }

}