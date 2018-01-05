package com.menuxx.code.ctrl

import com.menuxx.Const
import com.menuxx.apiserver.bean.ApiRespWithData
import com.menuxx.code.db.ItemCodeTaskDb
import com.menuxx.code.mongo.ItemCodeRepository
import com.menuxx.code.mq.CodeOnceBatchPublisher
import com.menuxx.code.service.CodeBatchService
import com.menuxx.common.bean.ItemCodeBatch
import com.menuxx.common.bean.ItemCodeTask
import com.menuxx.common.prop.AppProps
import org.hibernate.validator.constraints.NotEmpty
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RequestMapping("/item_code")
@RestController
class ItemCodeCtrl(
        private val batchPublisher: CodeOnceBatchPublisher,
        private val codeBatchService : CodeBatchService,
        private val codeItemCodeTaskDb: ItemCodeTaskDb,
        private val itemCodeRepository: ItemCodeRepository,
        private val appProps: AppProps
) {

    @GetMapping("/download/batch/{batchId}")
    @Transactional
    fun downloadBatch(@PathVariable batchId: Int) : ResponseEntity<Any> {
        val codes = itemCodeRepository.loadBatchCode(batchId)
        val task = codeItemCodeTaskDb.getTaskByBatchId(batchId)
        if (codes != null) {
            val downloadBytes = codeBatchService.genExcel2003(task.remark, codes, appProps.codeBaseUrl)
            // 更新到到导出状态
            itemCodeRepository.updateBatchExport(batchId)
            val headers = HttpHeaders()
            headers.set("Content-Type", "application/vnd.ms-excel")
            headers.set("Content-length", downloadBytes.size.toString())
            headers.set("Content-Disposition", "attachment;filename=batch_${batchId}_${task.remark}.xls")
            return ResponseEntity(downloadBytes, headers, HttpStatus.OK)
        }
        return ResponseEntity.notFound().build()
    }

    data class TaskParams(@NotEmpty val totalNum: Int, @NotEmpty val remark: String)
    @PostMapping("/task")
    fun submitMakeTask(@Valid @RequestBody param: TaskParams) : ApiRespWithData<ItemCodeTask> {

        val batches = codeBatchService.doBatchPlan(param.totalNum)

        var batchNum = 1

        // 构建数据库对象插入
        val codeBatches = batches.map { plan ->
            val batch = ItemCodeBatch()
            batch.batchNum = batchNum
            batchNum ++
            batch.startCode = plan.startCode
            batch.endCode = plan.endCode
            batch.count = plan.count
            batch
        }.toTypedArray()
        // 基本信息入库
        val task = codeItemCodeTaskDb.createTaskAndBatch(param.remark, codeBatches)
        // 发送消息, 开始 批量插入到 mongodb
        batches.forEach { batch ->
            batchPublisher.sendOneBatch(batch)
        }

        return ApiRespWithData(Const.NotErrorCode, "任务已分配", task)

    }

}