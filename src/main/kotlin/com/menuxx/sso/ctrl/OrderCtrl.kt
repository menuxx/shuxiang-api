package com.menuxx.sso.ctrl

import com.menuxx.AllOpen
import com.menuxx.Const
import com.menuxx.Page
import com.menuxx.PageParam
import com.menuxx.sso.bean.ApiResp
import com.menuxx.sso.service.OrderService
import com.menuxx.common.bean.Order
import com.menuxx.common.db.OrderDb
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.NotNull

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/4
 * 微信: yin80871901
 */
@AllOpen
@RestController
@RequestMapping("orders")
class OrderCtrl(
        private val orderDb: OrderDb,
        private val orderService: OrderService
) {

    /**
     * 加载商户订单
     * 1. 可以加载关于某个渠道的订单
     * 2. 也可以加载所有订单
     */
    @GetMapping
    fun loadMerchantOrders(@RequestParam(name = "channelId", required = false) channelId: Int?, @RequestParam(defaultValue = Page.DefaultPageNumText) pageNum: Int, @RequestParam(defaultValue = Page.DefaultPageSizeText) pageSize: Int) : List<Order> {
        return orderDb.loadMerchantOrders(1, channelId, PageParam(pageNum, pageSize))
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}")
    fun getOrderDetail(@PathVariable orderId: Int) : Order? {
        return orderDb.getOrderDetails(orderId)
    }

    /**
     * 订单配送
     */
    data class DeliveryData(@NotNull val expressNo: String, @NotNull val expressName: String)
    @PutMapping("/{orderId}/delivery")
    fun orderDoDelivery(@Valid @RequestBody express: DeliveryData, @PathVariable orderId: Int) : ApiResp {
        return try {
            orderService.doDoDelivery(orderId, expressNo = express.expressNo, expressName = express.expressName)
            ApiResp(Const.NotErrorCode, "更新完成")
        } catch (ex: Exception) {
            ApiResp(502, ex.message ?: "未知的错误")
        }
    }

    /**
     * 核销发货清单
     */
    @GetMapping("/{orderId}/write_off_delivery_manifest")
    fun writeOffDeliveryManifest() : ApiResp {
        return ApiResp(405, "用户身份不能进行该操作...")
    }

    /**
     * 下载发货清单
     */
    @GetMapping("/{orderId}/download_delivery_manifest")
    fun downloadDeliveryManifest() : ApiResp {
        return ApiResp(405, "用户身份不能进行该操作...")
    }

}