package com.menuxx.ocapiserver.ctrl

import com.menuxx.ocapiserver.AllOpen
import com.menuxx.ocapiserver.Page
import com.menuxx.ocapiserver.PageParam
import com.menuxx.ocapiserver.bean.Order
import com.menuxx.ocapiserver.db.OrderDb
import org.springframework.web.bind.annotation.*

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/4
 * 微信: yin80871901
 */
@AllOpen
@RestController
@RequestMapping("orders")
class OrderCtrl(private val orderDb: OrderDb) {

    /**
     * 加载商户订单
     * 1. 可以加载关于某个渠道的订单
     * 2. 也可以加载所有订单
     */
    @GetMapping
    fun loadMerchantOrders(@RequestParam(name = "channelId", required = false) channelId: Int?, @RequestParam(defaultValue = Page.DefaultPageNumText) pageNum: Int, @RequestParam(defaultValue = Page.DefaultPageSizeText) pageSize: Int) : List<Order> {
        return orderDb.loadOrders(1, channelId, PageParam(pageNum, pageSize))
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/orderId")
    fun getMerchantOrderDetail(@PathVariable orderId: Int) : Order {
        return orderDb.getOrderDetail(orderId)
    }

}