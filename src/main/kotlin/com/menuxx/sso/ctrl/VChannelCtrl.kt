package com.menuxx.sso.ctrl

import com.menuxx.AllOpen
import com.menuxx.Page
import com.menuxx.PageParam
import com.menuxx.common.bean.Order
import com.menuxx.common.bean.VChannel
import com.menuxx.common.db.VChannelDb
import com.menuxx.getCurrentUser
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
class VChannelCtrl(private val vChannelDb: VChannelDb) {

    @GetMapping("/{channelId}/order_users")
    fun getVChannelOrderInfo(@PathVariable channelId: Int, @RequestParam(defaultValue = Page.DefaultPageNumText) pageNum: Int, @RequestParam(defaultValue = Page.DefaultPageSizeText) pageSize: Int) : Map<String, Any> {
        return mapOf(
                "count" to vChannelDb.getChannelOrderUsersCount(channelId),
                "users" to vChannelDb.loadChannelOrderUsers(channelId, PageParam(pageNum, pageSize))
        )
    }

    @GetMapping("/{channelId}")
    fun getVChannel(@PathVariable channelId: Int) : VChannel? {
        return vChannelDb.getById(channelId)
    }

    @GetMapping
    fun loadVChannelOfPage(@RequestParam(defaultValue = Page.DefaultPageNumText) pageNum: Int, @RequestParam(defaultValue = Page.DefaultPageSizeText) pageSize: Int) : List<VChannel> {
        return vChannelDb.loadVChannels(1, PageParam(pageNum, pageSize))
    }

    @PostMapping
    fun addVChannel(@RequestBody @Valid vChannel: VChannel) : VChannel {
        vChannel.merchantId = 1
        return vChannelDb.insertVChannel(vChannel)
    }

    @PutMapping("/{channelId}")
    fun updateVChannel(@PathVariable channelId: Int, @RequestBody @Valid vChannel: VChannel) : VChannel? {
        vChannel.merchantId = 1
        return vChannelDb.updateVChannel(channelId, vChannel)
    }

    @GetMapping("{channelId}/order")
    fun getVChannelUserOrder(@PathVariable channelId: Int) : Order? {
        val user = getCurrentUser()
        return vChannelDb.getChannelUserOrder(user.id, channelId)
    }

}