package com.menuxx.code.service

import com.menuxx.code.db.ItemCodeBatchDb
import com.menuxx.code.mongo.ItemCodeRepository
import com.menuxx.common.bean.ItemCodeBatch.STATUS_FINISH
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2018/1/17
 * 微信: yin80871901
 */
@Service
class CodeService (
        private val itemCodeRepository: ItemCodeRepository,
        private val itemCodeBatchDb: ItemCodeBatchDb
) {

    @Transactional
    fun tryUpdateBatchCodeToAssignedStatus(batchId: Int, itemId: Int) : Int {
        val batch = itemCodeBatchDb.getBatchById(batchId)
        if ( batch.status == STATUS_FINISH  ) {
            itemCodeRepository.assignItemIdToBatchCode(batchId, itemId)
            itemCodeBatchDb.updateBatchToAssignedById(batchId, itemId)
            return 2
        }
        return 1
    }

}