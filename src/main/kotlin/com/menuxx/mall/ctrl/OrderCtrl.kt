package com.menuxx.mall.ctrl

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest
import com.github.binarywang.wxpay.service.WxPayService
import com.menuxx.common.bean.MallYhsdOrder
import com.menuxx.genMallOrderNo
import com.menuxx.getCurrentUser
import com.menuxx.mall.service.MallOrderService
import com.menuxx.miaosha.queue.MsgTags
import com.menuxx.weixin.prop.WeiXinProps
import org.hibernate.validator.constraints.NotEmpty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpSession
import javax.validation.Valid

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2018/1/31
 * 微信: yin80871901
 */

data class YhsdCreateOrder(
        @JsonProperty("address_id") val addressId: Int,
        @JsonProperty("payment_method_id") val paymentMethodId: Int,
        @JsonProperty("shipments") val shipments: String,
        @JsonProperty("meta_fields") val metaFields: String
) {
    fun toMap(): HashMap<String, Any> {
        return hashMapOf(
            "address_id" to addressId, "payment_method_id" to paymentMethodId, "shipments" to shipments, "meta_fields" to metaFields
        )
    }
}

@RestController
@RequestMapping("/mall/orders")
class MallOrderCtrl(
        private val objectMapper: ObjectMapper,
        private val mallOrderService: MallOrderService,
        @Qualifier("wxMiniAppPayService") @Autowired private val wxMiniPayService: WxPayService,
        private val wxProps: WeiXinProps
) {

    private fun calcDiscount(discounts: List<Map<String, Any?>>) : Int {
        return discounts.filter { it["actived"] as Boolean }.sumBy { it["discount_amount"] as Int }
    }

    private fun calcShipmentFee(shipments: List<Map<String, Any?>>) : Int {
        return shipments.map {
            val methods = it["shipment_methods"] as List<Map<String, Any?>>
            methods.sumBy { it["amount"] as Int }
        }.sum()
    }

    private fun makeOrderShipments(shipments: List<Map<String, Any?>>) : List<Map<String, Any?>> {
        return shipments.map { ship ->
            val methods = ship["shipment_methods"] as List<Map<String, Any?>>
            methods.map {
                mapOf("id" to ship["id"], "shipment_method_id" to it["id"], "amount" to it["amount"])
            }
        }.flatten()
    }

    private fun getCartsItemNames(shipments: List<Map<String, Any?>>) : String {
        return shipments.map { ship ->
            val products = ship["carts"] as List<Map<String, Any?>>
            products.map { it["name"] }
        }.flatten().joinToString(",")
    }

    private fun getOfflinePaymentMethod(paymentMethods: List<Map<String, Any?>>) : Map<String, Any?> {
        // 支付方式可用 并且 是货到付款方式
        return paymentMethods.findLast { it["is_actived"] as Boolean && (it["pay_type"] as String == "offline") } ?: emptyMap()
    }

    data class CreateOrder(@NotEmpty val addressId: Int)
    @PostMapping("/create")
    fun create(session: HttpSession, @Valid @RequestBody createOrder: CreateOrder) : ResponseEntity<WxPayMpOrderResult> {
        val user = getCurrentUser()

        /**
         * {
        "code": 200,
        "message": "",
        "shipments": [{
        "id": 113072,
        "carts": [{
        "product_id": 1030211,
        "variant_id": 4755257,
        "quantity": 2,
        "price": 1,
        "point": 0,
        "weight": 0,
        "volume": 0,
        "options_desc": "",
        "name": "斯坦福社会创新评论01",
        "page_url": "/products/p000001",
        "image_id": "5a5d660c3f8f907a6900047f",
        "image_name": "web.jpeg",
        "image_epoch": "1516070413",
        "image_src": "https://asset.ibanquan.com/image/5a5d660c3f8f907a6900047f/s.jpeg?v=1516070413",
        "line_price": 2,
        "stock": 98
        }, {
        "product_id": 1030212,
        "variant_id": 4755379,
        "quantity": 1,
        "price": 1,
        "point": 0,
        "weight": 0,
        "volume": 0,
        "options_desc": "",
        "name": "雪人读书",
        "page_url": "/products/p000002",
        "image_id": "5a587a8b9bedc41648000071",
        "image_name": "web.png",
        "image_epoch": "1515747980",
        "image_src": "https://asset.ibanquan.com/image/5a587a8b9bedc41648000071/s.png?v=1515747980",
        "line_price": 1,
        "stock": 997
        }, {
        "product_id": 1030928,
        "variant_id": 4757520,
        "quantity": 2,
        "price": 25800,
        "point": 0,
        "weight": 0,
        "volume": 0,
        "options_desc": "",
        "name": "东盛堂玫瑰精油单方精油",
        "page_url": "/products/p000007",
        "image_id": "5a5ef71706ce8b2bb3000158",
        "image_name": "web.jpeg",
        "image_epoch": "1516173079",
        "image_src": "https://asset.ibanquan.com/image/5a5ef71706ce8b2bb3000158/s.jpeg?v=1516173079",
        "line_price": 51600,
        "stock": 999
        }],
        "shipment_methods": [{
        "id": 163349,
        "type": 1,
        "ship_type": 4,
        "amount": 600,
        "discount": null
        }]
        }]
        }
         */

        val paymentMethods = mallOrderService.queryPaymentMethods()
        val offlineMethod = getOfflinePaymentMethod(paymentMethods)
        val methodId = offlineMethod["id"] as Int

        val shipments = mallOrderService.queryCartWithinShipments(createOrder.addressId)
        val shipmentAmount = calcShipmentFee(shipments)

        val orderShipments = makeOrderShipments(shipments)

        val order = MallYhsdOrder()

        val cartDiscount = mallOrderService.queryCalcCart()
        val test = cartDiscount["test"] as HashMap<String, Any?>
        val postParams = test["post_params"] as HashMap<String, Any?>
        val itemAmount = postParams["item_amount"] as Int

        val discounts = cartDiscount["discounts"] as List<HashMap<String, Any?>>
        val discountAmount = calcDiscount(discounts)

        order.itemNames = getCartsItemNames(shipments)
        order.totalAmount = itemAmount + shipmentAmount - discountAmount
        order.addressId = createOrder.addressId
        order.paymentMethodId = methodId
        order.paymentMethodType = "offline" // 货到付款
        order.shipments = objectMapper.writeValueAsString(orderShipments)
        order.metaFields = "{\"name\": \"order_attributes\", \"description\": \"order_attributes\", \"fields\": {}}"
        order.localOrderNo = genMallOrderNo(userId = user.id)

        mallOrderService.createOrder(order)
        val orderCharge = mallOrderService.createMallOrderCharge(order, user.openid!!)
        // 运费
        val payReq = WxPayUnifiedOrderRequest()
        payReq.notifyURL = "${wxProps.miniAppPay.notifyUrl}/${MsgTags.TagMallOrderPay}"
        payReq.totalFee = orderCharge.totalFee
        payReq.attach = orderCharge.attach
        payReq.openid = orderCharge.openid
        payReq.tradeType = orderCharge.tradeType
        payReq.nonceStr = orderCharge.nonceStr
        payReq.timeExpire = orderCharge.timeExpire
        payReq.deviceInfo = orderCharge.deviceInfo

        val paymentValue = wxMiniPayService.createOrder<WxPayMpOrderResult>(payReq)
        return ResponseEntity.ok(paymentValue)
    }

}