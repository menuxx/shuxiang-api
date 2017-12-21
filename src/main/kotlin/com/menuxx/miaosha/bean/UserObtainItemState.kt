package com.menuxx.miaosha.bean

import com.menuxx.NoArg

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/10
 * 微信: yin80871901
 */

@NoArg
data class UserObtainItemState(
        var loopRefId: String,
        var userId: Int,
        var channelItemId: Int,
        var confirmState: Int,
        // 消费后产生
        var orderId: Int?,
        var queueNum: Int?
)