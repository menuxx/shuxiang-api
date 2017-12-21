package com.menuxx.weixin.util

import java.util.AbstractMap.SimpleImmutableEntry



/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/14
 * 微信: yin80871901
 */


fun getSex(sex: String) : Int {
    return try {
        Integer.parseInt(sex)
    } catch (ex: Exception) {
        0
    }
}