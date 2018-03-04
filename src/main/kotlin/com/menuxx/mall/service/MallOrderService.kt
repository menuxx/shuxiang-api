package com.menuxx.mall.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.binarywang.wxpay.constant.WxPayConstants
import com.menuxx.common.bean.MallYhsdOrder
import com.menuxx.common.bean.OrderCharge
import com.menuxx.common.db.MallOrderDb
import com.menuxx.formatWXTime
import com.menuxx.genRandomString32
import com.menuxx.mall.WebApiService
import com.menuxx.mall.YouHaoWebApi
import com.menuxx.mall.ctrl.YhsdCreateOrder
import com.menuxx.mall.exception.YouHaoException
import com.menuxx.mall.getYhsdCustomerSession
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.HashMap

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2018/1/31
 * 微信: yin80871901
 *
 *  http://mall.nizhuantech.com/api/v1/discount/match/cart?token=9dd5934c294149a8aaba5a3540d7f709&rnd=1517385928387
 * {
"code": 200,
"message": "",
"discounts": [{
"id": "7003",
"name": "满100减20",
"handle": "d000002",
"page_url": "/discounts/d000002",
"discount_type": "amount_off",
"discount_amount": 2000,
"coupon_group_id": 0,
"active_type": "entire",
"range_type": "entire",
"details": [{
"active_amount": 10000,
"discount_amount": 2000
}],
"match_item_amount": 51603,
"range_products": [],
"actived": true
}],
"point": 0,
"reward_point_limit": 0.0,
"reward_point_enabled": false,
"reward_point_exchange_ratio": 0.0,
"test": {
"post_params": {
"products": "1030211:2;1030212:1;1030928:51600",
"created_at": "2018-01-31 16:17:47 +0800",
"item_amount": 51603,
"customer_id": "958280",
"customer_level_id": "23599",
"customer_level_percent": 100,
"is_first_trade": true,
"store_id": "23859"
},
"post_domain": {
"scheme": "http",
"user": null,
"password": null,
"host": "192.168.3.15",
"port": 7071,
"path": "/calculate",
"query": null,
"opaque": null,
"fragment": null,
"parser": {
"regexp": {
"SCHEME": "(?-mix:\\A[A-Za-z][A-Za-z0-9+\\-.]*\\z)",
"USERINFO": "(?-mix:\\A(?:%\\h\\h|[!$&-.0-;=A-Z_a-z~])*\\z)",
"HOST": "(?-mix:\\A(?:(?<IP-literal>\\[(?:(?<IPv6address>(?:\\h{1,4}:){6}(?<ls32>\\h{1,4}:\\h{1,4}|(?<IPv4address>(?<dec-octet>[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5]|\\d)\\.\\g<dec-octet>\\.\\g<dec-octet>\\.\\g<dec-octet>))|::(?:\\h{1,4}:){5}\\g<ls32>|\\h{,4}::(?:\\h{1,4}:){4}\\g<ls32>|(?:(?:\\h{1,4}:)?\\h{1,4})?::(?:\\h{1,4}:){3}\\g<ls32>|(?:(?:\\h{1,4}:){,2}\\h{1,4})?::(?:\\h{1,4}:){2}\\g<ls32>|(?:(?:\\h{1,4}:){,3}\\h{1,4})?::\\h{1,4}:\\g<ls32>|(?:(?:\\h{1,4}:){,4}\\h{1,4})?::\\g<ls32>|(?:(?:\\h{1,4}:){,5}\\h{1,4})?::\\h{1,4}|(?:(?:\\h{1,4}:){,6}\\h{1,4})?::)|(?<IPvFuture>v\\h+\\.[!$&-.0-;=A-Z_a-z~]+))\\])|\\g<IPv4address>|(?<reg-name>(?:%\\h\\h|[!$&-.0-9;=A-Z_a-z~])*))\\z)",
"ABS_PATH": "(?-mix:\\A\\/(?:%\\h\\h|[!$&-.0-;=@-Z_a-z~])*(?:\\/(?:%\\h\\h|[!$&-.0-;=@-Z_a-z~])*)*\\z)",
"REL_PATH": "(?-mix:\\A(?:%\\h\\h|[!$&-.0-;=@-Z_a-z~])+(?:\\/(?:%\\h\\h|[!$&-.0-;=@-Z_a-z~])*)*\\z)",
"QUERY": "(?-mix:\\A(?:%\\h\\h|[!$&-.0-;=@-Z_a-z~\\/?])*\\z)",
"FRAGMENT": "(?-mix:\\A(?:%\\h\\h|[!$&-.0-;=@-Z_a-z~\\/?])*\\z)",
"OPAQUE": "(?-mix:\\A(?:[^\\/].*)?\\z)",
"PORT": "(?-mix:\\A[\\x09\\x0a\\x0c\\x0d ]*\\d*[\\x09\\x0a\\x0c\\x0d ]*\\z)"
}
}
},
"return_body": "{\"Code\":\"200\",\"Msg\":\"\",\"ReduceList\":[{\"Id\":\"7003\",\"Name\":\"满100减20\",\"Handle\":\"d000002\",\"PageUrl\":\"/discounts/d000002\",\"DiscountType\":\"amount_off\",\"ActiveAmount\":10000,\"MatchItemAmount\":51603,\"DiscountAmount\":2000,\"CouponGroupId\":0,\"RangeType\":\"entire\",\"RangeProducts\":[],\"ActiveType\":\"entire\"}],\"CouponList\":[],\"LevelReduceAmount\":0,\"PromotionReduceAmount\":2000}"
}
}
 *
 *
 *
 * http://mall.nizhuantech.com/api/v1/discount/match/cart?token=9dd5934c294149a8aaba5a3540d7f709&rnd=1517384917171
 * {
"code": 200,
"message": "",
"discounts": [],
"point": 0,
"reward_point_limit": 0.0,
"reward_point_enabled": false,
"reward_point_exchange_ratio": 0.0,
"test": {
"post_params": {
"products": "1030211:2;1030212:1;1030928:51600",
"created_at": "2018-01-31 15:48:37 +0800",
"item_amount": 51603,
"customer_id": "958280",
"customer_level_id": "23599",
"customer_level_percent": 100,
"is_first_trade": true,
"store_id": "23859"
},
"post_domain": {
"scheme": "http",
"user": null,
"password": null,
"host": "192.168.3.15",
"port": 7071,
"path": "/calculate",
"query": null,
"opaque": null,
"fragment": null,
"parser": {
"regexp": {
"SCHEME": "(?-mix:\\A[A-Za-z][A-Za-z0-9+\\-.]*\\z)",
"USERINFO": "(?-mix:\\A(?:%\\h\\h|[!$&-.0-;=A-Z_a-z~])*\\z)",
"HOST": "(?-mix:\\A(?:(?<IP-literal>\\[(?:(?<IPv6address>(?:\\h{1,4}:){6}(?<ls32>\\h{1,4}:\\h{1,4}|(?<IPv4address>(?<dec-octet>[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5]|\\d)\\.\\g<dec-octet>\\.\\g<dec-octet>\\.\\g<dec-octet>))|::(?:\\h{1,4}:){5}\\g<ls32>|\\h{,4}::(?:\\h{1,4}:){4}\\g<ls32>|(?:(?:\\h{1,4}:)?\\h{1,4})?::(?:\\h{1,4}:){3}\\g<ls32>|(?:(?:\\h{1,4}:){,2}\\h{1,4})?::(?:\\h{1,4}:){2}\\g<ls32>|(?:(?:\\h{1,4}:){,3}\\h{1,4})?::\\h{1,4}:\\g<ls32>|(?:(?:\\h{1,4}:){,4}\\h{1,4})?::\\g<ls32>|(?:(?:\\h{1,4}:){,5}\\h{1,4})?::\\h{1,4}|(?:(?:\\h{1,4}:){,6}\\h{1,4})?::)|(?<IPvFuture>v\\h+\\.[!$&-.0-;=A-Z_a-z~]+))\\])|\\g<IPv4address>|(?<reg-name>(?:%\\h\\h|[!$&-.0-9;=A-Z_a-z~])*))\\z)",
"ABS_PATH": "(?-mix:\\A\\/(?:%\\h\\h|[!$&-.0-;=@-Z_a-z~])*(?:\\/(?:%\\h\\h|[!$&-.0-;=@-Z_a-z~])*)*\\z)",
"REL_PATH": "(?-mix:\\A(?:%\\h\\h|[!$&-.0-;=@-Z_a-z~])+(?:\\/(?:%\\h\\h|[!$&-.0-;=@-Z_a-z~])*)*\\z)",
"QUERY": "(?-mix:\\A(?:%\\h\\h|[!$&-.0-;=@-Z_a-z~\\/?])*\\z)",
"FRAGMENT": "(?-mix:\\A(?:%\\h\\h|[!$&-.0-;=@-Z_a-z~\\/?])*\\z)",
"OPAQUE": "(?-mix:\\A(?:[^\\/].*)?\\z)",
"PORT": "(?-mix:\\A[\\x09\\x0a\\x0c\\x0d ]*\\d*[\\x09\\x0a\\x0c\\x0d ]*\\z)"
}
}
},
"return_body": "{\"Code\":\"404\",\"Msg\":\"\",\"ReduceList\":[],\"CouponList\":[],\"LevelReduceAmount\":0,\"PromotionReduceAmount\":0}"
}
}
 */

