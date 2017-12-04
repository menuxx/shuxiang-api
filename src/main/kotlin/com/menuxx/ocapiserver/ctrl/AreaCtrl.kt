package com.menuxx.ocapiserver.ctrl

import com.menuxx.ocapiserver.bean.Area
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/4
 * 微信: yin80871901
 */
@RestController
@RequestMapping("/areas")
class AreaCtrl {

    @GetMapping
    fun getProvinces() : List<Area> {
        return listOf()
    }

    @GetMapping("/{pid}")
    fun getAreaByPid(@PathVariable pId: Int) : List<Area> {
        return listOf()
    }

}