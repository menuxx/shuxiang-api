package com.menuxx.code.ctrl

import com.menuxx.Const
import com.menuxx.apiserver.bean.ApiResp
import com.menuxx.code.db.ItemCodeTaskDb
import com.menuxx.code.mongo.ItemCodeRepository
import com.menuxx.code.mq.CodeOnceBatchPublisher
import com.menuxx.code.service.CodeBatchService
import com.menuxx.common.bean.ItemCodeBatch
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.hibernate.validator.constraints.NotEmpty
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RequestMapping("/code")
@RestController
class CodeCtrl(
        private val batchPublisher: CodeOnceBatchPublisher,
        private val codeBatchService : CodeBatchService,
        private val codeItemCodeTaskDb: ItemCodeTaskDb,
        private val itemCodeRepository: ItemCodeRepository
) {

    @GetMapping("/download/batch_{batchId}.xslx")
    fun downloadBatch(@PathVariable batchId: Int) {
        val codes = itemCodeRepository.loadBatchCode(batchId)
        if ( codes != null ) {

        }
    }

    data class TaskParams(@NotEmpty val totalNum: Int, @NotEmpty val remark: String)
    @PostMapping("task")
    fun submitMakeTask(@Valid @RequestBody param: TaskParams) : ApiResp {

        val batches = codeBatchService.doBatchPlan(param.totalNum)

        var batchNum = 1

        // 构建数据库对象插入
        val codeBatches = batches.map { plan ->
            val batch = ItemCodeBatch()
            batch.batchNum = batchNum
            batchNum ++
            batch.endCode = plan.endCode
            batch.startCode = plan.startCode
            batch.count = plan.count
            batch
        }.toTypedArray()
        // 基本信息入库
        codeItemCodeTaskDb.createTaskAndBatch(param.remark, codeBatches)
        // 发送消息, 开始 批量插入到 mongodb
        batches.forEach { batch ->
            batchPublisher.sendOneBatch(batch)
        }

        return ApiResp(Const.NotErrorCode, "任务已分配")

    }

}