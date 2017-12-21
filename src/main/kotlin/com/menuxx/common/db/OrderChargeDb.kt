package com.menuxx.common.db

import com.menuxx.common.bean.OrderCharge
import com.menuxx.common.db.tables.TOrderCharge
import org.jooq.DSLContext
import org.springframework.stereotype.Service
import java.sql.Timestamp

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/21
 * 微信: yin80871901
 */
@Service
class OrderChargeDb(private val dsl: DSLContext) {

    private val tOrderCharge = TOrderCharge.T_ORDER_CHARGE

    fun insertChargeRecord(charge: OrderCharge) : OrderCharge {
        return dsl.insertInto(tOrderCharge).set(dsl.newRecord(tOrderCharge, charge)).returning().fetchOne().into(OrderCharge::class.java)
    }

    fun findChargeRecordByOutTradeNo(outTradeNo: String): OrderCharge? {
        return dsl.select().from(tOrderCharge).where(tOrderCharge.OUT_TRADE_NO.eq(outTradeNo)).fetchOne()?.into(OrderCharge::class.java)
    }

    /**
     * 只更新部分字段
     */
    fun updateChargeRecordByOutTradeNo(charge: OrderCharge, outTradeNo: String) : Int {
        return dsl.update(tOrderCharge)
                .set(tOrderCharge.APPID, charge.appid)
                .set(tOrderCharge.MCH_ID, charge.mchId)
                .set(tOrderCharge.CASH_FEE, charge.cashFee)
                .set(tOrderCharge.CASH_FEE_TYPE, charge.cashFeeType)
                .set(tOrderCharge.ERR_CODE, charge.errCode)
                .set(tOrderCharge.RESULT_CODE, charge.resultCode)
                .set(tOrderCharge.GOODS_TAG, charge.goodsTag)
                .set(tOrderCharge.TRANSACTION_ID, charge.transactionId)
                .set(tOrderCharge.SIGN, charge.sign)
                .set(tOrderCharge.SIGN_TYPE, charge.signType)
                .set(tOrderCharge.FEE_TYPE, charge.feeType)
                .set(tOrderCharge.SETTLEMENT_TOTAL_FEE, charge.settlementTotalFee)
                .set(tOrderCharge.TIME_END, Timestamp.from(charge.timeEnd.toInstant()))
                .where(tOrderCharge.OUT_TRADE_NO.eq(outTradeNo))
                .execute()
    }

}