@Service
class MallOrderService(
        private val objectMapper: ObjectMapper,
        private val mallOrderDb: MallOrderDb,
        private val webApiService: WebApiService,
        private val openApiService: OpenApiService
        ) {

    /**
     * 绑定 友好速搭 order_id
     */
     fun bindYhsdOrderId(orderNo: String, orderId: String) : Int {
        return mallOrderDb.updateYhsdOrderId(orderNo, orderId)
     }

    fun createOrder(order: MallYhsdOrder) : Int {
        return mallOrderDb.insertOrder(order)
    }

    /**
     * 提交订单创建
     * {"code":200,"message":"","order_no":"2018013123859867428","order_id":535697}
     */
    fun submitCreateOrder(addressId: Int, paymentMethodId: Int, shipments: String, metaFields: String) : Map<String, Any?> {
        val customerSession = getYhsdCustomerSession()
        val token = openApiService.getAuthTokenFromCache()
        val resp = webApiService.authRequest(
                method = YouHaoWebApi.OrderCreate.method,
                path = YouHaoWebApi.OrderCreate.path,
                customerSession = customerSession,
                accessToken = token,
                queryData = null,
                data = YhsdCreateOrder(
                        addressId = addressId,
                        paymentMethodId = paymentMethodId,
                        shipments = shipments,
                        metaFields = metaFields
                ).toMap()
        )
        val code = resp["code"] as Int
        val message = resp["message"] as String
        if ( code == 200 ) {
            return resp
        } else {
            throw YouHaoException(code, message, resp)
        }
    }

    /**
     * 对购物车发起计算逻辑
     */
    fun queryCalcCart() : Map<String, Any?> {
        val customerSession = getYhsdCustomerSession()
        val token = openApiService.getAuthTokenFromCache()
        return webApiService.authRequest(
                method = YouHaoWebApi.CartDiscount.method,
                path = YouHaoWebApi.CartDiscount.path,
                customerSession = customerSession,
                accessToken = token
        )
    }

    /**
     * 订单支付交易的创建逻辑
     */
    fun createMallOrderCharge(order: MallYhsdOrder, openId: String) : OrderCharge {
        val charge = OrderCharge()
        charge.openid = openId
        // 不能超过 128 个字符，否则会失败
        // 长度超过 128 ，就加省略号
        charge.body = order.itemNames.substring(0, 20) + "..."
        charge.tradeType = WxPayConstants.TradeType.JSAPI
        charge.deviceInfo = "WechatMiniApp"
        charge.attach = "localOrderNo=${order.localOrderNo}"
        // 生成订单编号 32 位
        charge.outTradeNo = order.localOrderNo
        // 生成随机按号 32 位
        charge.nonceStr = genRandomString32()
        // 需要支付的总价格
        charge.totalFee = order.totalAmount
        charge.timeStart = formatWXTime(Date(System.currentTimeMillis()))
        // 2 小时后过期
        charge.timeExpire = formatWXTime(Date(System.currentTimeMillis() + 7200 * 1000))
        return charge
    }

    /**
     * 查询本订单的支付方式
     */
    fun queryPaymentMethods() : List<Map<String, Any?>> {
        val resp = openApiService.request(
                method = "GET",
                path = "payment_methods",
                queryData = null,
                data = null
        )
        if ( resp.statusCode == 200 ) {
            val data = objectMapper.readValue(resp.body, HashMap::class.java) as HashMap<String, Any>
            return if (data["payment_methods"] != null) {
                data["payment_methods"] as List<HashMap<String, Any>>
            } else {
                throw YouHaoException(data["code"] as Int, data["message"] as String, null)
            }
        }
        throw YouHaoException(resp.statusCode, "友好速搭服务器异常", null)
    }

    /**
     * 查询运单信息
     */
    fun queryCartWithinShipments(addressId: Int) : List<Map<String, Any?>> {
        val customerSession = getYhsdCustomerSession()
        val token = openApiService.getAuthTokenFromCache()
        val resp = webApiService.authRequest(
                method = YouHaoWebApi.WithinShipments.method,
                path = YouHaoWebApi.WithinShipments.path,
                customerSession = customerSession,
                accessToken = token,
                queryData = hashMapOf(
                        "address_id" to addressId.toString(),
                        "payment_method_type" to "offline"
                )
        )
        val code = resp["code"] as Int
        val message = resp["message"] as String
        if ( code == 200 ) {
            return resp["shipments"] as List<Map<String, Any?>>
        } else {
            throw YouHaoException(code, message, resp)
        }
    }

}