package com.menuxx.code

import com.menuxx.code.bean.SXItemCodeAssigned
import com.menuxx.code.bean.SXItemCodeConsumed
import com.menuxx.code.bean.SXItemCodeCreated
import java.util.regex.Pattern

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2018/1/17
 * 微信: yin80871901
 */

/**
 * 到达下一个状态
 */

fun codeNextStatus(currStatus: Int) : Int {
    return when (currStatus) {
        0 -> SXItemCodeCreated
        SXItemCodeCreated -> SXItemCodeAssigned
        SXItemCodeAssigned -> SXItemCodeConsumed
        else -> 4
    }
}

fun parseUrlPathCode(pathCode: String) : Pair<String, String> {
    val codeRegExp = Pattern.compile("~([a-zA-Z0-9]*)~([a-zA-Z0-9]*)")
    // 1 : 解析 itemCode 得到 code 和 salt
    val matcher = codeRegExp.matcher(pathCode)
    matcher.find()
    val code = matcher.group(1)
    val salt = matcher.group(2)
    return Pair(code, salt)
}