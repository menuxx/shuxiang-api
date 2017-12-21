package com.menuxx.miaosha.store

import com.menuxx.miaosha.disruptor.ChannelUserEvent


/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/10
 * 微信: yin80871901
 */

class ChannelUserGroup {

    companion object {
        @JvmStatic val UserGroup = HashMap<Int, ChannelUserEvent>()
    }

    fun getUser(userId: Int) = ChannelUserGroup.UserGroup[userId]

    fun getAndInsertUser(userId: Int, channelUser: ChannelUserEvent) : ChannelUserEvent {
        var user = ChannelUserGroup.UserGroup[userId]
        if ( user == null ) {
            user = channelUser
            ChannelUserGroup.UserGroup.put(userId, channelUser)
        }
        return user
    }

}

object ChannelUserStore {

    @JvmStatic private val UserStore = HashMap<Int, ChannelUserGroup>()

    fun getUserGroup(channelId: Int) : ChannelUserGroup {
        var group = UserStore[channelId]
        if ( group == null ) {
            group = ChannelUserGroup()
            UserStore.put(channelId, group)
        }
        return group
    }

}