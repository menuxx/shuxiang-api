package com.menuxx.code.bean

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

val SXItemCodeCreated = 1       // 创建状态
val SXItemCodeExported = 2      // 导出状态
val SXItemCodeConsumed = 3      // 消费状态
val SXItemCodeBinded = 4        // 码绑定 item

fun getStatusTxt(status: Int) : String {
        return when (status) {
                SXItemCodeCreated -> "创建状态"
                SXItemCodeExported -> "导出状态"
                SXItemCodeConsumed -> "消费状态"
                else -> "位置状态"
        }
}

@Document(collection = "sx_item_code")
class SXItemCode(
        @Id
        val id: String?,
        val code: String,
        val salt: String,
        val batchId: Int,        // 批次编号，对应业务系统的 批次 id
        var exportTime: Date?,   // 导出时间
        var itemId: String?,      // 商品的id
        var userId: String?,      // 关联人的 id
        @CreatedDate
        var createAt: Date?,     // 创建时间
        @LastModifiedDate
        var updateAt: Date?,      // 更新时间
        var consumeTime: Date?,     // 消费时间
        var status: Int           // 状态
)