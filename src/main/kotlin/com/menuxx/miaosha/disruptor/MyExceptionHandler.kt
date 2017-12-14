package com.menuxx.miaosha.disruptor

import com.lmax.disruptor.ExceptionHandler

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/10
 * 微信: yin80871901
 */
class MyExceptionHandler(val eventName: String) : ExceptionHandler<ChannelUserEvent> {
    override fun handleOnShutdownException(ex: Throwable) {
        print(eventName)
        ex.printStackTrace()
    }

    override fun handleEventException(ex: Throwable, sequence: Long, event: ChannelUserEvent) {
        print(eventName + event)
        ex.printStackTrace()
    }

    override fun handleOnStartException(ex: Throwable) {
        print(eventName)
        ex.printStackTrace()
    }
}