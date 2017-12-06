package com.menuxx.ocapiserver.ctrl

import com.menuxx.ocapiserver.AllOpen
import com.menuxx.ocapiserver.bean.ApiResp
import com.menuxx.ocapiserver.bean.UserAddress
import org.springframework.web.bind.annotation.*

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/4
 * 微信: yin80871901
 */

@AllOpen
@RestController
@RequestMapping("/user_addresses")
class UserAddressCtrl {

    @GetMapping
    fun loadAddresses() : List<UserAddress> {
        return listOf()
    }

    @PostMapping
    fun addAddress() : UserAddress {
        return UserAddress()
    }

    @PutMapping("/{addressId}")
    fun updateAddress() : UserAddress {
        return UserAddress()
    }

    @DeleteMapping("/addressId")
    fun delAddress() : ApiResp {
        return ApiResp(1, "delete ok!")
    }

}