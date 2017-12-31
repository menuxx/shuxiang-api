package com.menuxx.code.code

import org.springframework.stereotype.Component

@Component
class ItemCodeFactory {

    private val base62 = Base62()

    fun next(code: String) : String {
        return base62.encodeBase10(base62.decodeBase62(code) + 1)
    }

    fun offsetBit(code: String, count: Int) : String {
        return base62.encodeBase10(base62.decodeBase62(code) + count)
    }

}