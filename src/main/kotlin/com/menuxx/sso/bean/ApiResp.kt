package com.menuxx.sso.bean

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/4
 * 微信: yin80871901
 */
data class ApiResp(val code: Int, val message: String)

data class ApiRespWithData<T>(val code: Int, val  message: String, val data: T?)