package com.menuxx.miaosha.page

import com.menuxx.common.db.VipChannelDb
import com.menuxx.miaosha.exception.NotFoundException
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/11
 * 微信: yin80871901
 */

@Controller
@RequestMapping("/channels")
class ChannelCtrl(private val vipChannelDb: VipChannelDb) {

    @GetMapping("/{channelId}/item")
    fun viewChannelItem(@PathVariable channelId: Int, model: Model) : String {
        val channelItem = vipChannelDb.getById(channelId) ?: throw NotFoundException("不存在该推荐频道")
        model.addAttribute("channelItem", channelItem)
        return "channel_item"
    }

}