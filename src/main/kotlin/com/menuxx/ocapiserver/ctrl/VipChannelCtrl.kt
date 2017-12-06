package com.menuxx.ocapiserver.ctrl

import com.menuxx.ocapiserver.AllOpen
import com.menuxx.ocapiserver.bean.VipChannel
import com.menuxx.ocapiserver.db.VipChannelDb
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/4
 * 微信: yin80871901
 */

@AllOpen
@RestController
@RequestMapping("/vip_channel")
class VipChannelCtrl(val vipChannelDb: VipChannelDb) {

    @PostMapping
    fun addChannel(@RequestBody @Valid vipChannel: VipChannel) : VipChannel {
        vipChannel.merchantId = 1
        return vipChannelDb.insertVipChannel(vipChannel)
    }

    @PutMapping("/channelId")
    fun updateChannel(@PathVariable channelId: Int, @RequestBody @Valid vipChannel: VipChannel) : VipChannel {
        vipChannel.merchantId = 1
        return vipChannelDb.updateVipChannel(channelId, vipChannel)
    }

}