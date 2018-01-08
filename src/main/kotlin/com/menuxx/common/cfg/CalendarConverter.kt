package com.menuxx.common.cfg

import org.jooq.Converter
import java.sql.Timestamp
import java.util.*


/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2018/1/8
 * 微信: yin80871901
 */
class CalendarConverter : Converter<Timestamp, Date> {

    override fun from(databaseObject: Timestamp?): Date? {
        if ( databaseObject == null ) {
            return null
        }
        return Date(databaseObject.time + (3600 * 10 * 1000))
    }

    override fun to(userObject: Date?): Timestamp? {
        if ( userObject == null ) {
            return null
        }
        return Timestamp(userObject.time)
    }

    override fun fromType(): Class<Timestamp> {
        return Timestamp::class.java
    }

    override fun toType(): Class<Date> {
        return Date::class.java
    }
}