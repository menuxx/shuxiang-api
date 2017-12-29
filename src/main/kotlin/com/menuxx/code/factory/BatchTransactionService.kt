package com.menuxx.code.factory

import com.menuxx.code.bean.SXItemCode
import com.menuxx.code.bean.SXItemCodeCreated
import com.menuxx.code.code.ItemCode
import com.menuxx.code.db.ItemCodeBatchDb
import com.menuxx.code.db.ItemCodeTaskDb
import com.menuxx.code.mongo.ItemCodeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BatchTransactionService (
        private val codeRepo: ItemCodeRepository,
        private val itemCodeBatchDb: ItemCodeBatchDb,
        private val itemCodeTaskDb: ItemCodeTaskDb
) {

    /**
     * 执行该流程的时候，数据库的基本信息，已经就绪
     */
    @Transactional
    fun doOneBatch(count: Int, startCode: String, endCode: String) {
        val batch = itemCodeBatchDb.findBatchByStartCode(startCode)!!
        var _startCode = startCode
        val codes = (1..count).map {
            val sxCode = SXItemCode(id = null, status = SXItemCodeCreated, code = _startCode, batchId = batch.id, exportTime = null, itemId = null, userId = null, createAt = null, updateAt = null, consumeTime = null)
            _startCode = ItemCode(_startCode).next()
            sxCode
        }
        // 写入到 mongodb
        codeRepo.bulkWrite(codes.toTypedArray())
        // 更新数据库批次记录 到结束状态
        itemCodeBatchDb.updateBatchToFinishByStartCode(startCode)
        // 尝试更新到结束状态
        itemCodeTaskDb.tryUpdateFinish(batch.taskId)
    }

}