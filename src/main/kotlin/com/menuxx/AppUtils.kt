package com.menuxx

import com.menuxx.sso.auth.AuthUser
import com.menuxx.common.bean.User
import com.menuxx.common.bean.WXUser
import org.springframework.beans.BeanUtils
import org.springframework.security.core.context.SecurityContextHolder
import java.text.SimpleDateFormat
import java.util.*
import java.util.HashMap

fun imageUrlsFirst (urls: String) : String {
    return urls.split(":").first()
}

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/20
 * 微信: yin80871901
 */

fun getCurrentUser() : AuthUser {
    return SecurityContextHolder.getContext().authentication.principal as AuthUser
}

/**
 * 生成渠道订单的订单编号
 * 共32位
 * orderNoNow() 14
 * userId 7
 * channelId 6
 * genRandomString 5
 * 14 + 7 + 6 + 5 = 32
 */
fun genChannelOrderNo(userId: Int, channelId: Int) : String {
    val userIdStr = String.format("%07d", userId)
    val channelIdStr = String.format("%06d", channelId)
    val randomStr = genRandomString(5)
    return orderNoNow() + channelIdStr +  userIdStr + randomStr
}

fun genMallOrderNo(userId: Int) : String {
    val userIdStr = String.format("%07d", userId)
    val randomStr = genRandomString(5)
    return orderNoNow() +  userIdStr + randomStr
}

fun getQueryMap(query: String): Map<String, String> {
    val params = query.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    val map = HashMap<String, String>()
    for (param in params) {
        val name = param.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        val value = param.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        map.put(name, value)
    }
    return map
}

fun orderNoNow(): String {
    val date = Date(System.currentTimeMillis())
    val format = SimpleDateFormat("yyyyMMddHHmmss")
    return format.format(date)
}

fun formatWXTime(date: Date): String {
    val format = SimpleDateFormat("yyyyMMddHHmmss")
    return format.format(date)
}

fun formatWXTime(dateStr: String): Date {
    val format = SimpleDateFormat("yyyyMMddHHmmss")
    return format.parse(dateStr)
}

fun genRandomString32() = genRandomString(32)

fun genRandomString(length: Int): String {
    var _length = length
    val characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJLMNOPQRSTUVWXYZ1234567890"
    val result = StringBuilder()
    while (_length > 0) {
        val rand = Random()
        result.append(characters[rand.nextInt(characters.length)])
        _length--
    }
    return result.toString()
}

fun genRandomNumberString4() = genRandomNumberString(4)

fun genRandomNumberString(length: Int): String {
    var _length = length
    val characters = "1234567890"
    val result = StringBuilder()
    while (_length > 0) {
        val rand = Random()
        result.append(characters[rand.nextInt(characters.length)])
        _length--
    }
    return result.toString()
}

/**
 * 移除敏感数据
 */
fun removeSensitiveData(wxUser: WXUser) : WXUser {
    val newUer = WXUser()
    BeanUtils.copyProperties(wxUser, newUer)
    newUer.refreshToken = null
    newUer.unionid = null
    newUer.openid = null
    return newUer
}

fun removeSensitiveData(user: User) : User {
    val newUer = User()
    BeanUtils.copyProperties(user, newUer)
    newUer.wxUser = removeSensitiveData(newUer.wxUser)
    newUer.passwordToken = null
    newUer.lastLoginIp = null
    newUer.lastLoginTime = null
    return newUer
}