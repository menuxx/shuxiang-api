package com.menuxx.miaosha.service

import com.github.binarywang.wxpay.constant.WxPayConstants
import com.menuxx.common.bean.Order
import com.menuxx.common.bean.OrderCharge
import com.menuxx.common.bean.OrderItem
import com.menuxx.common.db.UserAddressDb
import com.menuxx.common.db.UserDb
import com.menuxx.common.db.VChannelDb
import com.menuxx.genChannelOrderNo
import com.menuxx.genRandomString32
import org.springframework.stereotype.Service
import java.util.*

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/21
 * 微信: yin80871901
 */

@Service
class ChannelOrderService(
        private val channelDb: VChannelDb,
        private val userDb: UserDb,
        private val addressDb: UserAddressDb
) {

    /**
     * 订单支付交易的创建逻辑
     */
    fun createChannelOrderCharge(order: Order, openId: String) : OrderCharge {
        val charge = OrderCharge()
        charge.openid = openId
        // 不能超过 128 个字符，否则会失败
        // 长度超过 128 ，就加省略号
        charge.body = if (order.orderName.length > 128) { order.orderName.slice(IntRange(0, 123)) + "..." } else { order.orderName }
        charge.tradeType = WxPayConstants.TradeType.JSAPI
        charge.deviceInfo = "WechatMpWeb"
        charge.attach = "channelId=${order.channelId}&userId=${order.userId}&orderId=${order.id}"
        // 生成订单编号 32 位
        charge.outTradeNo = genChannelOrderNo(userId = order.userId, channelId = order.channelId)
        // 生成随机按号 32 位
        charge.nonceStr = genRandomString32()
        // 需要支付的总价格
        charge.totalFee = order.payAmount
        charge.timeStart = Date(System.currentTimeMillis())
        // 2 小时后过期
        charge.timeExpire = Date(System.currentTimeMillis() + 7200 * 1000)
        return charge
    }

    /**
     * 维护订单的创建逻辑
     */
    fun createChannelOrder(channelId: Int, userId: Int, addressId: Int) : Order {
        // 获取渠道详细信息包括 item
        val channel = channelDb.getById(channelId)!!
        val user = userDb.getUserDetailById(userId)!!
        val address = addressDb.getUserAddressById(user.id, addressId)!!

        val totalFee = channel.payFee + channel.expressFee

        val order = Order()

        // 基础信息香相关
        order.channelId = channelId
        order.count = 1
        order.userId = userId
        order.merchantId = channel.merchantId
        order.orderName = "${channel.ownerName}赠于${user.userName}${channel.item.name}"
        order.orderNo = genChannelOrderNo(userId, channelId)
        order.payAmount = totalFee
        order.totalAmount = totalFee

        // 收货信息相关
        order.receiverAreaAddress = address.province + address.city + address.country
        order.receiverDetailInfo = address.detailInfo
        order.receiverName = address.receiverName
        order.receiverPhoneNumber = address.phoneNumber
        order.receiverPostalCode = address.postalCode

        // 订单商品相关
        val item = OrderItem()
        item.itemId = channel.itemId
        item.itemName = channel.item.name
        item.itemQuantity = 1 // 目前默认都是一个
        item.itemPayAmount = 0 // 目前书籍没有定价

        order.items = listOf(item)

        // 订单状态相关
        order.status = Order.CREATED
        order.paid = 0

        return order
    }

}