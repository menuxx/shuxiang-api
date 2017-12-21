package com.menuxx.miaosha.queue.msg

import com.menuxx.NoArg

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/19
 * 微信: yin80871901
 */

@NoArg
data class ObtainUserMsg(
        var userId: Int,
        var channelId: Int,
        var loopRefId: String
)

@NoArg
data class ObtainConsumedMsg(
        var userId: Int,
        var channelId: Int,
        var orderId: Int,
        var loopRefId: String?
)