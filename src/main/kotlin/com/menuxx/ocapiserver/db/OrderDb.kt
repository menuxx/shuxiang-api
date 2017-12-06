package com.menuxx.ocapiserver.db

import com.menuxx.ocapiserver.PageParam
import com.menuxx.ocapiserver.bean.Item
import com.menuxx.ocapiserver.bean.Order
import com.menuxx.ocapiserver.bean.VipChannel
import com.menuxx.ocapiserver.db.tables.TItem
import com.menuxx.ocapiserver.db.tables.TOrder
import com.menuxx.ocapiserver.db.tables.TVipChannel
import com.zaxxer.hikari.HikariDataSource
import org.jooq.DSLContext
import org.jooq.types.UInteger
import org.springframework.stereotype.Service

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/5
 * 微信: yin80871901
 */
@Service
class OrderDb(private val dsl: DSLContext) {

    /**
     * 获取订单详情，包括订单渠道，订单商品
     */
    fun getOrderDetail(orderId: Int) : Order {
        val tOrder = TOrder.T_ORDER
        val tVipChannel = TVipChannel.T_VIP_CHANNEL
        val tItem = TItem.T_ITEM
        return dsl.select()
                .from(tOrder)
                .leftJoin(tVipChannel).on(tOrder.CHANNEL_ID.eq(tVipChannel.ID))
                .leftJoin(tItem).on(tItem.ID.eq(tVipChannel.ITEM_ID))
                .where(tOrder.ID.eq(UInteger.valueOf(orderId)))
                .fetchOne().let {
                    val order = it.into(Order::class.java)
                    order.vipChannel = it.into(VipChannel::class.java)
                    order.vipChannel.item = it.into(Item::class.java)
                    order
                }
    }

    /**
     * 1. 获取商户关于渠道的订单
     * 2. 获取商户全部订单
     * 支持分页
     */
    fun loadOrders(creatorId: Int, channelId: Int?, page: PageParam) : List<Order> {
        val tOrder = TOrder.T_ORDER
        val tVipChannel = TVipChannel.T_VIP_CHANNEL
        val tItem = TItem.T_ITEM
        // where 条件
        val condition = if (channelId != null) {
            // 筛选 商户
            tOrder.MERCHANT_ID.eq(UInteger.valueOf(creatorId))
                            // 删选 channelId
                            .and(tOrder.CHANNEL_ID.eq(UInteger.valueOf(channelId)))
        } else {
            tOrder.MERCHANT_ID.eq(UInteger.valueOf(creatorId))
        }

        return dsl.select()
                .from(tOrder)
                .leftJoin(tVipChannel).on(tOrder.CHANNEL_ID.eq(tVipChannel.ID))
                .leftJoin(tItem).on(tItem.ID.eq(tVipChannel.ITEM_ID))
                .where(condition)
                .orderBy(tOrder.CREATE_AT.desc())
                .limit(page.getLimit())
                .offset(page.getOffset())
                .fetchArray().map {
                    val order = it.into(Order::class.java)
                    order.vipChannel = it.into(VipChannel::class.java)
                    order.vipChannel.item = it.into(Item::class.java)
                    order
                }
    }

}