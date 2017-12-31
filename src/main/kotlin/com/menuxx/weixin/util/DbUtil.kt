package com.menuxx.weixin.util

import org.jooq.Record
import org.jooq.TableRecord

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/22
 * 微信: yin80871901
 * 数据库工具
 */
/**
 * 空值跳过更新
 */
fun nullSkipUpdate(record: Record) : Record {
    record.fields().forEach { field ->
        if ( field.get(record) == null ) {
            record.changed(field, false)
        }
    }
    return record
}

fun <R : TableRecord<R>> nullSkipUpdate(record: TableRecord<R>) : TableRecord<R> {
    record.fields().forEach { field ->
        if ( field.get(record) == null ) {
            record.changed(field, false)
        }
    }
    return record
}