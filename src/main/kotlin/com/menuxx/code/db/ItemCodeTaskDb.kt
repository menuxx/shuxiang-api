package com.menuxx.code.db

import com.menuxx.common.bean.ItemCodeBatch
import com.menuxx.common.bean.ItemCodeTask
import com.menuxx.common.bean.ItemCodeTask.STATUS_FINISH
import com.menuxx.common.bean.ItemCodeTask.STATUS_CREATED
import com.menuxx.common.db.tables.TItemCodeBatch
import com.menuxx.common.db.tables.TItemCodeTask
import com.menuxx.weixin.util.nullSkipUpdate
import org.jooq.DSLContext
import org.jooq.types.UInteger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class ItemCodeTaskDb (
        private val dsl: DSLContext,
        private val itemCodeBatchDb: ItemCodeBatchDb
) {

    private final val tItemCodeTask = TItemCodeTask.T_ITEM_CODE_TASK

    private final val tItemCodeBatch = TItemCodeBatch.T_ITEM_CODE_BATCH

    /**
     * 创建任务和对应的批处理
     */
    @Transactional
    fun createTaskAndBatch(remark: String, batches: Array<ItemCodeBatch>) {
        val task = ItemCodeTask()
        task.remark = remark
        task.status = STATUS_CREATED
        val dbTask = dsl.insertInto(tItemCodeTask).set( nullSkipUpdate(dsl.newRecord(tItemCodeTask, task)) ).returning().fetchOne()
        batches.map {
            // 关联 taskId
            it.taskId  = dbTask.id.toInt()
            it
        }
        itemCodeBatchDb.insertAllBatch(batches)
    }

    /**
     * 更新一个任务的状态为结束状态
     */
    fun updateTaskFinish(taskId: Int) : Int {
        return dsl.update(tItemCodeTask).set(tItemCodeTask.STATUS, STATUS_FINISH).where(tItemCodeTask.ID.eq(UInteger.valueOf(taskId))).execute()
    }

    /**
     * 当子批处理任务都是完成状态的时候，更新为已完成状态
     */
    fun tryUpdateFinish(taskId: Int) : Int {
        return dsl.update(tItemCodeTask)
                .set(tItemCodeTask.STATUS, STATUS_FINISH)
                .where(
                        tItemCodeTask.ID.eq(UInteger.valueOf(taskId))
                                .and(
                                       dsl.select().from().where(tItemCodeBatch.TASK_ID.eq(UInteger.valueOf(taskId)))
                                               .asTable().eq(
                                                dsl.select().from(tItemCodeBatch).where(tItemCodeBatch.TASK_ID.eq(UInteger.valueOf(taskId))
                                                       .and(tItemCodeBatch.STATUS.eq(ItemCodeBatch.STATUS_FINISH))).asTable()
                                       )
                                )
                )
                .execute()
    }

}