package com.menuxx.apiserver.ctrl

import com.menuxx.AllOpen
import com.menuxx.Const
import com.menuxx.apiserver.bean.ApiResp
import com.menuxx.apiserver.exception.NotFoundException
import com.menuxx.common.bean.UserAddress
import com.menuxx.common.db.UserAddressDb
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
@RequestMapping("/user_addresses")
class UserAddressCtrl(private val userAddressDb: UserAddressDb) {

    @GetMapping
    fun loadAddresses() : List<UserAddress> {
        val currentUser = getCurrentUser()
        return userAddressDb.loadMyAddress(currentUser.id)
    }

    /**
     * 获取默认地址
     */
    @GetMapping("/primary")
    fun getPrimaryAddress() : UserAddress {
        val currentUser = getCurrentUser()
        return userAddressDb.getPrimaryAddress(currentUser.id) ?: throw NotFoundException("用户没有默认地址")
    }

    @GetMapping("/{addressId}")
    fun getAddress(@PathVariable addressId: Int) : UserAddress {
        val currentUser = getCurrentUser()
        return userAddressDb.getUserAddressById(currentUser.id, addressId) ?: throw NotFoundException("没有找到指定的地址")
    }

    @PostMapping
    fun addAddress(@Valid @RequestBody address: UserAddress) : UserAddress {
        val currentUser = getCurrentUser()
        return userAddressDb.insertAddress(currentUser.id, address)
    }

    @PutMapping("/{addressId}")
    fun updateAddress(@PathVariable addressId: Int, @Valid @RequestBody address: UserAddress) : ApiResp {
        val currentUser = getCurrentUser()
        val rNum = userAddressDb.updateAddress(currentUser.id, addressId, address)
        return if (rNum > 0 ) {
            ApiResp(Const.NotErrorCode, "更新成功")
        } else {
            ApiResp(401, "指定的地址不存在，更新失败")
        }
    }

    @DeleteMapping("/{addressId}")
    fun delAddress(@PathVariable addressId: Int) : ApiResp {
        val currentUser = getCurrentUser()
        val rNum = userAddressDb.delAddress(currentUser.id, addressId)
        return if (rNum > 0) {
            ApiResp(Const.NotErrorCode, "delete ok!")
        } else {
            ApiResp(401, "指定的地址不存在")
        }
    }

    @PutMapping("/{addressId}/primary")
    fun setAddressPrimary(@PathVariable addressId: Int) : ApiResp {
        val currentUser = getCurrentUser()
        val rNum = userAddressDb.setAddressPrimary(currentUser.id, addressId)
        return if (rNum > 0) {
            ApiResp(Const.NotErrorCode, "设置完成")
        } else {
            ApiResp(401, "设置的地址不存在")
        }
    }

}