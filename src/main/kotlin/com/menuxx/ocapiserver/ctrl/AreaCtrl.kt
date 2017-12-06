package com.menuxx.ocapiserver.ctrl

import com.menuxx.ocapiserver.AllOpen
import com.menuxx.ocapiserver.db.AreaDb
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/4
 * 微信: yin80871901
 */
@AllOpen
@RestController
@RequestMapping("/areas")
class AreaCtrl(private val areaDb: AreaDb) {

    @GetMapping
    fun getProvinces() = areaDb.loadProvinces()

    @GetMapping("/{pId}")
    fun getAreaByPid(@PathVariable pId: Int) = areaDb.loadAreasByPid(pId)

}