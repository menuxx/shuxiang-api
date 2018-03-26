package com.menuxx.code.mongo

import com.menuxx.AllOpen
import com.menuxx.code.bean.SXItemCode
import com.menuxx.code.bean.SXItemCodeAssigned
import com.menuxx.code.bean.SXItemCodeConsumed
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

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
     * 获取一个批次的 code
     */
    fun loadBatchCode(batchId: Int) : Array<SXItemCode>? {
        val query = Query(Criteria.where("batchId").`is`(batchId))
        return dbTpl.find(query, SXItemCode::class.java)?.toTypedArray()
    }

    /**
     * 获取 xs_item_code 数据通过 base62 code
     */
    fun getItemCodeDataByCodeWithSalt(code: String, salt: String) : SXItemCode? {
        val query = Query(Criteria.where("code").`is`(code).andOperator(Criteria.where("salt").`is`(salt)))
        return dbTpl.findOne(query, SXItemCode::class.java)
    }

    /**
     * 给一个批次绑定 itemId
     */
    fun assignItemIdToBatchCode(batchId: Int, itemId: Int) : Boolean {
        val query = Query(Criteria.where("batchId").`is`(batchId))
        val update = Update()
                .set("status", SXItemCodeAssigned)
                .set("itemId", itemId)
        return dbTpl.updateMulti(query, update, SXItemCode::class.java).wasAcknowledged()
    }

    /**
     * 更新一个码到消费状态
     */
    fun updateCodeToConsume(code: String, salt: String, channel: String, userId: Int) : Boolean {
        val query = Query(Criteria.where("code").`is`(code).andOperator(Criteria.where("salt").`is`(salt)))
        val update = Update()
                .set("status", SXItemCodeConsumed)
                .set("userId", userId)
                .set("channel", channel)
        return dbTpl.updateFirst(query, update, SXItemCode::class.java).wasAcknowledged()
    }

    /**
     * 状态是否能到达
     */
    fun canToStatus(code: String, status: Int) : Boolean {
        val query = Query(Criteria.where("code").`is`(code))
        val itemCode = dbTpl.findOne(query, SXItemCode::class.java)
        return itemCode.status + 1 == status
    }

}