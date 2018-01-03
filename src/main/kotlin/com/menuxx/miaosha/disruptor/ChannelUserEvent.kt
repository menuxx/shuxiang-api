package com.menuxx.miaosha.disruptor

import com.menuxx.NoArg

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/10
 * 微信: yin80871901
 */

enum class ConfirmState(val state: Int) {
    NoObtain(0),        // 未持有
    Obtain(1),          // 已持有 单位消费
    ObtainConsumed(2),  // 已消费 完成抢购
    ObtainConsumeAgain(3),  // 再次消费结束了
    Finish(4),          // 抢完了，
    FreeObtain(5)       // 持有释放了
}

@NoArg
data class ChannelUserEvent(
        var userId: Int,
        var channelId: Int,
        var loopRefId: String?,
        var confirmState: ConfirmState = ConfirmState.NoObtain,
        var orderId: Int?   // 后期会产生
)