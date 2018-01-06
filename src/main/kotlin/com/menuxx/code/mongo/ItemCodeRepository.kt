package com.menuxx.code.mongo

import com.menuxx.AllOpen
import com.menuxx.code.bean.SXItemCode
import com.menuxx.code.bean.SXItemCodeBinded
import com.menuxx.code.bean.SXItemCodeConsumed
import com.menuxx.code.bean.SXItemCodeExported
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository
import java.util.*

@AllOpen
@Repository
class ItemCodeRepository (
        private val dbTpl : MongoTemplate
) {

    /**
     * 批量写入 item_code
     */
    fun bulkWrite(codes: Array<SXItemCode>) {
        dbTpl.insert(codes.toList(), SXItemCode::class.java)
    }

    /**
     * 更新 mongodb 指定批次的数码 到到处状态
     */
    fun updateBatchExport(batchId: Int) : Boolean {
        val query = Query(Criteria.where("batchId").`is`(batchId))
        val update = Update()
                .set("status", SXItemCodeExported)
                .set("exportTime", Date())
        return dbTpl.updateMulti(query, update, SXItemCode::class.java).wasAcknowledged()
    }

    /**
     * 获取一个批次的 code
     */
    fun loadBatchCode(batchId: Int) : Array<SXItemCode>? {
        val query = Query(Criteria.where("batchId").`is`(batchId))
        return dbTpl.find(query, SXItemCode::class.java)?.toTypedArray()
    }

    /**
     * 获取 xs_item_code 数据通过 base62 code
     */
    fun getItemCodeDataByCode(code: String) : SXItemCode {
        val query = Query(Criteria.where("code").`is`(code))
        return dbTpl.findOne(query, SXItemCode::class.java)
    }

    /**
     * 给一个批次绑定 itemId
     */
    fun bindItemIdToBatchCode(batchId: Int, itemId: Int) : Boolean {
        val query = Query(Criteria.where("batchId").`is`(batchId))
        val update = Update()
                .set("status", SXItemCodeBinded)
                .set("itemId", itemId)
        return dbTpl.updateMulti(query, update, SXItemCode::class.java).wasAcknowledged()
    }

    /**
     * 更新一个码到消费状态
     */
    fun updateCodeToConsume(code: String, salt: String, userId: Int) : Boolean {
        val query = Query(Criteria.where("code").`is`(code).andOperator(Criteria.where("salt").`is`(salt)))
        val update = Update()
                .set("status", SXItemCodeConsumed)
                .set("userId", userId)
        return dbTpl.updateFirst(query, update, SXItemCode::class.java).wasAcknowledged()
    }

}