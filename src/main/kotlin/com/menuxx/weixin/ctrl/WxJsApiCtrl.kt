package com.menuxx.weixin.ctrl

import me.chanjar.weixin.common.bean.WxJsapiSignature
import me.chanjar.weixin.mp.api.WxMpService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URLDecoder

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/20
 * 微信: yin80871901
 */

@RequestMapping("/weixin")
@RestController
class WxJsApiCtrl(private val wxMpService: WxMpService) {

    data class SignatureInfo(val jsApiConfig: WxJsapiSignature)
    @GetMapping("/config")
    fun jsTicket(@RequestParam url: String) : SignatureInfo {
        val jsApiConfig = wxMpService.createJsapiSignature(URLDecoder.decode(url, "UTF-8"))
        return SignatureInfo(
                jsApiConfig = jsApiConfig
        )
    }

}