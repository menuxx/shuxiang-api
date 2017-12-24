package com.menuxx.miaosha.queue.msg

import com.menuxx.NoArg

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/21
 * 微信: yin80871901
 */

@NoArg
data class ConsumeSuccessMsg(
        var mobile: String,
        var itemName: String,
        var receiverPhoneNumber: String
)