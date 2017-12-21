package com.menuxx.common.db

import com.menuxx.common.bean.OrderItem
import com.menuxx.common.db.tables.TOrderItem
import org.jooq.DSLContext
import org.jooq.types.UInteger
import org.springframework.stereotype.Service

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/21
 * 微信: yin80871901
 */
@Service
class OrderItemDb(private val dsl: DSLContext) {

    private val tOrderItem = TOrderItem.T_ORDER_ITEM

    fun insertOrderItem(item: OrderItem) : OrderItem {
        return dsl.insertInto(tOrderItem).set(dsl.newRecord(tOrderItem, item)).returning().fetchOne().into(OrderItem::class.java)
    }

    fun findOrderItems(orderId: Int) : List<OrderItem> {
        return dsl.select().from(tOrderItem).where(tOrderItem.ORDER_ID.eq(UInteger.valueOf(orderId))).fetchArray().map {
            it.into(OrderItem::class.java)
        }
    }

}