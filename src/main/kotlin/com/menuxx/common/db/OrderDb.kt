package com.menuxx.common.db

import com.menuxx.apiserver.PageParam
import com.menuxx.common.bean.*
import com.menuxx.common.db.tables.*
import com.menuxx.weixin.util.nullSkipUpdate
import org.jooq.DSLContext
import org.jooq.types.UInteger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/5
 * 微信: yin80871901
 */
@Service
class OrderDb(
        private val dsl: DSLContext,
        private val orderItemDb: OrderItemDb
) {

    private final val tUser = TUser.T_USER
    private final val tOrder = TOrder.T_ORDER
    private final val tVChannel = TVChannel.T_V_CHANNEL
    private final val tItem = TItem.T_ITEM
    private final val tOrderItem = TOrderItem.T_ORDER_ITEM

    fun getUserChannelOrder(userId: Int, channelId: Int) : Order? {
        val order = dsl.select().from(tOrder)
                .where(tOrder.USER_ID.eq(UInteger.valueOf(userId)).and(tOrder.CHANNEL_ID.eq(UInteger.valueOf(channelId))))
                .fetchOne()?.into(Order::class.java)
        if ( order != null ) {
            order.items = orderItemDb.findOrderItems(order.id)
        }
        return order
    }

    @Transactional
    fun insertOrder(order: Order) : Order {
        val newOrder = dsl.insertInto(tOrder).set( nullSkipUpdate(dsl.newRecord(tOrder, order)) ).returning().fetchOne().into(Order::class.java)
        newOrder.items = order.items.map {
            it.orderId = newOrder.id
            orderItemDb.insertOrderItem(it)
        }
        return newOrder
    }

    fun updateOrderPaid(orderId: Int, paid: Int) : Int {
        return dsl.update(tOrder)
                .set(tOrder.PAID, paid)
                .where(tOrder.ID.eq(UInteger.valueOf(orderId)))
                .execute()
    }

    fun updateOrderShareImage(orderId: Int, shareImage: String) : Int {
        return dsl.update(tOrder)
                .set(tOrder.SHARE_IMAGE, shareImage)
                .where(tOrder.ID.eq(UInteger.valueOf(orderId)))
                .execute()
    }

    fun updateOrderConsumed(orderId: Int, status: Int, queueNum: Int) : Int {
        return dsl.update(tOrder)
                .set(tOrder.STATUS, Order.CONSUMED)
                .set(tOrder.QUEUE_NUM, queueNum)
                .where(tOrder.ID.eq(UInteger.valueOf(orderId)))
                .execute()
    }

    fun updateOrderExpress(orderId: Int, expressNo: String, expressName: String) : Int {
        return dsl.update(tOrder)
                .set(tOrder.EXPRESS_NO, expressNo)
                .set(tOrder.EXPRESS_NAME, expressName)
                .where(tOrder.ID.eq(UInteger.valueOf(orderId)))
                .execute()
    }

    fun getOrderById(orderId: Int) : Order? {
        return dsl.select().from(tOrder).where(tOrder.ID.eq(UInteger.valueOf(orderId)))?.fetchOne()?.into(Order::class.java)
    }

    /**
     * 获取订单详情，包括订单渠道，订单商品
     */
    fun getOrderDetails(orderId: Int) : Order? {
        val record = dsl.select()
                .from(tOrder)
                .leftJoin(tUser).on(tOrder.USER_ID.eq(tUser.ID))
                .leftJoin(tVChannel).on(tOrder.CHANNEL_ID.eq(tVChannel.ID))
                .leftJoin(tItem).on(tItem.ID.eq(tVChannel.ITEM_ID))
                .where(tOrder.ID.eq(UInteger.valueOf(orderId)))
                .fetchOne()

        val order = record?.into(tOrder)?.into(Order::class.java)
        order?.user = record?.into(tUser)?.into(User::class.java)
        order?.items = loadOrderItems(orderId)
        order?.vChannel = record?.into(tVChannel)?.into(VChannel::class.java)
        order?.vChannel?.item = record?.into(tItem)?.into(Item::class.java)
        return order
    }

    /**
     * 获取指定用用户订单详情
     */
    fun getUserOrderDetails(orderId: Int, userId: Int) : Order? {
        val record = dsl.select()
                .from(tOrder)
                .leftJoin(tUser).on(tOrder.USER_ID.eq(tUser.ID))
                .leftJoin(tVChannel).on(tOrder.CHANNEL_ID.eq(tVChannel.ID))
                .leftJoin(tItem).on(tItem.ID.eq(tVChannel.ITEM_ID))
                .where(tOrder.ID.eq(UInteger.valueOf(orderId)).and(tUser.ID.eq(UInteger.valueOf(userId))))
                .fetchOne()

        val order = record?.into(tOrder)?.into(Order::class.java)
        if (order != null) {
            order.user = record.into(tUser)?.into(User::class.java)
            order.vChannel = record.into(tVChannel)?.into(VChannel::class.java)
            order.vChannel.item = record.into(tItem)?.into(Item::class.java)
            order.items = loadOrderItems(order.id)
        }
        return order
    }

    fun loadOrderItems(orderId: Int) : List<OrderItem> {
        return dsl.select().from(tOrderItem).where(tOrderItem.ORDER_ID.eq(UInteger.valueOf(orderId))).fetchArray().map {
            it.into(OrderItem::class.java)
        }
    }


    fun loadMyOrders(userId: Int, page: PageParam) : List<Order> {
        return dsl.select()
                .from(tOrder)
                .leftJoin(tVChannel).on(tOrder.CHANNEL_ID.eq(tVChannel.ID))
                .leftJoin(tItem).on(tItem.ID.eq(tVChannel.ITEM_ID))
                .where(tOrder.USER_ID.eq(UInteger.valueOf(userId)))
                .orderBy(tOrder.CREATE_AT.desc())
                .limit(page.getLimit())
                .offset(page.getOffset())
                .fetchArray().map {
            val order = it.into(tOrder).into(Order::class.java)
            order?.vChannel = it.into(tVChannel).into(VChannel::class.java)
            order?.vChannel?.item = it.into(tItem).into(Item::class.java)
            order?.items = loadOrderItems(order.id)
            order
        }
    }

    /**
     * 1. 获取商户关于渠道的订单
     * 2. 获取商户全部订单
     * 支持分页
     */
    fun loadMerchantOrders(merchantId: Int, channelId: Int?, page: PageParam) : List<Order> {
        // where 条件
        val condition = if (channelId != null) {
            // 筛选 商户
            tOrder.MERCHANT_ID.eq(UInteger.valueOf(merchantId))
                            // 删选 channelId
                            .and(tOrder.CHANNEL_ID.eq(UInteger.valueOf(channelId)))
        } else {
            tOrder.MERCHANT_ID.eq(UInteger.valueOf(merchantId))
        }

        return dsl.select()
                .from(tOrder)
                .leftJoin(tVChannel).on(tOrder.CHANNEL_ID.eq(tVChannel.ID))
                .leftJoin(tItem).on(tItem.ID.eq(tVChannel.ITEM_ID))
                .where(condition)
                .orderBy(tOrder.CREATE_AT.desc())
                .limit(page.getLimit())
                .offset(page.getOffset())
                .fetchArray().map {
                    val order = it.into(tOrder).into(Order::class.java)
                    order?.vChannel = it.into(tVChannel).into(VChannel::class.java)
                    order?.items = loadOrderItems(order.id)
                    order?.vChannel?.item = it.into(tItem).into(Item::class.java)
                    order
                }
    }

}