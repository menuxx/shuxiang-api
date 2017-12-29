package com.menuxx.code.mongo

import com.menuxx.AllOpen
import com.menuxx.code.bean.SXItemCode
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

}