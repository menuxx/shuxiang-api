package com.menuxx.code.factory

import com.menuxx.code.bean.SXItemCode
import com.menuxx.code.bean.SXItemCodeCreated
import com.menuxx.code.code.ItemCodeFactory
import com.menuxx.code.db.ItemCodeBatchDb
import com.menuxx.code.db.ItemCodeTaskDb
import com.menuxx.code.mongo.ItemCodeRepository
import com.menuxx.genRandomString
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class BatchTransactionService (
        private val codeRepo: ItemCodeRepository,
        private val itemCodeBatchDb: ItemCodeBatchDb,
        private val itemCodeTaskDb: ItemCodeTaskDb,
        private val itemCodeFactory: ItemCodeFactory
) {

    /**
     * 执行该流程的时候，数据库的基本信息，已经就绪
     */
    @Transactional
    fun doOneBatch(count: Int, startCode: String, endCode: String) {
        val batch = itemCodeBatchDb.findBatchByStartCode(startCode)!!
        var _startCode = startCode
        val codes = (1..count).map {
            val sxCode = SXItemCode(id = null, status = SXItemCodeCreated,
                    code = _startCode, salt = genRandomString(6),
                    batchId = batch.id, exportTime = null, itemId = null,
                    userId = null, createAt = Date(), updateAt = Date(), consumeTime = null
            )
            _startCode = itemCodeFactory.next(_startCode)
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