package com.menuxx.sso.ctrl

import com.menuxx.AllOpen
import com.menuxx.common.prop.QiNiuProps
import com.qiniu.util.Auth
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/5
 * 微信: yin80871901
 */
@AllOpen
@RestController
@RequestMapping("/upload")
class QiNiuCtrl (private val qiniuProps: QiNiuProps) {

    @GetMapping("/qn_uptoken")
    fun getUptoken() : Map<String, String> {
        val auth = Auth.create(qiniuProps.accessKey, qiniuProps.secretKey)
        val upToken = auth.uploadToken(qiniuProps.bucket)
        return mapOf("uptoken" to upToken)
    }

}