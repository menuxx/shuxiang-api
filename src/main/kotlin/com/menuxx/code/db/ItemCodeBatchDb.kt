package com.menuxx.code.db

import com.menuxx.common.bean.ItemCodeBatch
import com.menuxx.common.db.tables.TItemCodeBatch
import com.menuxx.weixin.util.nullSkipUpdate
import org.jooq.DSLContext
import org.jooq.types.UInteger
import org.springframework.stereotype.Service

@Service
class ItemCodeBatchDb (private val dsl: DSLContext) {

    private final val tItemCodeBatch = TItemCodeBatch.T_ITEM_CODE_BATCH

    /**
     * 批量插入
     */
    fun insertAllBatch(batches: Array<ItemCodeBatch>) : IntArray {
        val records = batches.map { nullSkipUpdate(dsl.newRecord(tItemCodeBatch, it)) }
        return dsl.batchInsert(records).execute()
    }

    /**
     * 更新状态到结束，通过 start_code
     */
    fun updateBatchToFinishByStartCode(startCode: String) : Int {
        return dsl.update(tItemCodeBatch).set(tItemCodeBatch.STATUS, ItemCodeBatch.STATUS_FINISH)
                .where(tItemCodeBatch.START_CODE.eq(startCode)).execute()
    }

    /**
     * 通过 first 查找 code
     */
    fun findBatchByStartCode(startCode: String) : ItemCodeBatch? {
        return dsl.select().from(tItemCodeBatch)
                .where(tItemCodeBatch.START_CODE.eq(startCode))
                .fetchOneInto(ItemCodeBatch::class.java)
    }

    fun getBatchById(batchId: Int) : ItemCodeBatch {
        return dsl.select().from(tItemCodeBatch)
                .where(tItemCodeBatch.ID.eq(UInteger.valueOf(batchId)))
                .fetchOneInto(ItemCodeBatch::class.java)
    }

    fun updateBatchToAssignedById(batchId: Int, itemId: Int) : Int {
        return dsl.update(tItemCodeBatch).set(tItemCodeBatch.ID, UInteger.valueOf(batchId)).set(tItemCodeBatch.ITEM_ID, UInteger.valueOf(itemId)).execute()
    }

    /**
     * 找出最后一个
     */
    fun findLastBatch() : ItemCodeBatch? {
        return dsl.select().from(tItemCodeBatch)
                .orderBy(tItemCodeBatch.CREATE_AT.desc())
                .offset(0).limit(1)
                .fetchOneInto(ItemCodeBatch::class.java)
    }

}