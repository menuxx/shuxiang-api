package com.menuxx.ocapiserver.ctrl

import com.menuxx.ocapiserver.bean.VipChannel
import org.springframework.web.bind.annotation.*

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/4
 * 微信: yin80871901
 */

@RestController
@RequestMapping("/vip_channel")
class VipChannelCtrl {

    @PostMapping
    fun addChannel() : VipChannel {
        return VipChannel()
    }

    @PutMapping("/channelId")
    fun updateChannel(@PathVariable channelId: Int) : VipChannel {
        return VipChannel()
    }

}