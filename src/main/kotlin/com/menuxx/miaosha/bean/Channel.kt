package com.menuxx.miaosha.bean

import java.time.Instant

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/8
 * 微信: yin80871901
 */


data class ChannelItem(
        val id: Int,
        var channelId: Int?,
        var itemId: Int,
        var obtainUserId: Int?,
        var obtainTime: Instant?
)