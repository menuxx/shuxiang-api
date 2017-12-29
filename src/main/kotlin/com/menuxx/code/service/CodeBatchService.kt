package com.menuxx.code.service

import com.google.common.collect.Lists
import com.menuxx.code.mq.OneBatch
import com.menuxx.code.code.ItemCode
import com.menuxx.code.db.ItemCodeBatchDb
import org.springframework.stereotype.Service

@Service
class CodeBatchService (private val codeBatchDb: ItemCodeBatchDb) {

    // 一个批次由一千个 code 组成，可以不到1000个，但是最多1000个
    private final val OnceBatchUnitCount = 1000

    /**
     * 计算批次数量
     */
    fun calcBatchCount(totalNum: Int) : Array<Int> {
        val count = Math.floor((totalNum / OnceBatchUnitCount).toDouble()).toInt()
        // 计算出 满 数量的的 批次个数
        val fullBatch = (1..count).map {
            OnceBatchUnitCount
        }.toCollection(Lists.newArrayList())
        // 计算最后一个批次的数量
        val leftOver = totalNum - (count * OnceBatchUnitCount)
        fullBatch.add(leftOver)
        return fullBatch.toTypedArray()
    }

    /**
     * 超找批处理系统中最后一次工作产生的编号
     */
    fun doBatchPlan(totalNum: Int) : Array<OneBatch> {
        val lastBatch = codeBatchDb.findLastBatch()
        // 找不到一批，说明是第一次生成，从 0 开始
        var startCode = if (lastBatch == null) {
            "0"
        } else {
            ItemCode(lastBatch.endCode).next()
        }

        // 计算需要 需要产生的批次 和每个批次的数量
        val batches = calcBatchCount(totalNum)

        return batches.map { batchCount ->
            // 计算一个批次开始和结束的 code
            val batchStartCode = startCode
            val batchEndCode = ItemCode(batchStartCode).offsetBit(batchCount)
            startCode = batchEndCode
            OneBatch(count = batchCount, startCode = batchStartCode, endCode = batchEndCode)
        }.toTypedArray()

    }

}