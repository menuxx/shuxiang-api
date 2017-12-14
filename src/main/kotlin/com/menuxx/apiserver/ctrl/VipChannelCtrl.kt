package com.menuxx.apiserver.ctrl

import com.menuxx.AllOpen
import com.menuxx.apiserver.Page
import com.menuxx.apiserver.PageParam
import com.menuxx.common.bean.VipChannel
import com.menuxx.common.db.VipChannelDb
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/4
 * 微信: yin80871901
 */

@AllOpen
@RestController
@RequestMapping("/vip_channels")
class VipChannelCtrl(val vipChannelDb: VipChannelDb) {

    @GetMapping("/{channelId}")
    fun getChannel(@PathVariable channelId: Int) : VipChannel? {
        return vipChannelDb.getById(channelId)
    }

    @GetMapping
    fun loadChannelOf(@RequestParam(defaultValue = Page.DefaultPageNumText) pageNum: Int, @RequestParam(defaultValue = Page.DefaultPageSizeText) pageSize: Int) : List<VipChannel> {
        return vipChannelDb.loadVipChannels(1, PageParam(pageNum, pageSize))
    }

    @PostMapping
    fun addChannel(@RequestBody @Valid vipChannel: VipChannel) : VipChannel {
        vipChannel.merchantId = 1
        return vipChannelDb.insertVipChannel(vipChannel)
    }

    @PutMapping("/{channelId}")
    fun updateChannel(@PathVariable channelId: Int, @RequestBody @Valid vipChannel: VipChannel) : VipChannel? {
        vipChannel.merchantId = 1
        return vipChannelDb.updateVipChannel(channelId, vipChannel)
    }

}