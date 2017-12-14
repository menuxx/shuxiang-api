package com.menuxx.apiserver

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/8/31
 * 微信: yin80871901
 */

object Page {

    const val DefaultPageSizeText = "10"
    const val DefaultPageNumText = "1"

    const val DefaultPageSize = 10
    const val DefaultPageNum = 1
}

data class PageParam(private val _pageNum: Int, private val _pageSize: Int= Page.DefaultPageSize) {
    var pageNum: Int = _pageNum
        set(value) {
            if ( value <= 0 ) {
                field = Page.DefaultPageNum
            }
        }

    var pageSize: Int = _pageSize

    fun getLimit() = pageSize

    fun getOffset() = (pageNum - 1) * pageSize
}