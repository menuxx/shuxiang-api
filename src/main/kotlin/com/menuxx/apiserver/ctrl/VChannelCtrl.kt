package com.menuxx.apiserver.ctrl

import com.menuxx.AllOpen
import com.menuxx.apiserver.Page
import com.menuxx.apiserver.PageParam
import com.menuxx.common.bean.VChannel
import com.menuxx.common.db.VChannelDb
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/4
 * 微信: yin80871901
 */

@AllOpen
@RestController
@RequestMapping("/v_channels")
class VChannelCtrl(val vChannelDb: VChannelDb) {

    @GetMapping("/{channelId}")
    fun getChannel(@PathVariable channelId: Int) : VChannel? {
        return vChannelDb.getById(channelId)
    }

    @GetMapping
    fun loadChannelOf(@RequestParam(defaultValue = Page.DefaultPageNumText) pageNum: Int, @RequestParam(defaultValue = Page.DefaultPageSizeText) pageSize: Int) : List<VChannel> {
        return vChannelDb.loadVChannels(1, PageParam(pageNum, pageSize))
    }

    @PostMapping
    fun addChannel(@RequestBody @Valid vChannel: VChannel) : VChannel {
        vChannel.merchantId = 1
        return vChannelDb.insertVChannel(vChannel)
    }

    @PutMapping("/{channelId}")
    fun updateChannel(@PathVariable channelId: Int, @RequestBody @Valid vChannel: VChannel) : VChannel? {
        vChannel.merchantId = 1
        return vChannelDb.updateVChannel(channelId, vChannel)
    }

}