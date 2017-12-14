package com.menuxx.miaosha.bean

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/10
 * 微信: yin80871901
 */

data class UserObtainItemState(
        val loopRefId: String,
        val userId: Int,
        val channelItemId: Int,
        val confirmState: Int
)