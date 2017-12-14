package com.menuxx.apiserver.ctrl

import com.menuxx.AllOpen
import com.menuxx.common.bean.MerchantUser
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/4
 * 微信: yin80871901
 */

@AllOpen
@RestController
@RequestMapping("/merchants")
class MerchantCtrl {

    @PostMapping
    fun addMerchantCtrl() : MerchantUser {
        return MerchantUser()
    }

    @PutMapping
    fun updateMerchantCtrl() : MerchantUser {
        return MerchantUser()
    }

}