package com.menuxx.code.db

import com.menuxx.apiserver.PageParam
import com.menuxx.common.bean.ItemCodeBatch
import com.menuxx.common.bean.ItemCodeTask
import com.menuxx.common.bean.ItemCodeTask.STATUS_FINISH
import com.menuxx.common.bean.ItemCodeTask.STATUS_CREATED
import com.menuxx.common.db.tables.TItemCodeBatch
import com.menuxx.common.db.tables.TItemCodeTask
import com.menuxx.weixin.util.nullSkipUpdate
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jooq.impl.DSL.count
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

    fun getTaskByBatchId(batchId: Int) : ItemCodeTask {
        return dsl.select().from(tItemCodeTask).where(
                tItemCodeTask.ID.eq(
                        dsl.select(tItemCodeBatch.TASK_ID).from(tItemCodeBatch).where(tItemCodeBatch.ID.eq(UInteger.valueOf(batchId)))
                )
        ).fetchOneInto(ItemCodeTask::class.java)
    }

    /**
     * 创建任务和对应的批处理
     */
    @Transactional
    fun createTaskAndBatch(remark: String, batches: Array<ItemCodeBatch>) : ItemCodeTask {
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
        return dbTask.into(ItemCodeTask::class.java)
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
        // 完成数
        val count2 = dsl.selectCount().from(tItemCodeBatch).where(tItemCodeBatch.TASK_ID.eq(UInteger.valueOf(taskId))
                .and(tItemCodeBatch.STATUS.eq(ItemCodeBatch.STATUS_FINISH))).execute()

        // 总数
        val count1 = dsl.selectCount().from(tItemCodeBatch).where(
                tItemCodeBatch.TASK_ID.eq(UInteger.valueOf(taskId))
        ).execute()
        // 已完成数量
        return dsl.update(tItemCodeTask)
                .set(tItemCodeTask.STATUS, STATUS_FINISH)
                .where(tItemCodeTask.ID.eq(UInteger.valueOf(taskId)).and(DSL.`val`(count2).eq(DSL.`val`(count1))))
                .execute()
    }

    fun loadTask(page: PageParam): List<ItemCodeTask> {
        return dsl.select().from(tItemCodeTask).offset(page.getOffset()).limit(page.getLimit()).fetchArray().map {
            it.into(ItemCodeTask::class.java)
        }.map { task ->
            task.batches = dsl.select().from(tItemCodeBatch).where(tItemCodeBatch.TASK_ID.eq(UInteger.valueOf(task.id))).fetchArray().map {
                it.into(ItemCodeBatch::class.java)
            }
            task
        }
    }

    fun getTaskDetails(taskId: Int) : ItemCodeTask {
        val task = dsl.select().from(tItemCodeTask).where(tItemCodeTask.ID.eq(UInteger.valueOf(taskId))).fetchOneInto(ItemCodeTask::class.java)
        task.batches = dsl.select().from(tItemCodeBatch).where(tItemCodeBatch.TASK_ID.eq(UInteger.valueOf(task.id))).fetchInto(ItemCodeBatch::class.java)
        return task
    }

}