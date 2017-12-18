package com.menuxx.apiserver.ctrl

import com.menuxx.AllOpen
import com.menuxx.Const
import com.menuxx.apiserver.bean.ApiResp
import com.menuxx.common.bean.UserAddress
import com.menuxx.common.db.UserAddressDb
import com.menuxx.common.db.UserAddressIsPrimary
import com.menuxx.weixin.auth.AUser
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/4
 * 微信: yin80871901
 */

@AllOpen
@RestController
@RequestMapping("/user_addresses")
class UserAddressCtrl(private val userAddressDb: UserAddressDb) {

    @GetMapping
    fun loadAddresses() : List<UserAddress> {
        val currentUser = SecurityContextHolder.getContext().authentication as AUser
        return userAddressDb.loadMyAddress(currentUser.id)
    }

    /**
     * 获取默认地址
     */
    @GetMapping("/primary")
    fun getPrimaryAddress() : UserAddress? {
        val currentUser = SecurityContextHolder.getContext().authentication as AUser
        return userAddressDb.getPrimaryAddress(currentUser.id)
    }

    @PostMapping
    fun addAddress(address: UserAddress) : UserAddress {
        val currentUser = SecurityContextHolder.getContext().authentication as AUser
        return userAddressDb.insertAddress(currentUser.id, address)
    }

    @PutMapping("/{addressId}")
    fun updateAddress(@PathVariable addressId: Int, address: UserAddress) : ApiResp {
        val currentUser = SecurityContextHolder.getContext().authentication as AUser
        val rNum = userAddressDb.updateAddress(currentUser.id, addressId, address)
        return if (rNum > 0 ) {
            ApiResp(Const.NotErrorCode, "更新成功")
        } else {
            ApiResp(401, "指定的地址不存在，更新失败")
        }
    }

    @DeleteMapping("/{addressId}")
    fun delAddress(@PathVariable addressId: Int) : ApiResp {
        val currentUser = SecurityContextHolder.getContext().authentication as AUser
        val rNum = userAddressDb.delAddress(currentUser.id, addressId)
        return if (rNum > 0) {
            ApiResp(Const.NotErrorCode, "delete ok!")
        } else {
            ApiResp(401, "指定的地址不存在")
        }
    }

}