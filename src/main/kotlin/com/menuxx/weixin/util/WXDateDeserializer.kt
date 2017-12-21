package com.menuxx.weixin.util

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.text.SimpleDateFormat
import java.util.*

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/21
 * 微信: yin80871901
 */

class WXDateDeserializer : JsonDeserializer<Date>() {

    private val WXDateFormart = SimpleDateFormat("yyyyMMddHHmmss")

    override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): Date {
        val dateStr = parser.readValueAs(String::class.java)
        return WXDateFormart.parse(dateStr)
    }

}