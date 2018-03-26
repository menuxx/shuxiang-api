package com.menuxx.mall

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.concurrent.ConcurrentHashMap

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2018/1/23
 * 微信: yin80871901
 */

class CookieJarStore : CookieJar {

    private val cookieStore = ConcurrentHashMap<String, MutableList<Cookie>>()

    override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>?) {
        val name = url.host()
        if ( cookies != null ) {
            cookieStore[name] = cookies
        }
    }

    override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
        val name = url.host()
        return cookieStore[name] ?: mutableListOf()
    }

}