package com.menuxx.common.db

import com.menuxx.common.bean.MallYhsdOrder
import com.menuxx.common.db.tables.TMallYhsdOrder
import com.menuxx.weixin.util.nullSkipUpdate
import org.jooq.DSLContext
import org.springframework.stereotype.Service

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2018/1/31
 * 微信: yin80871901
 */

@Service
class MallOrderDb (
        private val dsl: DSLContext
) {

    private val tMallOrder = TMallYhsdOrder.T_MALL_YHSD_ORDER

    fun updateYhsdOrderId(orderNo: String, orderId: String) : Int {
        return dsl.update(tMallOrder).set(tMallOrder.ORDER_ID, orderId).where(tMallOrder.LOCAL_ORDER_NO.eq(orderNo)).execute()
    }

    fun insertOrder(yhsdOrder: MallYhsdOrder) : Int {
        return dsl.insertInto(tMallOrder).set(nullSkipUpdate(dsl.newRecord(tMallOrder, yhsdOrder))).execute()
    }

    fun getByOrderNo(orderNo: String) : MallYhsdOrder? {
        return dsl.select()
                .from(tMallOrder)
                .where(tMallOrder.LOCAL_ORDER_NO.eq(orderNo))
                .fetchOne()
                ?.into(MallYhsdOrder::class.java)
    }

}