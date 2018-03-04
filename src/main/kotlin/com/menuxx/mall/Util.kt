package com.menuxx.mall

import com.menuxx.common.exception.AuthSessionException
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2018/1/23
 * 微信: yin80871901
 */

fun genAccountEmail(openid: String) : String {
    return openid + "@menuxx.com"
}

fun toStringMap(param: Map<String, Any?>) : HashMap<String, String> {
    val newMap = hashMapOf<String, String>()
    param.forEach { entry ->
        newMap[entry.key] = if (entry.value == null) "" else entry.value.toString()
    }
    return newMap
}

fun <T> getSessionValue(key: String) : T? {
    val servlet = RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes
    return servlet.request.session.getAttribute(key) as T?
}

fun <T> setSessionValue(key: String, value: T) {
    val servlet = RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes
    servlet.request.session.setAttribute(key, value)
}

val YHSdCustomerSessionKey = "__youhaosd_api_customer_session__"

fun getYhsdCustomerSession() = getSessionValue<String>(YHSdCustomerSessionKey) ?: throw AuthSessionException("友好速搭用户会话 session-id 找不到")

fun setYhsdCustomerSession(session: String) = setSessionValue(YHSdCustomerSessionKey, session)

