package com.menuxx.apiserver.ctrl

import com.menuxx.AllOpen
import com.menuxx.common.bean.Order
import com.menuxx.common.db.OrderDb
import com.menuxx.getCurrentUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/14
 * 微信: yin80871901
 */
@AllOpen
@RestController
@RequestMapping("/user")
class UserCtrl (private val orderDb: OrderDb) {

    /**
     * 获取用户订单
     */
    @GetMapping("/orders/{orderId}")
    fun getUserOrderDetails(@PathVariable orderId: Int) : Order? {
        val user = getCurrentUser()
        return orderDb.getUserOrderDetails(user.id, orderId)
    }


}