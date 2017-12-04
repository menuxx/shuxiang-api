package com.menuxx.ocapiserver.ctrl

import com.menuxx.ocapiserver.bean.MerchantUser
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/4
 * 微信: yin80871901
 */

@RestController
@RequestMapping("/merchants")
class MerchantCtrl {

    @PostMapping
    fun addMerchantCtrl() : MerchantUser {
        return MerchantUser()
    }

    @PostMapping
    fun updateMerchantCtrl() : MerchantUser {
        return MerchantUser()
    }

}