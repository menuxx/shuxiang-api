package com.menuxx.apiserver.ctrl

import com.menuxx.common.db.ExpressDb
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/23
 * 微信: yin80871901
 */
@RestController
@RequestMapping("expresses")
class ExpressCtrl(private val expressDb: ExpressDb) {

    @GetMapping
    fun loadExpresses() = expressDb.loadExpresses()

}